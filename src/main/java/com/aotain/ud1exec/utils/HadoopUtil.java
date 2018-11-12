package com.aotain.ud1exec.utils;

import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class HadoopUtil {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(HadoopUtil.class);
    private static FileSystem hadoopFS;

    static {
        try {
            FileSystem hadoopFS1;
            Configuration conf = new Configuration();
            conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
            conf.addResource(new Path("/etc/hadoop/conf/yarn-site.xml"));
            conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
            conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
            hadoopFS1 = FileSystem.get(conf);
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                String hdfsUri = "hdfs://192.168.50.204:8020";
                String hdfsUser = "root";
                hadoopFS1 = FileSystem.get(new URI(hdfsUri), new Configuration(), hdfsUser);
            }
            hadoopFS = hadoopFS1;
        } catch (Exception e) {
            logger.error("[zf-ud1exec-server][HadoopUtil] error ", e);
            MonitorStatisticsUtils.addEvent(e);
        }
    }

    /**
     * 将日志写到hdfs指定的目录。
     *
     * @param path     文件路径
     * @param logStr   日志
     * @param fileName 文件名称
     */
    private static boolean persistLog(String path, String logStr, String tempFilename, String fileName) {
        FSDataOutputStream os = null;
        try {
            if (!hadoopFS.exists(new Path(path))) {
                hadoopFS.mkdirs(new Path(path));
            }
            logger.debug("");
            os = hadoopFS.create(new Path(tempFilename));
            os.write(logStr.getBytes("UTF-8"));
            os.flush();
            boolean b = hadoopFS.rename(new Path(tempFilename), new Path(fileName));
            if (logger.isDebugEnabled()) {
                if (b) {
                    logger.info(String.format("file={%s} is create.", fileName));
                } else {
                    logger.error(String.format("file={%s},rename to file={%s} error", tempFilename, fileName));
                }
            }
            return b;
        } catch (Exception e) {
            logger.error("hadoop persist check the dest path is error", e);
            MonitorStatisticsUtils.addEvent(e);
            return false;
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (IOException e) {
                logger.error("IO close error.", e);
                MonitorStatisticsUtils.addEvent(e);
            }
        }

    }

    /**
     * @param fsPath hive表的路径
     * @param date   日期
     * @param hour   时间
     * @param prefix 文件名前缀
     */
    public synchronized static boolean writerHDFS(String fsPath, String date, String hour, List<String> list,
                                                  String prefix, String fileNameKey) {
        try {
            String logStr = listToStr(list);
            //数据条数
            int size = list.size();
            int logLength = logStr.length();
            if (logStr != null) {
                long time = System.currentTimeMillis() / 1000;
                String postfix = "txt";
                String tempFilename = String.format(".%s+%s+%s.%s", prefix, fileNameKey, time, postfix);
                String filename = String.format("%s+%s+%s.%s", prefix, fileNameKey, time, postfix);

                String path = fsPath + File.separator + date + File.separator + hour;
                boolean b = persistLog(path, logStr, path + File.separator + tempFilename, path + File.separator + filename);
                if (b) {
                    if (logger.isInfoEnabled()) {
                        logger.info(path + File.separator + filename + " [size:" + size + ",length:" + logLength + "]");
                    }
                } else {
                    logger.error(path + File.separator + filename + " [is create failure]");
                }
                return b;
            }
            return false;
        } catch (Exception e) {
            logger.error("Writer HDFS ERROR:", e);
            MonitorStatisticsUtils.addEvent(e);
            return false;
        }
    }

    /**
     * @param fsPath hive表路径
     * @param date   日期
     * @param prefix 文件名前缀
     */
    public static boolean writerHDFS(String fsPath, String date, List<String> list,
                                     String prefix, String fileNameKey) {
        try {
            String logStr = listToStr(list);
            long time = System.currentTimeMillis();
            String postfix = "txt";
            String tempFilename = String.format(".%s+%s+%s.%s", prefix, fileNameKey, time, postfix);
            String filename = String.format("%s+%s+%s.%s", prefix, fileNameKey, time, postfix);
            String path = fsPath + File.separator + date;
            return persistLog(path, logStr, path + File.separator + tempFilename, path + File.separator + filename);
        } catch (Exception e) {
            logger.error("Writer HDFS ERROR:", e);
            MonitorStatisticsUtils.addEvent(e);
            return false;
        }
    }

    private synchronized static String listToStr(List<String> list) {
        StringBuilder sb = new StringBuilder();
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (String str : list) {
            sb.append(str).append("\n");
        }
        return sb.toString();
    }

}
