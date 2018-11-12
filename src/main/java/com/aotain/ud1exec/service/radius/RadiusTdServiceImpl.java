package com.aotain.ud1exec.service.radius;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.tools.Tools;
import com.aotain.ud1exec.cache.CacheCreater;
import com.aotain.ud1exec.cache.TdFileCache;
import com.aotain.ud1exec.model.RadiusModel;
import com.aotain.ud1exec.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RadiusTdServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(RadiusTdServiceImpl.class);
    private TdFileCache cache;

    public RadiusTdServiceImpl(){
        cache = new CacheCreater().new RadiusTdCache();
    }

    public void execute( RadiusModel model,int threadnum,int partition,String sync) {
        if ("sync".equals(sync)){
            cache.schedulerCheckElement();
        }else {
            //假的时间分区
            String dthour = "tmp";
            String file_key = construct2FileKey(partition,threadnum);
            String logContent = model.toTdObj();
            //加到缓存从而生成文件
            cache.addElement(dthour, file_key, logContent);
            cache.checkElement(dthour, file_key);
        }
    }

    /**
     * key is construct by 0x01+0x0300+002+[deploy.province]+[deploy.province.provider]+[threadNum%9+partition]+[yyyyMMddHHmmss]
     * @param partition
     * @param threadnum
     * @return
     */
    private String construct2FileKey(int partition,int threadnum){
        String dev ="";
        int prefix = (threadnum)%9;
        if (partition<10){
            dev=prefix+"0"+partition;
        }else if(partition>=10 && partition<100){
            dev=prefix+""+partition;
        }else{
            dev=""+partition;
        }
        return "0x01+0x0300+002+"+ LocalConfig.getInstance().getHashValueByHashKey("system.deploy.province.shortname")+"+"+
                LocalConfig.getInstance().getHashValueByHashKey("system.deploy.province.provider")+"+"+dev;
    }
}
