package com.aotain.ud1exec.stream;


import com.aotain.common.config.LocalConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/spring-base.xml"})
public class kafkaStreamCount {


    //获得kafka消费配置
    static Map<String, Object> conf = LocalConfig.getInstance().getKafkaProducerConf();

    @Test
    public void count(){

    }
}
