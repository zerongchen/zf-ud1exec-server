package com.aotain.ud1exec;

import com.aotain.common.utils.kafka.KafkaCustomer;
import com.aotain.common.utils.monitorstatistics.ModuleConstant;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.ud1exec.processor.RadiusProcessor;
import com.aotain.ud1exec.processor.UdProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    /**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(Main.class);
   
    /**
     * 消费者
     */
    private static KafkaCustomer customer = null;
    private static ExecutorService threadPool =null;
    private static int threadnum = 1;
    /**
     * kafka消费组IDs
     */
    private static String KAFKA_JOBQUEUE_GROUP_ID = "zf_ud1exec_server";

    private static String KAFKA_RADUIS_GROUP_ID = "zf_radius_comsumers";

    /**
     * spring 容器
     */
    private static ApplicationContext applicationContext;
    
    /**
     * 启动方法
     * 
     * @param args
     *  输入参数
     */
    public static void main(String[] args) {
    	applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/spring-base.xml");
        MonitorStatisticsUtils.initModuleNoThread(ModuleConstant.MODULE_UD1_EXEC_DATA);

      //  threadPool= Executors.newCachedThreadPool();
        threadPool= Executors.newFixedThreadPool(2);
        threadPool.execute(new UdProcessor(KAFKA_JOBQUEUE_GROUP_ID));
        threadPool.execute(new RadiusProcessor(KAFKA_RADUIS_GROUP_ID));

        // 退出程序执行
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // 停止消费
                threadPool.shutdown();
                logger.info("zf Processor shutdown customer!");
            }
        });
    }
}
