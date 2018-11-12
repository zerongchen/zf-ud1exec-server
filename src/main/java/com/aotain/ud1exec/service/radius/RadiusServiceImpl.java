package com.aotain.ud1exec.service.radius;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.tools.Tools;
import com.aotain.ud1exec.cache.CacheCreater;
import com.aotain.ud1exec.cache.FileCache;
import com.aotain.ud1exec.model.RadiusModel;
import com.aotain.ud1exec.utils.CastUtil;
import com.aotain.ud1exec.utils.StringUtil;
import com.aotain.ud1exec.utils.UcParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class RadiusServiceImpl implements RadiusService {

    private static Logger logger = LoggerFactory.getLogger(RadiusServiceImpl.class);

    private FileCache cache;
    private RadiusTdServiceImpl radiusTdServiceImpl;
    //生成TD文件的时间
    private static int syncTimeInterval = CastUtil.castInt(LocalConfig.getInstance().getHashValueByHashKey("ubas.td.timeout"),60)*1000;


    public RadiusServiceImpl(){
        cache = new CacheCreater().new RadiusCache();
        radiusTdServiceImpl = new RadiusTdServiceImpl();

        /**
         * scheduler to sync for td file
         */
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                radiusTdServiceImpl.execute(null,0,0,"sync");
            }
        }, syncTimeInterval, syncTimeInterval);
    }

//    @Override
    public void execute( Integer type,long createtime,String createip,int threadnum,String data,int partition ) {

        if ("SYNC_RUN".equals(data)){
            cache.schedulerCheckElement();
        }else {

            RadiusModel model = UcParseUtil.pasrseToRadiusModel(UcParseUtil.decodeBase64(data));
            if (model == null) {
                return;
            }
            model.setCreateTime(createtime);
            model.setCreateIp(createip);
            String dthour = StringUtil.getDtHour(String.valueOf(model.getEventTimestamp()));
            String file_key = construct2FileKey();
            String logContent = model.toHdfsObj();
            cache.addElement(dthour, file_key, logContent);
            cache.checkElement(dthour, file_key);
            //生成TD 文件
            if (model.getAcctStatusType() == 1 || model.getAcctStatusType()==3) {
                radiusTdServiceImpl.execute(model, threadnum, partition,null);
            }
        }
    }
    //RadiusFile +192.168.1.100+1+1513768013.txt
    private String construct2FileKey(){
        return Tools.getHostAddress()+"+"+Thread.currentThread().getId();
    }
}
