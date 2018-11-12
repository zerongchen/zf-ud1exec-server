package com.aotain.ud1exec.general_flow;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.kafka.KafkaProducer;
import com.aotain.common.utils.tools.CommonConstant;
import com.aotain.ud1exec.stream.util.KafkaProducerUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/spring-base.xml"})

public class KafkaProducer4Test {


    static int count = 1;

    @Test
    public void producer(){

        //获得kafka消费配置
        Map<String, Object> conf = LocalConfig.getInstance().getKafkaProducerConf();
//        org.apache.kafka.clients.producer.KafkaProducer producer = KafkaProducerUtil.getProducer();
        Random random =new Random(100);

        List<String> list = new ArrayList<String>();
        for (int j=0;j<0;j++){
            for (int i =0;i<10;i++){
                Long times = (System.currentTimeMillis()-100000)/1000+new Random().nextInt(10)*i-i;
                int type = new Random().nextInt(2);
                //web flow
                String webFlow = "{\"data\":{\"logcontent\":\""+times+"|"+times+"|10|8|3|appname|9|460\"},\"packetsubtype\":"+type*5+",\"packettype\":1,\"probetype\":1,\"receivedip\":\"192.168.10.10\",\"receivedtime\":1523251910,\"sendip\":\"192.168.50.190\"}";
                //CPSP
                String cpsp = "{\"data\":{\"logcontent\":\""+times+"|"+times+"|10|"+new Random().nextInt(3)+"|1|appname|9|3987|4541|4|HJDD+"+random.nextInt(5)+"\"},\"packetsubtype\":193,\"packettype\":1,\"probetype\":1,\"receivedip\":\"192.168.10.10\",\"receivedtime\":1523351810,\"sendip\":\"12.3.3.8\"}";
                //ddos
                String ddos = "{\"data\":{\"logcontent\":\""+times+"|"+times+"|10|"+random.nextInt(3)+"|1|52\"},\"packetsubtype\":192,\"packettype\":1,\"probetype\":1,\"receivedip\":\"192.168.10.10\",\"receivedtime\":1523351810,\"sendip\":\"12.3.3.8\"}";
                String ddosarea = "{\"data\":{\"logcontent\":\""+times+"|"+times+"|10|"+random.nextInt(3)+"|DSS|110|1|52\"},\"packetsubtype\":192,\"packettype\":1,\"probetype\":1,\"receivedip\":\"192.168.10.10\",\"receivedtime\":1523351810,\"sendip\":\"12.3.3.8\"}";
                //illegal
                String illegal = "{\"data\":{\"logcontent\":\""+times+"|"+times+"|10.10.1."+random.nextInt(256)+"|123|222|SDD"+random.nextInt(20)+"\"},\"packetsubtype\":129,\"packettype\":1,\"probetype\":1,\"receivedip\":\"192.168.10.10\",\"receivedtime\":1523351810,\"sendip\":\"12.3.3.8\"}";

                //kw
                String sharekw = "{\"data\":{\"logcontent\":\""+times+"|"+times+"|10.10.1."+random.nextInt(256)+"|15.10.1."+random.nextInt(256)+"|822422"+random.nextInt(256)+"|158.1.1."+random.nextInt(20)+"|asdsadasd."+random.nextInt(20)+"|DDSD|SDSA\"},\"packetsubtype\":131,\"packettype\":1,\"probetype\":1,\"receivedip\":\"192.168.10.10\",\"receivedtime\":1523351810,\"sendip\":\"12.3.3.8\"}";
                //shareresult
                String shareresult = "{\"data\":{\"logcontent\":\""+times+"|"+times+"|10.10.1."+random.nextInt(256)+"|15.10.1."+random.nextInt(256)+"|"+random.nextInt(100)+"\"},\"packetsubtype\":130,\"packettype\":1,\"probetype\":1,\"receivedip\":\"192.168.10.10\",\"receivedtime\":1523351810,\"sendip\":\"12.3.3.8\"}";

                //userapp
                String userapp = "{\"data\":{\"logcontent\":\""+times+"|"+times+"|"+random.nextInt(20)+"|"+random.nextInt(20)+"|TEST+"+random.nextInt(20)+"|"+random.nextInt(4)+"|DSDS|460\"},\"packetsubtype\":3,\"packettype\":1,\"probetype\":1,\"receivedip\":\"192.168.10.10\",\"receivedtime\":1523251910,\"sendip\":\"192.168.50.190\"}";
                //webpush
                String webpush = "{\"data\":{\"logcontent\":\""+random.nextInt(100)+"|10.10.1."+random.nextInt(256)+"|"+(count++)+"|"+times+"|www.test.dd"+random.nextInt(22)+"\"},\"packetsubtype\":132,\"packettype\":1,\"probetype\":1,\"receivedip\":\"192.168.10.10\",\"receivedtime\":1523351810,\"sendip\":\"12.3.3.8\"}";

                list.add(webFlow);
                list.add(cpsp);
                list.add(ddos);
                list.add(ddosarea);
                list.add(illegal);
                list.add(sharekw);
                list.add(shareresult);
                list.add(userapp);
                list.add(webpush);
            }
//        boolean b = producer.sendOffsetsToTransaction(CommonConstant.KAFKA_QUEUE_NAME_UD1LOG, list);

//        System.out.println("---------status is,"+b+"======="+list.size());
            list.clear();
        }
    }

    @Test
    public void producerBase64(){

        //获得kafka消费配置
        Map<String, Object> conf = LocalConfig.getInstance().getKafkaProducerConf();
        KafkaProducer producer = new KafkaProducer(conf);

        List<String> list = new ArrayList<String>();
        for (int i=0;i<1000;i++){
            String s= "{\"createip\":\"192.168.50.156\",\"createtime\":1526889962,\"data\":\"EENVQ4EAAAAAAAAAMaoAAABoAQpseF8xODYyMQgGAABIvCAFU1otKAYAAAADLAMBNwZbAn3qGjJ4\r\nBgAAAAF5KjE4LzA1LzIxLzE2LzA2LzAyOzAuMC43Mi4xODg7MDswOzAuMC4wLjA=\",\"type\":1}";
            list.add(s);
        }

//        boolean b = producer.producer("zfradiusqueue", list);
//        System.out.println("---------status is,"+b+"======="+list.size());
        list.clear();

    }

}
