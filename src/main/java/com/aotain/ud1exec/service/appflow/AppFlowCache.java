package com.aotain.ud1exec.service.appflow;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.ud1exec.service.StorageThread;
import com.aotain.ud1exec.thread.ThreadUtil;
import com.aotain.ud1exec.utils.CastUtil;
import com.aotain.ud1exec.utils.Constants;
import com.aotain.ud1exec.utils.FileUtils;
import com.aotain.ud1exec.utils.MultifileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class AppFlowCache {

    private static Logger logger = LoggerFactory.getLogger(AppFlowCache.class);

    private static String hive_table_path = LocalConfig.getInstance().getHashValueByHashKey("hdfs.warehouse") + "job_ubas_appflow";

    private static MultifileWriter mfw = MultifileWriter.getInstance();
    // 满多少条记录后就同步到HDFS。
    private static int hdfsSyncCount = CastUtil.castInt(LocalConfig.getInstance().getHashValueByHashKey("ud1.hdfs.maxrecord"), 5000);

    /**
     * 缓存文件的路径。
     */
    private static String cacheFilePath = Constants.GENERAL_FLOW_PATH;

    /**
     * 数据缓存 Map<yyyyMMddHH,Map<HouseId+EU厂家+CU服务器IP, List<String>>>
     */
    private Map<String, Map<String, List<String>>> dataCacheMap = new ConcurrentHashMap<String, Map<String, List<String>>>();

    public AppFlowCache() {

        File cacheRootPath = new File(cacheFilePath);

        if (!cacheRootPath.exists()) {
            if (!cacheRootPath.mkdirs()) {
                logger.info(String.format("cachePath=%s,is created failed", cacheFilePath));
            }
        }
        initMap();
    }

    /**
     * <pre>
     * 系统初始化。。
     * </pre>
     */
    private void initMap() {
        File cacheRootPath = new File(cacheFilePath);
        try {
            if (logger.isInfoEnabled()) {
                logger.info("");
                logger.info("[init map start] ");
            }
            if (cacheRootPath.exists()) {
                File[] files_dthour = cacheRootPath.listFiles();
                if (files_dthour != null && files_dthour.length > 0) {
                    for (File dir : files_dthour) {

                        if (logger.isInfoEnabled()) {
                            logger.info("[init map directory] " + dir.getAbsolutePath());
                        }

                        String dt_hour = dir.getName();
                        File[] contentFile = dir.listFiles();
                        if (contentFile != null && contentFile.length > 0) {
                            for (File file : contentFile) {

                                if (logger.isInfoEnabled()) {
                                    logger.info("[init map file] " + file.getAbsolutePath());
                                }
                                String file_key = file.getName();
                                List<String> list = FileUtils.readFileByLines(file);

                                if (dataCacheMap.containsKey(dt_hour)) {
                                    dataCacheMap.get(dt_hour).put(file_key, list);
                                } else {
                                    Map<String, List<String>> file_map = new ConcurrentHashMap<String, List<String>>();
                                    file_map.put(file_key, list);
                                    dataCacheMap.put(dt_hour, file_map);
                                }
                            }
                        }else{
                            if (logger.isInfoEnabled()) {
                                String dirName=dir.getAbsolutePath();
                                if(dir.delete()){
                                    logger.info("[init map delete empty dir] "+dirName);
                                }
                            }
                        }
                    }
                }
                if (logger.isInfoEnabled()) {
                    for (Map.Entry<String, Map<String, List<String>>> e : dataCacheMap.entrySet()) {
                        String dthour = e.getKey();
                        for (String fileKey : e.getValue().keySet()) {
                            logger.info("[init map result] dthour:" + dthour + ",fileKey:" + fileKey);
                        }
                    }
                    logger.info("[init map end] ");
                    logger.info("");
                }
            }
        } catch (Exception e) {
            logger.error("init Map error", e);
            MonitorStatisticsUtils.addEvent(e);
        }
    }

    /**
     * <pre>
     * 向缓存中新增一条记录。
     * </pre>
     */
    public synchronized void addElement(String dthour, String file_key, String logcontent) {
        File cacheRootPath = new File(cacheFilePath);
        try {
            if (!cacheRootPath.exists()) {
                cacheRootPath.mkdirs();
            }
            if (cacheRootPath.exists()) {
                String filePath = cacheRootPath + File.separator + dthour;
                File datePath = new File(filePath);

                if (!datePath.exists()) {
                    if (!datePath.mkdir()) {
                        logger.info(String.format("path %s is created failed", filePath));
                    }
                }
                String keyPath = filePath + File.separator + file_key;
                mfw.writeLine(keyPath, logcontent);
                if (dataCacheMap.containsKey(dthour)) {
                    Map<String, List<String>> file_map = dataCacheMap.get(dthour);

                    if (file_map.containsKey(file_key)) {
                        file_map.get(file_key).add(logcontent);
                    } else {
                        List<String> list = new ArrayList<String>();
                        list.add(logcontent);
                        file_map.put(file_key, list);
                    }
                } else {
                    Map<String, List<String>> file_map = new ConcurrentHashMap<String, List<String>>();
                    List<String> list = new ArrayList<String>();
                    list.add(logcontent);
                    file_map.put(file_key, list);
                    dataCacheMap.put(dthour, file_map);
                }
            }

        } catch (Exception e) {
            logger.error("add element error ", e);
            MonitorStatisticsUtils.addEvent(e);
        }
    }


    /**
     * @param dthour   日期时间
     * @param file_key 缓存日志文件名
     */
    private void removeMap(String dthour, String file_key) {
        try {
            if (dataCacheMap.containsKey(dthour)) {
                Map file_map = dataCacheMap.get(dthour);
                if (file_map.containsKey(file_key)) {
                    file_map.remove(file_key);
                }
            }
        } catch (Exception e) {
            logger.error("delete file error ", e);
            MonitorStatisticsUtils.addEvent(e);
        }
    }

    public void removeMap(String dthour) {
        try {
            if (dataCacheMap.containsKey(dthour)) {
                dataCacheMap.remove(dthour);
            }
            checkDirIsEmpty(dthour);
        } catch (Exception e) {
            logger.error("delete file error ", e);
        }
    }

    /**
     * <>
     *
     * @param dthour   日期时间
     * @param file_key 缓存日志文件名
     */
    public synchronized void checkElement(String dthour, String file_key) {
        logger.debug("appflow cache size={" + dataCacheMap.size() + "}");
        try {
            if (dataCacheMap.containsKey(dthour)) {
                Map file_map = dataCacheMap.get(dthour);
                String pathPrefix = cacheFilePath + File.separator + dthour;
                if (file_map.containsKey(file_key)) {
                    List<String> list = (List<String>) file_map.get(file_key);
                    if (list.size() >= hdfsSyncCount) {
                        mfw.close(pathPrefix + File.separator + file_key);
                        removeMap(dthour, file_key);
                        if (list != null && list.size() > 0) {
                            StorageThread t = new StorageThread("GeneralFlowFile", hive_table_path, dthour, file_key, list, cacheFilePath);
                            ThreadUtil.getLongTimeOutThread(t);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("checkElement error ", e);
            MonitorStatisticsUtils.addEvent(e);
        }
    }

    /**
     * <pre>
     * 删除目录,先检查目录是否为空
     * </pre>
     *
     * @param dthour 日期时间
     */
    private static void checkDirIsEmpty(String dthour) {
        try {
            File dir = new File(cacheFilePath + File.separator + dthour);
            logger.info("checkDirIsEmpty: "+dir);
            if (dir != null && dir.exists()) {
                File[] files = dir.listFiles();
                if (files == null || files.length == 0) {
                    logger.info("prepared delete dir:"+dir.getAbsolutePath());
                    if (!dir.delete()) {
                        logger.warn(String.format("path %s is delete failed", dir));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("checkDirIsEmpty error ", e);
            MonitorStatisticsUtils.addEvent(e);
        }
    }

    /**
     * <pre>
     * 定时清理
     * </pre>
     */
    public synchronized void schedulerForCheckElement() {
        if (logger.isInfoEnabled()) {
            logger.info("");
            logger.info(" ----------- appflow timer before cache count -----------");
            for (Map.Entry<String, Map<String, List<String>>> e : dataCacheMap.entrySet()) {
                String dthour = e.getKey();
                for (String fileKey : e.getValue().keySet()) {
                    logger.info("[appflow cache] dthour:" + dthour + ",fileKey:" + fileKey);
                }
            }
            logger.info(" ----------- appflow timer before cache count -----------");
        }
        try {
            if (dataCacheMap.keySet().size() >= 1) {
                for (Entry<String, Map<String, List<String>>> entryMap : dataCacheMap.entrySet()) {
                    String key = entryMap.getKey();
                    Map<String, List<String>> file_map_last = dataCacheMap.get(key);
                    String pathPrefix = cacheFilePath + File.separator + key;
                    for (Entry<String, List<String>> entry : file_map_last.entrySet()) {
                        String fileNamekey = entry.getKey();
                        List<String> list = entry.getValue();
                        mfw.close(pathPrefix + File.separator + fileNamekey);
                        removeMap(key, fileNamekey);
                        removeMap(key);
                        if (list != null && list.size() > 0) {
                            StorageThread t = new StorageThread("GeneralFlowFile", hive_table_path, key, fileNamekey, list, cacheFilePath);
                            ThreadUtil.getLongTimeOutThread(t);
                        }
                    }
                }
            }
            if (logger.isInfoEnabled()) {
                logger.info(" ----------- appflow timer after count -----------");
                File cacheRootPath = new File(cacheFilePath);
                if (cacheRootPath.exists()) {
                    File[] files_dthour = cacheRootPath.listFiles();
                    if (files_dthour != null && files_dthour.length > 0) {
                        for (File dir : files_dthour) {
                            if (logger.isInfoEnabled()) {
                                logger.info("stat dir:" + dir.getAbsolutePath());
                            }
                            logger.info("delete dir:"+dir.getAbsolutePath());
                            if (!dir.delete()) {
                                logger.warn(String.format("path %s is delete failed", dir));
                            }
                        }
                    }
                }
                logger.info(" ----------- appflow timer after count -----------");
                logger.info("");
            }
        } catch (Exception e) {
            logger.error("timeForCheckElement error ", e);
            MonitorStatisticsUtils.addEvent(e);
        }
    }
}
