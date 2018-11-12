package com.aotain.ud1exec.service.appflow;

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
public class AppFlowThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(AppFlowThread.class);
	private String hive_table_path;
	
	private String dthour;

	private String fileNameKey;

	private List<String> datas;

	private String prefix = "GeneralFlowFile";

	private String cachePath;

	public AppFlowThread(String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath) {
		super();
		this.hive_table_path = hive_table_path;
		this.dthour = dthour;
		this.fileNameKey = fileNameKey;
		this.datas = datas;
		this.cachePath = cachePath;
	}

	public void run() {
		if (datas.size() == 0) {
			return;
		}
		String date = dthour.substring(0, 8);
		String hour = dthour.substring(8, 10);
	//	boolean b = HadoopUtil.writerHDFS(hive_table_path,date,hour,datas,prefix,fileNameKey);
	//	logger.info(String.format("path={%s},dthour={%s},storage status={%s}",hive_table_path,dthour,b));

		try{
			if(!HadoopUtil.writerHDFS(hive_table_path,date,hour,datas,prefix,fileNameKey)){
				throw new RuntimeException("dthour={"+dthour+"},fileNameKey={"+fileNameKey+"},hive_table_path={"+hive_table_path+"},failure.");
			}
			File removeFile = new File(cachePath + "/" + dthour + "/" + fileNameKey);
			if (!removeFile.delete()){
				throw new RuntimeException("delete file={ "+removeFile.getAbsolutePath()+" } error,");
			}
		}catch(Exception e){
			logger.error(" write hdfs error ",e);
		}

	}

}
