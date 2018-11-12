package com.aotain.ud1exec.cache;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.ud1exec.utils.CastUtil;
import com.aotain.ud1exec.utils.Constants;
import com.aotain.ud1exec.utils.FileUtils;
import com.aotain.ud1exec.utils.MultifileWriter;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TdFileCache {

    private MultifileWriter mfw = MultifileWriter.getInstance();

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 满多少条记录后就切换文件
     */
    private static int tdSyncCount = CastUtil.castInt(LocalConfig.getInstance().getHashValueByHashKey("ubas.td.maxrecord"),5000);

    private static int syncTimeInterval = CastUtil.castInt(LocalConfig.getInstance().getHashValueByHashKey("ubas.td.timeout"),60)*1000;
    /**
     * 缓存文件的路径。
     */
    private String cacheFilePath =null;
    private String td_path;
    private String cacheDirName;

    /**
     * 数据缓存 Map<yyyyMMddHH,Map<file_key, List<String>>>
     */
    private ThreadLocal local;

    private Map<String, Map<String, List<String>>> dataCacheMap = new ConcurrentHashMap<String, Map<String, List<String>>>();

    public TdFileCache(String CacheDirName,String td_path ){
        this.cacheDirName = CacheDirName;
        this.cacheFilePath = Constants.CACHE_PATH_ROOT+File.separator+this.cacheDirName;
        this.td_path = td_path;

        File cacheRootPath = new File(cacheFilePath);

        if (!cacheRootPath.exists()) {
            if (!cacheRootPath.mkdirs()) {
                logger.info(String.format("cachePath=%s,is created failed", cacheFilePath));
            }
        }
        initMap();
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//
//            public void run() {
//                schedulerCheckElement();
//            }
//        }, 100, syncTimeInterval);
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
            logger.error("init Map error",e);
            MonitorStatisticsUtils.addEvent(e);
        }
    }

    /**
     * <pre>
     * 向缓存中新增一条记录。
     * </pre>
     */
    public synchronized void addElement(String dthour, String file_key, String logcontent) {
        try {
//            dthour = dthour.substring(0,10);
            File cacheRootPath = new File(cacheFilePath);
            if(!cacheRootPath.exists()){
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
//                File keyPathFile = new File(keyPath);
//                if (!keyPathFile.exists()) {
//                    if (!keyPathFile.createNewFile()) {
//                        logger.warn(String.format("file %s is created failed", keyPath));
//                    }
//                }

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
            logger.error("add element error ",e);
            MonitorStatisticsUtils.addEvent(e);
        }
    }

    /**
     *
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
            logger.error("delete file error ",e);
        }
    }

    public void removeMap(String dthour) {
        try {
            if (dataCacheMap.containsKey(dthour)) {
                dataCacheMap.remove(dthour);
            }
            checkDirIsEmpty(dthour);
        } catch (Exception e) {
            logger.error("delete file error ",e);
        }
    }

    /**
     * <>
     *
     * @param dthour
     * @param file_key
     */
    public synchronized void checkElement(String dthour, String file_key) {

        try {
//            dthour = dthour.substring(0,10);
            if (dataCacheMap.containsKey(dthour)) {
                Map file_map = dataCacheMap.get(dthour);
                String pathPrefix = cacheFilePath + File.separator + dthour;
                if (file_map.containsKey(file_key)) {
                    List<String> list = (List<String>) file_map.get(file_key);
                    if (list.size() >= tdSyncCount) {
                        mfw.close(pathPrefix + File.separator + file_key);
                        removeMap(dthour, file_key);
                        storeData(td_path, dthour, file_key, list,cacheFilePath);
                    }
                    checkDirIsEmpty(dthour);
                }
            }
        } catch (Exception e) {
            logger.error("checkElement error ",e);
        }

    }

    /**
     * <pre>
     * 删除目录,先检查目录是否为空
     * </pre>
     *
     * @param dthour
     */
    private void checkDirIsEmpty(String dthour) {
        try {
            File dir = new File(cacheFilePath + "/" + dthour);
            File[] files = dir.listFiles();
            if (files!=null && files.length == 0)
                dir.delete();
        } catch (Exception e) {
            logger.debug(cacheFilePath + "/" + dthour+" dir no exit"+"e");
        }
    }

    /**
     * <pre>
     * 定时清理
     * </pre>
     *
     */
    public synchronized void schedulerCheckElement() {
        logger.debug(cacheDirName+" cache size={"+dataCacheMap.size()+"}");
        try {
            if (dataCacheMap.keySet().size() >= 1) {

//                Map<String,String> expireKeyValue = com.google.common.collect.Maps.newHashMap();
                /**
                 * 定期遍历map 将所有数据生成文件
                 */
                for (Map.Entry<String, Map<String, List<String>>> entryMap : dataCacheMap.entrySet()) {
                    String key = entryMap.getKey();

                    Map<String, List<String>> file_map_last = dataCacheMap.get(key);
                    String pathPrefix = cacheFilePath + File.separator + key;
                    for (Map.Entry<String, List<String>> entry : file_map_last.entrySet()) {
                        String fileNamekey = entry.getKey();
                        List<String> list = entry.getValue();
                        mfw.close(pathPrefix + File.separator + fileNamekey );
                        removeMap(key, fileNamekey);
                        removeMap(key);
                        storeData(td_path, key, fileNamekey, list,cacheFilePath);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("scheduler clear cache error",e);
        }

    }


    protected abstract void storeData( String tdDirName, String dthour, String fileNameKey, List<String> datas, String cachePath );


}
