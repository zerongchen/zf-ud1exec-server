package com.aotain.ud1exec.common;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider;
import org.junit.Test;

import java.net.URI;

public class test_HadoopUtil {
    private static FileSystem hadoopFS;

    @Test
    public void f(){

        try {
            Class c = Class.forName("org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
            System.out.println(c);
        } catch (ClassNotFoundException e) {
        }
    }
    @Test
    public void test1() {
        try {
            FileSystem hadoopFS1;
            Configuration conf = new Configuration();
            conf.addResource(new Path("F:/project/ZF/config/hadoop/core-site.dao"));
            conf.addResource(new Path("F:/project/ZF/config/hadoop/yarn-site.dao"));
            conf.addResource(new Path("F:/project/ZF/config/hadoop/hdfs-site.dao"));
            conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
            hadoopFS1 = FileSystem.get(conf);
            /*if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                String hdfsUri = "hdfs://192.168.50.202:8020";
                String hdfsUser = "hadoop";
                hadoopFS1 = FileSystem.get(new URI(hdfsUri), new Configuration(), hdfsUser);
            }*/
            hadoopFS = hadoopFS1;
        } catch (Exception e) {
        }
        System.out.println(hadoopFS);
    }

}
