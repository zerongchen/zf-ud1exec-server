package com.aotain.ud1exec.processor;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.kafka.KafkaCustomer;
import com.aotain.common.utils.tools.CommonConstant;
import com.aotain.ud1exec.service.RadiusProcesser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RadiusProcessor extends Thread{

    private Logger logger = LoggerFactory.getLogger(RadiusProcessor.class);
    /**
     * 消费者
     */
    private static KafkaCustomer customer = null;
    private static int threadnum = 1;

    public RadiusProcessor( String group){
        //获得kafka消费配置
        Map<String, Object> conf = LocalConfig.getInstance().getKafkaCustomerConf();
        conf.put("rebalance.max.retries","5");
        conf.put("rebalance.backoff.ms","50000");
        conf.put("group.id", group);

        //开始消费kafka队列消息
        customer = new KafkaCustomer(conf);
        //线程数
        String h = LocalConfig.getInstance().getHashValueByHashKey("radius.customer.threadnum").trim();
        threadnum = Integer.parseInt(h);

        // 退出程序执行
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // 停止消费
                customer.shutdown();
                logger.info("zf radius executor server shutdown customer!");
            }
        });
    }
    public void run() {
        RadiusProcesser radiusProcesser = new RadiusProcesser();
        customer.customer(CommonConstant.KAFKA_QUEUE_NAME_RADIUS, threadnum, radiusProcesser);
        logger.info("zf customer of radius is working ...");
    }


}
