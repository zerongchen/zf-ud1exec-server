package com.aotain.ud1exec.service.share;

import com.aotain.ud1exec.cache.CacheCreater;
import com.aotain.ud1exec.cache.FileCache;
import com.aotain.ud1exec.service.ExecBaseService;
import org.slf4j.LoggerFactory;

public class ShareResultServiceImpl extends ExecBaseService {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public ShareResultServiceImpl() {
        super(new CacheCreater().new ShareResultCache());
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
}
