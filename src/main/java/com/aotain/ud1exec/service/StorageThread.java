package com.aotain.ud1exec.service;

import com.aotain.ud1exec.utils.HadoopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * 
 * @author cym
 *
 */
public class StorageThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(StorageThread.class);
	private String hive_table_path;
	
	private String dthour;

	private String fileNameKey;

	private List<String> datas;

	private String prefix;

	private String cachePath;

	public StorageThread(String prefix, String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath) {
		super();
		this.hive_table_path = hive_table_path;
		this.dthour = dthour;
		this.fileNameKey = fileNameKey;
		this.datas = datas;
		this.cachePath = cachePath;
		this.prefix = prefix;
	}

	public void run() {

		if (datas.size() == 0) {
			return;
		}
		String date = dthour.substring(0, 8);
		String hour = dthour.substring(8, 10);
		try{
			boolean flag = HadoopUtil.writerHDFS(hive_table_path,date,hour,datas,prefix,fileNameKey);
			if(flag){
				if (logger.isDebugEnabled()) {
					logger.info("{" + hive_table_path + "/" + dthour + "},key={" + fileNameKey + "},datasSize={"
							+ datas.size() + "}");
				}
				File removeFile = new File(cachePath + File.separator + dthour,fileNameKey);
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
			logger.error(" write hdfs error ",e);
		}
	}

}
