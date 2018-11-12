package com.aotain.ud1exec.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class MultifileWriter{
	
	/**
     * 写日志
     */
    private Logger logger = LoggerFactory.getLogger(MultifileWriter.class);
    
	private static MultifileWriter instance;

	private MultifileWriter(){

	}
	
	public synchronized static MultifileWriter getInstance() {

		if (instance == null) {
			instance = new MultifileWriter();
		}
		return instance;
	}
	
	// the map's key is path
	private final static ConcurrentHashMap<String, BufferedWriter> writersMap = new ConcurrentHashMap<String, BufferedWriter>();

	public void writeLine(String path, String line) {
		try {
			BufferedWriter bw = writersMap.get(path);
			if (null == bw) {
				File file = new File(path);
				if(file.exists()) file.delete();
				bw = new BufferedWriter(new OutputStreamWriter(FileUtils.openOutputStream(file,
						true),"utf-8"));
				writersMap.put(path, bw);
				if(logger.isInfoEnabled()){
					logger.info("create new file:"+path);
				}
			}
			bw.write(line);
			bw.newLine();
			bw.flush();
		} catch (Exception e) {
			logger.error("Write data to file exception.path=" + path ,e);
		} 
	}

	public void flush(int date) {
		String filename = "";
		try {
			for (Entry<String, BufferedWriter> entry : writersMap.entrySet()) {
				filename = entry.getKey();
				BufferedWriter bw = entry.getValue();
				bw.flush();
			}
		} catch (Exception e) {
			logger.error("Failed to flush file.filename=" + filename, e);
		}
	}
	
	public void close(String filename) {
		try {
			if(logger.isInfoEnabled()){
				logger.info("delete file:"+filename);
			}
			BufferedWriter bw = writersMap.get(filename);
			if(bw != null) {
				writersMap.remove(filename);
				bw.flush();
				bw.close();
			}
		} catch (Exception e) {
			logger.error("Failed to close file.filename=" + filename , e);
		}
	}
	
	public void closeAndRename(String filename,String newFilename) {
		try {
			BufferedWriter bw = writersMap.get(filename);
			if(bw != null) {
				writersMap.remove(filename);
				bw.flush();
				bw.close();
			}
			File file = new File(filename);
			boolean b = file.renameTo(new File(newFilename));
			if(!b){
				logger.info("rename error");
			}
		} catch (Exception e) {
			logger.error("Failed to close And rename file.filename=" + filename +",newFilename=" + newFilename, e);
		}
	}

	public static void main(String[] args) {
		MultifileWriter.getInstance().writeLine("D:\\logs\\2.txt","123455");
	//	MultifileWriter.getInstance().close("2.txt");
	}
}
