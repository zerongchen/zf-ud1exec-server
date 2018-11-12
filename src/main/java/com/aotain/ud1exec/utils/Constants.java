package com.aotain.ud1exec.utils;

import com.aotain.common.config.LocalConfig;

import java.io.File;
import java.text.SimpleDateFormat;

public class Constants {

    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");
    public static SimpleDateFormat yyyyMMddHHMMSSFormatter = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String CACHE_PATH_ROOT = System.getProperty("user.dir")+File.separator+"cache";

    public static String GENERAL_FLOW_PATH = CACHE_PATH_ROOT+ File.separator+"job_ubas_appflow";

}
