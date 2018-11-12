package com.aotain.ud1exec.utils;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.config.model.IdcHouses;
import org.slf4j.LoggerFactory;

public class IdcHouseUtil {
	/**
     * 写日志
     */
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(IdcHouseUtil.class);
	/**
	 * eu 信息缓存
	 */
	private final static Map<String, IdcHouses> houseMap= LocalConfig.getInstance().getAllIdcHouses();
	
	 /**
     * 根据机房编码获得机房ID
     * @return 机房ID
     */
	public static long getHouseIdByHouseIdStr(String houseIdStr){
		IdcHouses info = houseMap.get(houseIdStr);
		if(info == null){
			info = LocalConfig.getInstance().getIdcHouse(houseIdStr);
			if(info == null){
				logger.error("IdcHouses is not exists.houseIdStr=" + houseIdStr);
			}
			else{
				houseMap.put(houseIdStr, info);
			}
		}
		return (info == null ? 0L : info.getHouseId());
	}

	/**
     * 根据机房ID获得机房编码
     * @param houseId
     * @return 机房编码
     */
    public String getHouseStrStrByHouseId(Long houseId){
        String houseIdStr = null;
        for(Entry<String, IdcHouses> entry : houseMap.entrySet()){
            if(entry.getValue().getHouseId().equals(houseId)){
                houseIdStr = entry.getKey();
                break;
            }
        }
        //本地缓存没有找到，同步数据库
        if(houseIdStr == null){
        	Map<String, IdcHouses> map = LocalConfig.getInstance().getAllIdcHouses();
        	houseMap.clear();
        	houseMap.putAll(map);
        	for(Entry<String, IdcHouses> entry : houseMap.entrySet()){
                if(entry.getValue().getHouseId().equals(houseId)){
                    houseIdStr = entry.getKey();
                    break;
                }
            }
        	if(houseIdStr == null){
				logger.error("IdcHouses is not exists.houseId=" + houseId);
				houseIdStr = "";
			}
        }
        
        return houseIdStr;
    }

}
