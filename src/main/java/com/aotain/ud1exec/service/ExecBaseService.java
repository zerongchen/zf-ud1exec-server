package com.aotain.ud1exec.service;

import com.aotain.ud1exec.cache.FileCache;
import com.aotain.ud1exec.utils.DpiAttributeUtil;
import com.aotain.ud1exec.utils.JsonUtils;
import com.aotain.ud1exec.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public abstract class ExecBaseService implements IUd1LogService {

    public Logger logger = LoggerFactory.getLogger(this.getClass());

    //暂时用于区分web流量统计 资源在内和资源在外
    protected abstract int getPacketSubtype();

    private FileCache fileCache;

    protected abstract boolean validateContent(String[] logContent);

    public ExecBaseService( FileCache fileCache){
        this.fileCache=fileCache;
    }
    /**
     * @param receivedtime 接收时间
     * @param receivedIp 接收ip
     * @param sendIp 发送ip
     * @param data 数据
     */
    public void execute( long receivedtime, String receivedIp, String sendIp, String data, String probetype ) {
        if ("SYNC_RUN".equals(data)) {
            fileCache.schedulerCheckElement();
        } else {
            logger.debug(String.format("deal message [ %s ]", data));

            String logContent = JsonUtils.getString(data, "logcontent");

            String[] logArray = logContent.split("\\|", 20);
            if (!validateContent(logArray)) {
                return;
            }
            String dthour = StringUtil.getDtHour(logArray[timePosition()]);

            String enVender = DpiAttributeUtil.getEuSoftwareProviderByIp(sendIp);
            String areaId = DpiAttributeUtil.getEuAreaIdByIp(sendIp);
            String file_key = construct2FileKey(enVender, sendIp);
            logContent = construct2logContent(logContent, probetype, areaId, receivedtime, receivedIp, sendIp, enVender);
            fileCache.addElement(dthour, file_key, logContent);
            fileCache.checkElement(dthour, file_key);

        }
    }

    protected int timePosition(){
        return 0;
    }
    protected String construct2FileKey(String enVender,String sendIp ){
        return sendIp+"+"+Thread.currentThread().getId();
    }

    protected String construct2logContent(String logContent,String probetype,String areaId, long receivedtime ,String receivedIp,String sendIp,String enVender){
        return String.format("%s|%s|%s|%s|%s|%s|%s",logContent,probetype,areaId,receivedtime,receivedIp,sendIp,enVender);
    }
}
