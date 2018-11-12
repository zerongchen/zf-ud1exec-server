package com.aotain.ud1exec.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public final class StringUtil {
    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        if (str != null) {
            str =str.trim();
        }
        return StringUtils.isEmpty(str);
    }

    /**
     * 判断字符串是否为非空
     */
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

    public static String getDtHour(String utcTime){
        if(org.apache.commons.lang.StringUtils.isBlank(utcTime)||utcTime.length() != 10){
            return "";
        }
        for(int i=0;i<utcTime.length();i++){
            Character c = utcTime.charAt(i);
            if(!Character.isDigit(c)){
                return "";
            }
        }
        Date date = new Date(Long.valueOf(utcTime)*1000);
        return Constants.formatter.format(date);
    }

    public static String getDtHourMS(String utcTime){
        if(org.apache.commons.lang.StringUtils.isBlank(utcTime)||utcTime.length() != 10){
            return "";
        }
        for(int i=0;i<utcTime.length();i++){
            Character c = utcTime.charAt(i);
            if(!Character.isDigit(c)){
                return "";
            }
        }
        Date date = new Date(Long.valueOf(utcTime)*1000);
        return Constants.yyyyMMddHHMMSSFormatter.format(date);
    }
}
