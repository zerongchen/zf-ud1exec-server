package com.aotain.ud1exec.service.illegalroutes;

import com.aotain.ud1exec.cache.CacheCreater;
import com.aotain.ud1exec.cache.FileCache;
import com.aotain.ud1exec.service.ExecBaseService;
import com.aotain.ud1exec.service.IUd1LogService;
import com.aotain.ud1exec.utils.DpiAttributeUtil;
import com.aotain.ud1exec.utils.JsonUtils;
import com.aotain.ud1exec.utils.StringUtil;
import org.slf4j.LoggerFactory;


public class IllegalRoutesServiceImpl extends ExecBaseService {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public IllegalRoutesServiceImpl() {
        super(new CacheCreater().new IllegalRoutesCache());
    }

    protected int getPacketSubtype() {
        return 0;
    }


    protected boolean validateContent( String[] logContent ) {
        if(logContent.length != 6){
            logger.error(String.format("log length is %s,reuired length is %s",logContent.length,6));
            return false;
        }
        return true;
    }

}
