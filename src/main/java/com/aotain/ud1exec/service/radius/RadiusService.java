package com.aotain.ud1exec.service.radius;

public interface RadiusService {

    void execute(Integer type,long createtime,String createip,int threadnum,String data,int partition);

}
