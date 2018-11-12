package com.aotain.ud1exec.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.kafka.ICustomerCallback;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.ud1exec.service.radius.RadiusService;
import com.aotain.ud1exec.service.radius.RadiusServiceImpl;
import com.aotain.ud1exec.utils.CastUtil;
import com.aotain.ud1exec.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class  RadiusProcesser implements ICustomerCallback {

    private Logger logger = LoggerFactory.getLogger(RadiusProcesser.class);
    private static int syncTimeInterval = CastUtil.castInt(LocalConfig.getInstance().getHashValueByHashKey("ud1.hdfs.timeout"),30)*1000;

    private RadiusService radiusService;


	public RadiusProcesser(){
        radiusService = new RadiusServiceImpl();

        /**
         * scheduler to sync for hdfs file
         */
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                radiusService.execute(0,0l,null,0,"SYNC_RUN",0);
            }
        }, syncTimeInterval, syncTimeInterval);


	}

    /**
     * 任务处理函数
     */
    public void callback(int threadnum, int partition, long offset, String message) {

        try{
            logger.trace("start deal radius message! threadnum=" + threadnum + ", message=" + message);
            
            JSONObject radiusLog = JSON.parseObject(message);
            int type = JsonUtils.getInteger(radiusLog,"type");
            //Base64 字符串
            String data = JsonUtils.getString(radiusLog,"data");
            Long createtime = (long)JsonUtils.getInteger(radiusLog,"createtime");
            //上报时间，UTC时间戳
            String createip = JsonUtils.getString(radiusLog,"createip");
            if(type==1){
                radiusService.execute(type,createtime,createip,threadnum,data,partition);
            }
        }catch(Exception e){
            logger.error("deal radius message error! threadnum=" + threadnum + ", message=" + message, e);
            MonitorStatisticsUtils.addEvent(e);
        }
        
    }

    
}
