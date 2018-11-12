package com.aotain.ud1exec.stream.util;

import com.aotain.common.config.LocalConfig;
import kafka.serializer.StringEncoder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Map;
import java.util.Properties;

public class KafkaProducerUtil {

    private static Map<String, Object> conf = LocalConfig.getInstance().getKafkaProducerConf();



    public static KafkaProducer getProducer(){

        Properties _props = new Properties();
        _props.put("serializer.class", StringEncoder.class.getName());
        for(Map.Entry<String, Object> entry : conf.entrySet()){

            _props.put(entry.getKey(), entry.getValue());
        }
        KafkaProducer _producer = new KafkaProducer<String, String>(_props);

        return _producer;
    }

    public static void send( ProducerRecord<String,String> recode){
        getProducer().send(recode);
    }
}
