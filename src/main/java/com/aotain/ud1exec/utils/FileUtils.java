package com.aotain.ud1exec.utils;

import com.alibaba.fastjson.JSON;
import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.date.DateUtils;
import com.aotain.common.utils.kafka.KafkaProducer;
import com.aotain.common.utils.tools.CommonConstant;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.common.utils.tools.Tools;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.tools.tar.TarOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPOutputStream;


/**
 * 
 * @author i
 *
 */
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
	/**
	 * 将文件中的每行加到list中
	 * @param file
	 * @return
	 */
	public static List<String> readFileByLines(File file) {  
		List<String> list = new CopyOnWriteArrayList<String>();
        BufferedReader reader = null;  
        try {  
            reader = new BufferedReader(new FileReader(file));  
            String line = null;  
            while ((line = reader.readLine()) != null) {  
        		list.add(line);
            }  
            reader.close();  
        } catch (IOException e) {
            logger.error("readFileByLines error ",e);
            MonitorStatisticsUtils.addEvent(e);
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {
                    MonitorStatisticsUtils.addEvent(e1);
                }  
            }  
        } 
        return list;
    }




    /**
     * 指定路径下生成文件
     * @param path
     * @param dataList
     */
    public synchronized static boolean createTdFile(String path,String fileNameKey , List<String> dataList ) {

        try {
            boolean b =false;
            String time = DateUtils.formatCurrDateyyyyMMddHHmmss();
            String filePrefix = String.format("%s+%s", fileNameKey,time);
            String fileName =String.format("%s.%s", filePrefix ,"txt");
            String filePath = path + File.separator +"temp"+File.separator+ fileName;
            File newFile = new File(filePath);
            BufferedWriter buffer = null;

            if(!newFile.getParentFile().exists()){
                newFile.getParentFile().mkdirs();
            }
            if(!newFile.exists()){
                newFile.createNewFile();
            }

            buffer = new BufferedWriter(new java.io.FileWriter(newFile));

            if(dataList!=null){
                for(String obj :dataList) {
                    buffer.write(obj);
                    buffer.newLine();
                }
            }

            buffer.flush();
            buffer.close();

            synchronized (newFile){

               // File after = compress(pack(new File[]{newFile},new File(path + File.separator +"temp"+File.separator+ String.format("%s.%s", filePrefix ,"tar"))));
                //生成Kafka
                //获得kafka消费配置
                Long uctTime = System.currentTimeMillis()/1000;
                try {
                    if (newFile!=null ) {
                        Long fileSize = newFile.length();
                        String readName =  String.format("%s.%s", filePrefix ,"txt");
                        String readFile = path + File.separator+ readName;
                        if(newFile.renameTo(new File(readFile)))
                        logger.info("create file "+readFile+ " success ,fileSize="+fileSize);

                        Map<String, Object> conf = LocalConfig.getInstance().getKafkaProducerConf();
                        KafkaProducer producer = new KafkaProducer(conf);
                        Map<String, String> map3 = new HashMap<String, String>();

                        map3.put("filetype", "1023");
                        map3.put("filename", readName);
                        map3.put("filetime", String.valueOf(uctTime));
                        map3.put("filesize", "" + fileSize);
                        map3.put("filerecord", String.valueOf(dataList.size()));

                        Map<String, Object> map2 = new HashMap<String, Object>();
                        map2.put("datatype", 3);
                        map2.put("datasubtype", 301);
                        map2.put("datamessage", map3);

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("type", 4);
                        map.put("message", map2);
                        map.put("createtime", time);
                        map.put("createip", Tools.getHostAddress());

                        String msg = JSON.toJSONString(map);
                        b = producer.producer(CommonConstant.KAFKA_QUEUE_NAME_NOTICE, msg);
                   //   logger.debug("write kafka " + msg);
                        logger.debug("write kafka creating info of 3A file success ");
                    }
                }catch (Exception e){
                    logger.error("write kafka creating info of 3A file error ",e);
                }
            }
            return b;
        } catch (IOException e) {
                logger.error("create TD file error  ",e);
                return false;
            }
    }

    /**
     *
     * @Title: pack
     * @Description: 将一组文件打成tar包
     * @param sources 要打包的原文件数组
     * @param target 打包后的文件
     * @return File    返回打包后的文件
     * @throws
     */
    public static File pack(File[] sources, File target){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(target);
        } catch (FileNotFoundException e1) {
            logger.error(target+" no found "+e1);
        }
        TarArchiveOutputStream os = new TarArchiveOutputStream(out);
        InputStream in =null;
        //如果不加下面这段，当压缩包中的路径字节数超过100 byte时，就会报错
        os.setLongFileMode(TarOutputStream.LONGFILE_GNU);
        for (File file : sources) {
            try {
                TarArchiveEntry entry = new TarArchiveEntry("" + file.getName());
                entry.setSize(file.length());
                os.putArchiveEntry(entry);
                in = new FileInputStream(file);
                byte[] buffer=new byte[1024];
                BufferedInputStream bis=new BufferedInputStream(in);
                int count=0;
                while((count=bis.read(buffer,0,1024))>-1){
                    os.write(buffer,0,count);
                }
                if(in!=null){
                    in.close();
                }
            } catch (FileNotFoundException e) {
                logger.error("pack file error ",e);
            } catch (IOException e) {
                logger.error("pack file error ",e);
            }finally {
                if(os != null) {
                    try {
                        os.closeArchiveEntry();
                        os.flush();
                        os.close();
                    } catch (IOException e) {
                        logger.error("close TarArchiveOutputStream error",e);
                    }
                }
                if(in!=null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.info("close input stream error ",e);
                    }
                }
                if(out!=null){
                    try {
                        out.close();
                    } catch (IOException e) {
                        logger.error("close pack file error ",e);
                    }
                }
            }
        }
        for (File file : sources) {
            boolean de = file.delete();
            if (!de){
                file.delete();
            }
        }

        return target;
    }

    /**
     *
     * @Title: compress
     * @Description: 将文件用gzip压缩
     * @param  source 需要压缩的文件
     * @return File    返回压缩后的文件
     * @throws
     */
    public static File compress(File source) {
        File target = new File(source.getAbsolutePath() + ".temp");

        if(!target.exists()){
            try {
                target.createNewFile();
            } catch (IOException e) {
                logger.error("compress tar file to tar.gz file error",e);
            }
        }
        FileInputStream in = null;
        GZIPOutputStream out = null;
        try {
            in = new FileInputStream(source);
            out = new GZIPOutputStream(new FileOutputStream(target));
            byte[] array = new byte[1024];
            int number = -1;
            while((number = in.read(array, 0, array.length)) != -1) {
                out.write(array, 0, number);
            }
        } catch (FileNotFoundException e) {
            logger.error("compress file to gzip error "+e);
            return null;
        } catch (IOException e) {
            logger.error("compress file to gzip error "+e);
            return null;
        } finally {
            if(in != null) {
                try {
                    in.close();

                } catch (IOException e) {
                    logger.error("compress file to gzip error "+e);
                    return null;
                }
            }
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("compress file to gzip error "+e);
                    return null;
                }
            }
            source.delete();
        }
        return target;
    }
}
