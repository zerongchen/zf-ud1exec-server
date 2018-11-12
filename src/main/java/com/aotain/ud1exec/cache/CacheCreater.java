package com.aotain.ud1exec.cache;

import com.aotain.ud1exec.service.CreateTdFileThread;
import com.aotain.ud1exec.service.StorageThread;
import com.aotain.ud1exec.thread.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CacheCreater {


    public class WebFlowCache extends FileCache{

        private Logger LOG = LoggerFactory.getLogger(WebFlowCache.class);

        public WebFlowCache() {
            super("job_ubas_webflow");
        }

        protected void storeData(String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath ) {
            if(datas!=null && datas.size()>0) {
                StorageThread t = new StorageThread("WebFlowFile", hive_table_path, dthour, fileNameKey, datas, cachePath);
                try {
                    ThreadUtil.getLongTimeOutThread(t);
                } catch (Exception e) {
                    LOG.error("",e);
                }
            }
        }
    }

    /**
     * 指定应用用户统计
     */
    public class UserAppCache extends FileCache{
        private Logger LOG = LoggerFactory.getLogger(UserAppCache.class);
        public UserAppCache() {
            super("job_ubas_userapp");
        }

        protected void storeData(  String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath ) {
            if(datas!=null && datas.size()>0) {
                Thread t= new StorageThread("UserAppFile", hive_table_path, dthour, fileNameKey, datas, cachePath);
                try {
                    ThreadUtil.getLongTimeOutThread(t);
                } catch (Exception e) {
                    LOG.error("",e);
                }
            }
        }
    }

    /**
     * DDOS应用层数据信息
     */
    public class DDosAttackCache extends FileCache{
        private Logger LOG = LoggerFactory.getLogger(DDosAttackCache.class);
        public DDosAttackCache() {
            super("job_ubas_ddos");
        }

        protected void storeData(  String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath ) {
            if(datas!=null && datas.size()>0){
                Thread t= new StorageThread("DDosFile", hive_table_path, dthour, fileNameKey, datas, cachePath);
                try {
                    ThreadUtil.getLongTimeOutThread(t);
                } catch (Exception e) {
                    LOG.error("",e);
                }
            }
        }
    }

    /**
     * DDOS异常信息
     */
    public class DDosAttackAreaCache extends FileCache{
        private Logger LOG = LoggerFactory.getLogger(DDosAttackAreaCache.class);
        public DDosAttackAreaCache() {
            super("job_ubas_ddos_area");
        }

        protected void storeData(  String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath ) {
         //   new StorageThread("DDosAreaFile",hive_table_path, dthour, fileNameKey, datas,cachePath).start();

            Thread t= new StorageThread("DDosAreaFile", hive_table_path, dthour, fileNameKey, datas, cachePath);
            try {
                ThreadUtil.getLongTimeOutThread(t);
            } catch (Exception e) {
                LOG.error("",e);
            }
        }
    }

    /**
     * 非法路由流量统计
     */
    public class IllegalRoutesCache extends FileCache{
        private Logger LOG = LoggerFactory.getLogger(IllegalRoutesCache.class);
        public IllegalRoutesCache() {
            super("job_ubas_illegalroutes");
        }

        protected void storeData(  String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath ) {
            if(datas!=null && datas.size()>0){
                Thread t= new StorageThread("IllegalRoutesFile", hive_table_path, dthour, fileNameKey, datas, cachePath);
                try {
                    ThreadUtil.getLongTimeOutThread(t);
                } catch (Exception e) {
                    LOG.error("",e);
                }
            }
         //   new StorageThread("IllegalRoutesFile",hive_table_path, dthour, fileNameKey, datas,cachePath).start();
        }
    }
    /**
     * WEB 推送
     */
    public class WebPushCache extends FileCache{
        private Logger LOG = LoggerFactory.getLogger(WebPushCache.class);
        public WebPushCache() {
            super("job_ubas_webpush");
        }

        protected void storeData(  String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath ) {
            if(datas!=null && datas.size()>0){
                Thread t= new StorageThread("WebPushFile", hive_table_path, dthour, fileNameKey, datas, cachePath);
                try {
                    ThreadUtil.getLongTimeOutThread(t);
                } catch (Exception e) {
                    LOG.error("",e);
                }
            }
        //    new StorageThread("WebPushFile",hive_table_path, dthour, fileNameKey, datas,cachePath).start();
        }
    }

    /**
     * 1拖N 关键字
     */
    public class ShareKWCache extends FileCache{
        private Logger LOG = LoggerFactory.getLogger(ShareKWCache.class);
        public ShareKWCache() {
            super("job_ubas_share_kw");
        }

        protected void storeData(  String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath ) {
            if(datas!=null && datas.size()>0){
                Thread t= new StorageThread("ShareKwFile", hive_table_path, dthour, fileNameKey, datas, cachePath);
                try {
                    ThreadUtil.getLongTimeOutThread(t);
                } catch (Exception e) {
                    LOG.error("",e);
                }
            }
          //  new StorageThread("ShareKwFile",hive_table_path, dthour, fileNameKey, datas,cachePath).start();
        }
    }
    /**
     * 1拖N 检测结果
     */
    public class ShareResultCache extends FileCache{
        private Logger LOG = LoggerFactory.getLogger(ShareResultCache.class);
        public ShareResultCache() {
            super("job_ubas_share_result");
        }

        protected void storeData(  String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath ) {
            if(datas!=null && datas.size()>0){

                Thread t= new StorageThread("ShareResultFile", hive_table_path, dthour, fileNameKey, datas, cachePath);
                try {
                    ThreadUtil.getLongTimeOutThread(t);
                } catch (Exception e) {
                    LOG.error("",e);
                }
            }
         //   new StorageThread("ShareResultFile",hive_table_path, dthour, fileNameKey, datas,cachePath).start();
        }
    }

    /**
     * CPSP
     */
    public class CPSPCache extends FileCache{
        private Logger LOG = LoggerFactory.getLogger(CPSPCache.class);
        public CPSPCache() {
            super("job_ubas_cpsp");
        }

        protected void storeData(  String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath ) {
            if(datas!=null && datas.size()>0){
                Thread t= new StorageThread("CpspFile", hive_table_path, dthour, fileNameKey, datas, cachePath);
                try {
                    ThreadUtil.getLongTimeOutThread(t);
                } catch (Exception e) {
                    LOG.error("",e);
                }
            }
         //   new StorageThread("CpspFile",hive_table_path, dthour, fileNameKey, datas,cachePath).start();
        }
    }

    /**
     * job_radius_log
     */
    public class RadiusCache extends FileCache{
        private Logger LOG = LoggerFactory.getLogger(RadiusCache.class);
        public RadiusCache() {
            super("job_radius_log");
        }

        protected void storeData(  String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath ) {
            if(datas!=null && datas.size()>0){

                Thread t= new StorageThread("RadiusFile", hive_table_path, dthour, fileNameKey, datas, cachePath);
                try {
                    ThreadUtil.getLongTimeOutThread(t);
                } catch (Exception e) {
                    LOG.error("",e);
                }
            }
          //  new StorageThread("RadiusFile",hive_table_path, dthour, fileNameKey, datas,cachePath).start();
        }
    }


    /**
     * td radius
     */
    public class RadiusTdCache extends TdFileCache{
        private Logger LOG = LoggerFactory.getLogger(RadiusTdCache.class);
        public RadiusTdCache() {
            super("radius","radius");
        }

        protected void storeData(  String td_path, String dthour, String fileNameKey, List<String> datas, String cachePath ) {
            if(datas!=null && datas.size()>0){
                Thread t= new CreateTdFileThread(td_path,dthour,fileNameKey,datas,cachePath);
                try {
                    ThreadUtil.getLongTimeOutThread(t);
                } catch (Exception e) {
                    LOG.error("",e);
                }

            //    new CreateTdFileThread(td_path,dthour,fileNameKey,datas,cachePath).start();
            }
        }
    }
}
