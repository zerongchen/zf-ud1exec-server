package com.aotain.ud1exec.service.webflow;

import com.aotain.ud1exec.cache.CacheCreater;
import com.aotain.ud1exec.cache.FileCache;
import com.aotain.ud1exec.service.ExecBaseService;
import com.aotain.ud1exec.service.IUd1LogService;
import com.aotain.ud1exec.utils.DpiAttributeUtil;
import com.aotain.ud1exec.utils.JsonUtils;
import com.aotain.ud1exec.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class WebFlowService extends ExecBaseService {

    public WebFlowService() {
        super( new CacheCreater().new WebFlowCache());
    }

    protected boolean validateContent( String[] logContent ) {

        if(logContent.length != 8){
            logger.error(String.format("log length is %s,reuired length is %s",logContent.length,8));
            return false;
        }
        return true;
    }

    @Override
    protected String construct2FileKey( String enVender, String sendIp ) {
        return getPacketSubtype()+"+"+sendIp+"+"+Thread.currentThread().getId();
    }

    @Override
    protected String construct2logContent( String logContent, String probetype, String areaId, long receivedtime, String receivedIp, String sendIp, String enVender ) {
        return String.format("%s|%s|%s|%s|%s|%s|%s|%s",logContent,getPacketSubtype(),probetype,areaId,receivedtime,receivedIp,sendIp,enVender);
    }

}
