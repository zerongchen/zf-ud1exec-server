package com.aotain.ud1exec.service.appflow;

import com.aotain.ud1exec.service.IUd1LogService;
import com.aotain.ud1exec.utils.DpiAttributeUtil;
import com.aotain.ud1exec.utils.JsonUtils;
import com.aotain.ud1exec.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppFlowServiceImpl implements IUd1LogService {

    private Logger logger = LoggerFactory.getLogger(AppFlowServiceImpl.class);

    private AppFlowCache generalFlowCache = new AppFlowCache();

    public AppFlowServiceImpl(){
    }
    /**
     * @param receivedtime 接收时间
     * @param receivedIp 接收ip
     * @param sendIp 发送ip
     * @param data 数据
     */
    public void execute(long receivedtime, String receivedIp, String sendIp, String data,String probetype) {


        if ("SYNC_RUN".equals(data)){
            generalFlowCache.schedulerForCheckElement();
        }else {
            logger.debug(String.format("deal message [ %s ]", data));

            String logContent = JsonUtils.getString(data, "logcontent");
            if (!"unkown".equals(logContent)) {
                String[] logArray = logContent.split("\\|", 20);
                if (logArray.length != 12) {
                    logger.error(String.format("log length is %s,reuired length is %s", logArray.length, 12));
                }
                String dthour = StringUtil.getDtHour(logArray[0]);
                // 软件厂家编号
                String enVender = DpiAttributeUtil.getEuSoftwareProviderByIp(sendIp);
                //区域 [DPI：区域 EU：机房]
                String areaId = DpiAttributeUtil.getEuAreaIdByIp(sendIp);
                //文件key=软件厂家编号+区域+CU服务器IP
                String file_key = areaId + "+" + sendIp + "+" + Thread.currentThread().getId();
                logContent = String.format("%s|%s|%s|%s|%s|%s|%s", logContent, probetype, areaId, receivedtime, receivedIp, sendIp, enVender);
                generalFlowCache.addElement(dthour, file_key, logContent);
                generalFlowCache.checkElement(dthour, file_key);
            }
        }
    }

   /* public String getDtHour(String utcTime){
        if(StringUtils.isBlank(utcTime)||utcTime.length() != 10){
            return "";
        }
        for(int i=0;i<utcTime.length();i++){
            Character c = utcTime.charAt(i);
            if(!Character.isDigit(c)){
                return "";
            }
        }
        Date date = new Date(Long.valueOf(utcTime)*1000);
        return Constants.formatter.format(date);
    }*/

}
