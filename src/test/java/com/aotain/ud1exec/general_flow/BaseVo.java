package com.aotain.ud1exec.general_flow;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class BaseVo implements Serializable{

    private static final long serialVersionUID = 1L;

    public String objectToJson(){
        return JSON.toJSONString(this);
    }

    public static <T extends BaseVo> T parseFrom(String json, Class<T> cls){
                return JSON.parseObject(json,cls);
    }
}
