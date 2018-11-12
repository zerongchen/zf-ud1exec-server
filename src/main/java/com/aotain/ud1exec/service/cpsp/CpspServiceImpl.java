package com.aotain.ud1exec.service.cpsp;

import com.aotain.ud1exec.cache.CacheCreater;
import com.aotain.ud1exec.service.ExecBaseService;
import org.slf4j.LoggerFactory;

public class CpspServiceImpl extends ExecBaseService {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public CpspServiceImpl() {
        super(new CacheCreater().new CPSPCache());
    }

    protected int getPacketSubtype() {
        return 0;
    }

    protected boolean validateContent( String[] logArray ) {
        if(logArray.length != 11){
            logger.error(String.format("log length is %s,reuired length is %s",logArray.length,11));
            return false;
        }
        return true;
    }

}
