package com.aotain.ud1exec.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonUtils {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    public static int getInteger(String json,String key){
        return getInteger(JSON.parseObject(json),key);
    }

    public static int getInteger(JSONObject json,String key){
        if(json.containsKey(key)){
            return json.getInteger(key);
        }
        logger.error(String.format("%s not exist key %s",json,key));
        return -1;
    }

    public static String getString(String json,String key){
        return getString(JSON.parseObject(json),key);
    }

    public static String getString(JSONObject json,String key){
        if(json.containsKey(key)){
            return json.getString(key);
        }
        logger.error(String.format("%s not exist key %s",json,key));
        return "unkown";
    }

}
