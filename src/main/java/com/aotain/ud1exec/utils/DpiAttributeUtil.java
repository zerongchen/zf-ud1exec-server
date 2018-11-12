package com.aotain.ud1exec.utils;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.config.model.EuAttributeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DpiAttributeUtil {
	/**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(DpiAttributeUtil.class);
	/**
	 * eu 信息缓存
	 */
	private final static Map<String, EuAttributeInfo> dpiInfoMap= LocalConfig.getInstance().getAllEuAttributeInfo();
	 /**
     * 根据ip获取对应dpi信息
     * @return 软件厂家编码
     */
	public static String getEuSoftwareProviderByIp(String dpiIp){
		EuAttributeInfo info = dpiInfoMap.get(dpiIp);
		if(info == null){
			info = LocalConfig.getInstance().getEuAttributeInfoByEuip(dpiIp);
			if(info == null){
				logger.warn("dpiIp={"+dpiIp+"} get EuAttributeInfo={"+info+"} ");
			}
			else{
				dpiInfoMap.put(dpiIp, info);
			}
		}
		return (info == null ? "unkownSoftwareProvider" : info.getSoftwareProvider());
	}

	/**
     * 根据ip获取对应Dpi信息
     * @return 地区编码
     */
	public static String getEuAreaIdByIp(String dpiIp){
		EuAttributeInfo info = dpiInfoMap.get(dpiIp);
		if(info == null){
			info = LocalConfig.getInstance().getEuAttributeInfoByEuip(dpiIp);
			if(info == null){
				logger.warn("dpiIp={"+dpiIp+"} get EuAttributeInfo={"+info+"} ");
			}
			else{
				dpiInfoMap.put(dpiIp, info);
			}
		}
		return (info == null ? "unkownAreaId" : info.getAreaId());
	}


	/**
	 * 根据ip获取对应Dpi信息
	 * @return 设备类型
	 */
	public static int getProbeTypeByIp(String dpiIp){
		EuAttributeInfo info = dpiInfoMap.get(dpiIp);
		if(info == null){
			info = LocalConfig.getInstance().getEuAttributeInfoByEuip(dpiIp);
			if(info == null){
				logger.warn("dpiIp={"+dpiIp+"} get EuAttributeInfo={"+info+"} ");
			}
			else{
				dpiInfoMap.put(dpiIp, info);
			}
		}
		return (info == null ? 0 : info.getProbeType());
	}

	public static void main(String[] args) {
		String euSoftwareProviderByIp = DpiAttributeUtil.getEuSoftwareProviderByIp("12.3.3.8");
		System.out.println(euSoftwareProviderByIp);
	}
}
