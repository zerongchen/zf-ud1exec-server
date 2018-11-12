package com.aotain.ud1exec.service.traffic;

import com.aotain.common.config.LocalConfig;
import com.aotain.ud1exec.service.StorageThread;
import com.aotain.ud1exec.thread.ThreadUtil;
import com.aotain.ud1exec.utils.CastUtil;
import com.aotain.ud1exec.utils.Constants;
import com.aotain.ud1exec.utils.FileUtils;
import com.aotain.ud1exec.utils.MultifileWriter;
import com.google.common.collect.Maps;
import com.sun.jersey.client.impl.CopyOnWriteHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class TrafficCache {
    private static Logger logger = LoggerFactory.getLogger(TrafficCache.class);

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");

    private static MultifileWriter mfw = MultifileWriter.getInstance();

    private static String hive_table_path = LocalConfig.getInstance().getHashValueByHashKey("hdfs.warehouse") + "job_ubas_traffic";

    // 满多少条记录后就同步到HDFS。
    private static int hdfsSyncCount = CastUtil.castInt(LocalConfig.getInstance().getHashValueByHashKey("ud1.hdfs.maxrecord"), 5000);

    private static int syncTimeInterval = CastUtil.castInt(LocalConfig.getInstance().getHashValueByHashKey("ud1.hdfs.timeout"), 30) * 1000;

    /**
     * 缓存文件的路径。
     */
    private static String cacheFilePath = Constants.CACHE_PATH_ROOT + File.separator + "job_ubas_traffic";
    ;

    /**
     * 数据缓存 Map<yyyyMMddHH,Map<DPI厂家+综分服务器IP, List<String>>>
     */
    private Map<String, Map<String, List<String>>> dataCacheMap = Maps.newConcurrentMap();

    public TrafficCache() {

        File cacheRootPath = new File(cacheFilePath);
        if (!cacheRootPath.exists()) {
            cacheRootPath.mkdir();
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
            if (cacheRootPath.exists()) {
                File[] files_dthour = cacheRootPath.listFiles();
                if (files_dthour != null && files_dthour.length > 0) {
                    for (File dir : files_dthour) {
                        String dt_hour = dir.getName();
                        File[] contentFile = dir.listFiles();
                        if (contentFile != null && contentFile.length > 0) {
                            for (File file : contentFile) {
                                String file_key = file.getName();
                                List<String> list = FileUtils.readFileByLines(file);
                                Map<String, List<String>> file_map = new ConcurrentHashMap<String, List<String>>();
                                file_map.put(file_key, list);
                                dataCacheMap.put(dt_hour, file_map);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("init map error ", e);
        }

    }

    /**
     * <pre>
     * 向缓存中新增一条记录。
     * </pre>
     */
    public void addElement(String dthour, String file_key, String logcontent) {
        File cacheRootPath = new File(cacheFilePath);
        try {
            if (!cacheRootPath.exists()) {
                cacheRootPath.mkdirs();
            }
            if (cacheRootPath.exists()) {
                String filePath = cacheRootPath + "/" + dthour;
                File datePath = new File(filePath);

                if (!datePath.exists())
                    datePath.mkdir();

                String keyPath = filePath + "/" + file_key;
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
            } else {
                throw new RuntimeException("file =" + cacheFilePath + " is not exists");
            }

        } catch (Exception e) {
            logger.error("addElement error,", e);
        }
    }


    /**
     * @param dthour
     * @param file_key
     */
    public void removeMap(String dthour, String file_key) {
        try {
            if (dataCacheMap.containsKey(dthour)) {
                Map file_map = dataCacheMap.get(dthour);
                if (file_map.containsKey(file_key)) {
                    file_map.remove(file_key);
                }
            }
        } catch (Exception e) {
            logger.error("delete file error ", e);
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
     * @param dthour
     * @param file_key
     */
    public void checkElement(String dthour, String file_key) {
        try {
            if (dataCacheMap.containsKey(dthour)) {
                Map file_map = dataCacheMap.get(dthour);
                String pathPrefix = cacheFilePath + File.separator + dthour;
                if (file_map.containsKey(file_key)) {
                    List<String> list = (List<String>) file_map.get(file_key);
                    if (list.size() >= hdfsSyncCount) {
                        mfw.close(pathPrefix + File.separator + file_key);
                        removeMap(dthour, file_key);
                        if (list != null && list.size() > 0){
                            Thread t = new StorageThread("FlowDirectionFile", hive_table_path, dthour, file_key, list, cacheFilePath);
                            ThreadUtil.getLongTimeOutThread(t);
                        }
                           // new StorageThread("FlowDirectionFile", hive_table_path, dthour, file_key, list, cacheFilePath).start();
                    }
                    checkDirIsEmpty(dthour);
                }
            }
        } catch (Exception e) {
            logger.error("checkElement error ", e);
        }

    }

    /**
     * <pre>
     * 删除目录,先检查目录是否为空
     * </pre>
     *
     * @param dthour
     */
    private static void checkDirIsEmpty(String dthour) {
        try {
            File dir = new File(cacheFilePath + "/" + dthour);
            if (dir != null && dir.exists()) {
                File[] files = dir.listFiles();
                if (files == null || files.length == 0)
                if (!dir.delete()) {
                    logger.warn(String.format("path %s is delete failed", dir));
                }
            }
        } catch (Exception e) {
            logger.error("checkDirIsEmpty error ", e);
        }
    }

    /**
     * <pre>
     * 定时清理
     * </pre>
     */
    public void schedulerCheckElement() {
        logger.debug("traffic cache size={" + dataCacheMap.size() + "}");
        try {
            if (dataCacheMap.keySet().size() >= 1) {

//				Map<String,String> expireKeyValue = Maps.newHashMap();
                /**
                 * 遍历map 将除当前小时之外的数全部入库。
                 */
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
                        if (list != null && list.size() > 0){
                            Thread t = new StorageThread("FlowDirectionFile", hive_table_path, key, fileNamekey, list, cacheFilePath);
                            ThreadUtil.getLongTimeOutThread(t);
                        }


                         //   new StorageThread("FlowDirectionFile", hive_table_path, key, fileNamekey, list, cacheFilePath).start();
//						expireKeyValue.put(key,fileNamekey);
                    }
                }

//				for(Entry<String,String> expireKeyValueEntry:expireKeyValue.entrySet()){
//					removeMap(expireKeyValueEntry.getKey(), expireKeyValueEntry.getValue());
//				}
//
//				for(String expiredKey:expireKeyValue.keySet()){
//					removeMap(expiredKey);
//				}
            }
        } catch (Exception e) {
            logger.error("schedulerCheckElement error ", e);
        }

    }
}
