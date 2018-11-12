package com.aotain.ud1exec.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.kafka.ICustomerCallback;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.ud1exec.service.appflow.AppFlowServiceImpl;
import com.aotain.ud1exec.service.cpsp.CpspServiceImpl;
import com.aotain.ud1exec.service.ddos.DDosAttackAreaServiceImpl;
import com.aotain.ud1exec.service.ddos.DDosAttackServiceImpl;
import com.aotain.ud1exec.service.illegalroutes.IllegalRoutesServiceImpl;
import com.aotain.ud1exec.service.share.ShareKWServiceImpl;
import com.aotain.ud1exec.service.share.ShareResultServiceImpl;
import com.aotain.ud1exec.service.traffic.TrafficServiceImpl;
import com.aotain.ud1exec.service.userapp.UserAppServiceImpl;
import com.aotain.ud1exec.service.webflow.WebInnerFlowServiceImpl;
import com.aotain.ud1exec.service.webflow.WebOuterFlowServiceImpl;
import com.aotain.ud1exec.service.webpush.WebPushServiceImpl;
import com.aotain.ud1exec.utils.CastUtil;
import com.aotain.ud1exec.utils.DpiAttributeUtil;
import com.aotain.ud1exec.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Ud1Processer implements ICustomerCallback {

    private static final SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHH");
    private Logger logger = LoggerFactory.getLogger(Ud1Processer.class);
    private static int syncTimeInterval = CastUtil.castInt(LocalConfig.getInstance().getHashValueByHashKey("ud1.hdfs.timeout"),30)*1000;

    private IUd1LogService appFlowServiceImpl;
    private IUd1LogService trafficServiceImpl;
    private IUd1LogService webInnerFlowService;
    private IUd1LogService webOuterFlowService;
    private IUd1LogService userAppService;
    private IUd1LogService ddosAttackService;
    private IUd1LogService ddosAttackAreaService;
    private IUd1LogService illegalRoutesService;
    private IUd1LogService webPushService;
    private IUd1LogService shareKWService;
    private IUd1LogService shareResultService;
    private IUd1LogService cpspService;

	public Ud1Processer(){
        appFlowServiceImpl = new AppFlowServiceImpl();
        trafficServiceImpl = new TrafficServiceImpl();
        webInnerFlowService = new WebInnerFlowServiceImpl();
        webOuterFlowService = new WebOuterFlowServiceImpl();
        userAppService = new UserAppServiceImpl();
        ddosAttackService = new DDosAttackServiceImpl();
        ddosAttackAreaService = new DDosAttackAreaServiceImpl();
        illegalRoutesService = new IllegalRoutesServiceImpl();
        webPushService = new WebPushServiceImpl();
        shareKWService = new ShareKWServiceImpl();
        shareResultService = new ShareResultServiceImpl();
        cpspService = new CpspServiceImpl();

        /**
         * scheduler to sync for hdfs file
         */
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                appFlowServiceImpl.execute(0l,null,null,"SYNC_RUN",null);
                trafficServiceImpl.execute(0l,null,null,"SYNC_RUN",null);
                webInnerFlowService.execute(0l,null,null,"SYNC_RUN",null);
                webOuterFlowService.execute(0l,null,null,"SYNC_RUN",null);
                userAppService.execute(0l,null,null,"SYNC_RUN",null);
                ddosAttackService.execute(0l,null,null,"SYNC_RUN",null);
                ddosAttackAreaService.execute(0l,null,null,"SYNC_RUN",null);
                illegalRoutesService.execute(0l,null,null,"SYNC_RUN",null);
                webPushService.execute(0l,null,null,"SYNC_RUN",null);
                shareKWService.execute(0l,null,null,"SYNC_RUN",null);
                shareResultService.execute(0l,null,null,"SYNC_RUN",null);
                cpspService.execute(0l,null,null,"SYNC_RUN",null);
            }
        }, syncTimeInterval, syncTimeInterval);
	}

    /**
     * 任务处理函数
     */
    public void callback(int threadnum, int partition, long offset, String message) {

        try{
            if(logger.isTraceEnabled()){
                logger.trace("start deal ud1 message! threadnum=" + threadnum + ", message=" + message);
            }

            JSONObject ud1log = JSON.parseObject(message);

            int packettype = JsonUtils.getInteger(ud1log,"packettype");
            int packetsubtype = JsonUtils.getInteger(ud1log,"packetsubtype");
            //上报时间，UTC时间戳
            long receivedtime = JsonUtils.getInteger(ud1log,"receivedtime");
            // 接收服务器ip
            String receivedip = JsonUtils.getString(ud1log,"receivedip");
            // 上报的服务器IP
            String sendip = JsonUtils.getString(ud1log,"sendip");
            //各UD数据，格式见各类上报数据章节定义
            String data = JsonUtils.getString(ud1log,"data");
            //0：代表DPI 1：代表EU
            int probetype = DpiAttributeUtil.getProbeTypeByIp(sendip);

            //通用流量类
           if(packettype == 0x01 && packetsubtype == 0x02){
           //    logger.info("appflow message:"+message);
               appFlowServiceImpl.execute(receivedtime,receivedip,sendip,data,probetype+"");
            //流量流向数据
           }else if(packettype == 0x01 && packetsubtype == 0xc4){
               trafficServiceImpl.execute(receivedtime, receivedip,sendip,data,probetype+"");
           }else if(packettype == 0x01 && packetsubtype == 0x05){
               logger.info(f.format(new Date())+":job_ubas_webflow message:"+message);
               //web流量统计(资源在内)
               webInnerFlowService.execute(receivedtime, receivedip,sendip,data,probetype+"");
           }else if(packettype == 0x01 && packetsubtype == 0x00){
               logger.info(f.format(new Date())+":job_ubas_webflow message:"+message);
               //web流量统计(资源在外)
               webOuterFlowService.execute(receivedtime, receivedip,sendip,data,probetype+"");
           }else if(packettype == 0x01 && packetsubtype == 0x03){
               //访问指定应用的用户统计
               userAppService.execute(receivedtime, receivedip,sendip,data,probetype+"");
           }else if(packettype == 0x01 && packetsubtype == 0xc0){
               //应用层DDoS异常流量分析功能模块
               ddosAttackService.execute(receivedtime, receivedip,sendip,data,probetype+"");
               ddosAttackAreaService.execute(receivedtime, receivedip,sendip,data,probetype+"");
           }else if(packettype == 0x01 && packetsubtype == 0x81){
               //非法路由检测分析
               illegalRoutesService.execute(receivedtime, receivedip,sendip,data,probetype+"");
           }else if(packettype == 0x01 && packetsubtype == 0x84){
               //推送信息统计
               webPushService.execute(receivedtime, receivedip,sendip,data,probetype+"");
           }else if(packettype == 0x01 && packetsubtype == 0x83){
            //   logger.info("job_ubas_share_kw,message:"+message);
               //一拖N用户行为分析（关键字段）
               shareKWService.execute(receivedtime, receivedip,sendip,data,probetype+"");
           }else if(packettype == 0x01 && packetsubtype == 0x82){
               //一拖N用户行为分析（检测结果）
               shareResultService.execute(receivedtime, receivedip,sendip,data,probetype+"");
           }else if(packettype == 0x01 && packetsubtype == 0xc1){
               //CP/SP 资源服务器分析上报模块
               cpspService.execute(receivedtime, receivedip,sendip,data,probetype+"");
           }

        }catch(Exception e){
            logger.error("deal ud1 message error! threadnum=" + threadnum + ", message=" + message, e);
            MonitorStatisticsUtils.addEvent(e);
        }
        
     }

    
}
