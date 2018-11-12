package com.aotain.ud1exec.processor;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.kafka.KafkaCustomer;
import com.aotain.common.utils.tools.CommonConstant;
import com.aotain.ud1exec.service.Ud1Processer;
import com.aotain.ud1exec.utils.CastUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class UdProcessor extends Thread{

    private Logger logger = LoggerFactory.getLogger(UdProcessor.class);
    /**
     * 消费者
     */
    private static KafkaCustomer customer = null;
    private static int threadnum = 1;


    public UdProcessor(String group){
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
                logger.info("zf ud Processor shutdown customer!");
            }
        });

    }

    public void run(){
        Ud1Processer ud1Processer = new Ud1Processer();
        customer.customer(CommonConstant.KAFKA_QUEUE_NAME_UD1LOG, threadnum, ud1Processer);
        logger.info("zf ud1 executor server start complete!");
    }
}
