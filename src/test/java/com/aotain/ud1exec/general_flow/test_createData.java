package com.aotain.ud1exec.general_flow;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.kafka.KafkaProducer;
import com.aotain.common.utils.tools.CommonConstant;
import com.aotain.ud1exec.service.Ud1Processer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/spring-base.xml"})
public class test_createData {

    @Test
    public void test_20180208(){

        String message = "{\"data\":{\"logcontent\":\"1531721399|1531894199|3|3|2||6000|1346|2568|44|98|800\"},\"packetsubtype\":2,\"packettype\":\n" +
                "1,\"probetype\":0,\"receivedip\":\"192.168.50.222\",\"receivedtime\":1531721399,\"sendip\":\"192.168.50.191\"}";
        Ud1Processer Ud1Processer = new Ud1Processer();
        Ud1Processer.callback(1,1,1,message);
    }

    @Test
    public void test2(){
        GeneralFlowModel generalFlowModel = new GeneralFlowModel();
        generalFlowModel.setAppid(1);
        generalFlowModel.setAppname("appname");
        generalFlowModel.setAppnamelength(2);
        generalFlowModel.setAppnewsessionnum(3);
        generalFlowModel.setApppacketsnum(4);
        generalFlowModel.setAppsessionsnum(5);
        generalFlowModel.setApptraffic_dn(6);
        generalFlowModel.setApptraffic_up(7);
        generalFlowModel.setApptype(8);
        generalFlowModel.setAppusernum(9);
        generalFlowModel.setR_endtime(new Date().getTime());
        generalFlowModel.setR_starttime(new Date().getTime());
        generalFlowModel.setUsergroupno(10);

        Map<String,String> data = new HashMap<String, String>();
        data.put("logcontent",generalFlowModel.getString());
        Ud1Queue ud1Queue = new Ud1Queue();
        ud1Queue.setdata(data);
        ud1Queue.setPacketsubtype(0x02);
        ud1Queue.setPackettype(0x01);
        ud1Queue.setProbetype(1);
        ud1Queue.setReceivedip("192.168.10.10");
        ud1Queue.setReceivedtime(new Date().getTime());
        ud1Queue.setSendip("192.168.10.10");

        String message = ud1Queue.objectToJson();
        System.out.println(message);
        Ud1Processer Ud1Processer = new Ud1Processer();
        Ud1Processer.callback(1,1,1,message);
    }

    @Test
    public void test3(){
        GeneralFlowModel generalFlowModel = new GeneralFlowModel();
        generalFlowModel.setAppid(1);
        generalFlowModel.setAppname("appname");
        generalFlowModel.setAppnamelength(2);
        generalFlowModel.setAppnewsessionnum(3);
        generalFlowModel.setApppacketsnum(4);
        generalFlowModel.setAppsessionsnum(5);
        generalFlowModel.setApptraffic_dn(6);
        generalFlowModel.setApptraffic_up(7);
        generalFlowModel.setApptype(8);
        generalFlowModel.setAppusernum(9);
        generalFlowModel.setR_endtime(new Date().getTime()/1000);
        generalFlowModel.setR_starttime(new Date().getTime()/1000);
        generalFlowModel.setUsergroupno(10);

        Map<String,String> data = new HashMap<String, String>();
        data.put("logcontent",generalFlowModel.getString());
        Ud1Queue ud1Queue = new Ud1Queue();
        ud1Queue.setdata(data);
        ud1Queue.setPacketsubtype(0x02);
        ud1Queue.setPackettype(0x01);
        ud1Queue.setProbetype(1);
        ud1Queue.setReceivedip("192.168.10.10");
        ud1Queue.setReceivedtime(new Date().getTime()/1000);
        ud1Queue.setSendip("192.168.10.10");

        String message = ud1Queue.objectToJson();
        System.out.println(message);

        Map<String, Object> conf = LocalConfig.getInstance().getKafkaProducerConf();
        KafkaProducer producer = new KafkaProducer(conf);

        List<String> list = new ArrayList<String>();
        for(int i=0;i<10000;i++){
            System.out.println(i);
            list.add(message);
        }
        boolean b = producer.producer(CommonConstant.KAFKA_QUEUE_NAME_UD1LOG, list);
        System.out.println("status is,"+b);
    }

    @Test
    public void test4(){
        GeneralFlowModel generalFlowModel = new GeneralFlowModel();
        generalFlowModel.setAppid(1);
        generalFlowModel.setAppname("appname");
        generalFlowModel.setAppnamelength(2);
        generalFlowModel.setAppnewsessionnum(3);
        generalFlowModel.setApppacketsnum(4);
        generalFlowModel.setAppsessionsnum(5);
        generalFlowModel.setApptraffic_dn(6);
        generalFlowModel.setApptraffic_up(7);
        generalFlowModel.setApptype(8);
        generalFlowModel.setAppusernum(9);
        generalFlowModel.setR_endtime(new Date().getTime()/1000);
        generalFlowModel.setR_starttime(new Date().getTime()/1000);
        generalFlowModel.setUsergroupno(10);

        Map<String,String> data = new HashMap<String, String>();
        data.put("logcontent",generalFlowModel.getString());
        Ud1Queue ud1Queue = new Ud1Queue();
        ud1Queue.setdata(data);
        ud1Queue.setPacketsubtype(0x02);
        ud1Queue.setPackettype(0x01);
        ud1Queue.setProbetype(1);
        ud1Queue.setReceivedip("192.168.10.10");
        ud1Queue.setReceivedtime(new Date().getTime()/1000);
        ud1Queue.setSendip("192.168.10.10");

        String message = ud1Queue.objectToJson();
        System.out.println(message);

        Map<String, Object> conf = LocalConfig.getInstance().getKafkaProducerConf();
        KafkaProducer producer = new KafkaProducer(conf);

        List<String> list = new ArrayList<String>();
        for(int i=0;i<10000;i++){
            System.out.println(i);
            boolean b = producer.producer(CommonConstant.KAFKA_QUEUE_NAME_UD1LOG, message);
            System.out.println("status is,"+b);
        }

    }

}
