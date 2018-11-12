package com.aotain.ud1exec.service.ddos;

import com.aotain.ud1exec.cache.CacheCreater;
import com.aotain.ud1exec.service.ExecBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDosAttackServiceImpl extends ExecBaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public DDosAttackServiceImpl() {
        super(new CacheCreater().new DDosAttackCache());
    }

    protected int getPacketSubtype() {
        return 0;
    }

    protected boolean validateContent( String[] logContent ) {
        if(logContent.length !=7){
            if(logContent.length ==8){
                return false;
            }
            logger.error(String.format("log length is %s,ddos app log reuired length is %s",logContent.length,7));
            return false;
        }
        return true;
    }

}
