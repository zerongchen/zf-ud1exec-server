package com.aotain.ud1exec.service.ddos;

import com.aotain.ud1exec.cache.CacheCreater;
import com.aotain.ud1exec.service.ExecBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDosAttackAreaServiceImpl extends ExecBaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public DDosAttackAreaServiceImpl( ) {
        super(new CacheCreater().new DDosAttackAreaCache());
    }

    protected int getPacketSubtype() {
        return 0;
    }

    protected boolean validateContent( String[] logContent ) {
        if(logContent.length !=8){
            if(logContent.length==7){
                return false;
            }
            logger.error(String.format("log length is %s,ddos reuired length is %s",logContent.length,8));
            return false;
        }
        return true;
    }

}
