package com.aotain.ud1exec.service.webpush;

import com.aotain.ud1exec.cache.CacheCreater;
import com.aotain.ud1exec.cache.FileCache;
import com.aotain.ud1exec.service.ExecBaseService;
import org.slf4j.LoggerFactory;

public class WebPushServiceImpl extends ExecBaseService {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public WebPushServiceImpl() {
        super(new CacheCreater().new WebPushCache());
    }

    protected int getPacketSubtype() {
        return 0;
    }

    protected boolean validateContent( String[] logContent ) {

        if(logContent.length != 5){
            logger.error(String.format("log length is %s,reuired length is %s",logContent.length,5));
            return false;
        }
        return true;
    }

    @Override
    protected int timePosition() {
        return 3;
    }
}
