package com.aotain.ud1exec.common;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.config.model.EuAttributeInfo;
import com.aotain.ud1exec.utils.DpiAttributeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/spring-base.xml"})
public class Test_LocalConfig {
    @Test
    public void test1(){
        LocalConfig local = LocalConfig.getInstance();
        System.out.println(local);
        String euSoftwareProviderByIp = DpiAttributeUtil.getEuSoftwareProviderByIp("12.3.3.8");
        System.out.println(euSoftwareProviderByIp);
    }

    @Test
    public void test2(){
        EuAttributeInfo info = LocalConfig.getInstance().getEuAttributeInfoByEuip("12.3.3.8");
        System.out.println(info);
    }

}
