package com.aotain.ud1exec.service.traffic;

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
public class TrafficThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(TrafficThread.class);
	private String hive_table_path;
	
	private String dthour;

	private String fileNameKey;

	private List<String> datas;

	private String prefix = "FlowDirectionFile";

	private String cachePath;

	public TrafficThread(String hive_table_path, String dthour, String fileNameKey, List<String> datas, String cachePath) {
		super();
		this.hive_table_path = hive_table_path;
		this.dthour = dthour;
		this.fileNameKey = fileNameKey;
		this.datas = datas;
		this.cachePath = cachePath;
	}

	public void run() {
		
		logger.info("[TrafficThread] run .start.. dthour={" + dthour + "},fileNameKey={" + fileNameKey + "},datasSize={"
				+ datas.size() + "}");
		
		if (datas.size() == 0) {
			return;
		}

		boolean partition = true;
		String date = dthour.substring(0, 8);
		String hour = dthour.substring(8, 10);

		try{
			if(!HadoopUtil.writerHDFS(hive_table_path,date,hour,datas,prefix,fileNameKey)){
				throw new RuntimeException("dthour={"+dthour+"},fileNameKey={"+fileNameKey+"},hive_table_path={"+hive_table_path+"},failure.");
			}
			File removeFile = new File(cachePath + File.pathSeparator + dthour,fileNameKey);
			if(removeFile.exists()){
				if (!removeFile.delete()){
					throw new RuntimeException("delete file={ "+removeFile.getAbsolutePath()+" } error,");
				}
			}
		}catch(Exception e){
			logger.error(" write hdfs error ",e);
		}
	}

}
