package com.aotain.ud1exec.service;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.kafka.KafkaProducer;
import com.aotain.common.utils.tools.CommonConstant;
import com.aotain.ud1exec.utils.Constants;
import com.aotain.ud1exec.utils.FileUtils;
import com.aotain.ud1exec.utils.HadoopUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author chenzr
 *
 */
public class CreateTdFileThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(CreateTdFileThread.class);

	private String td_path;

	private String tmp;

	private String fileNameKey;

	private List<String> datas;

	private String cachePath;

	public CreateTdFileThread( String td_path, String dthour, String fileNameKey, List<String> datas, String cachePath) {
		super();
		this.td_path = td_path;
		this.tmp = dthour;
		this.fileNameKey = fileNameKey;
		this.datas = datas;
		this.cachePath = cachePath;
	}

	public void run() {
		
		logger.info("[CreateTdFileThread] run .start.. fileNameKey={" + fileNameKey + "},datasSize={"
				+ datas.size() + "}");
		if (datas.size() == 0) {
			return;
		}
		try{
			String path;
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				path = System.getProperty("user.dir")+File.separator+"td"+File.separator+td_path;
			}else {
				path = LocalConfig.getInstance().getHashValueByHashKey("ubas.td.export.path")+File.separator+td_path;
			}
			boolean flag = FileUtils.createTdFile(path,fileNameKey,datas);
			if(flag){
				File removeFile = new File(cachePath + File.separator + tmp,fileNameKey);
				boolean deleteFlag = removeFile.delete();
				if(logger.isDebugEnabled()){
					if(deleteFlag){
						logger.debug("file={"+removeFile.getAbsolutePath()+"} delete success,");
					}else{
						logger.debug("file={"+removeFile.getAbsolutePath()+"} delete failure,");
					}
				}
			}
		}catch(Exception e){
			logger.error(" create td file error ",e);
		}
	}



}
