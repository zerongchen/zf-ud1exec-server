package com.aotain.ud1exec.utils;


public class CastUtil {

    /**
     * 转为String 类型 默认值为空字符串
     */
    public static String castString(Object obj) {
        return castString(obj,"");
    }

    /**
     * 转为String 类型 可以指定默认值
     */
    public static String castString(Object obj, String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    /**
     * 转为double 类型 默认值为0
     */
    public static double castDouble(Object obj) {
        return castDouble(obj,0);
    }

    /**
     * 转为double 类型 可以指定默认值
     */
    public static double castDouble(Object obj, double defaultValue) {
       double doubleValue  = defaultValue;

       if(obj!=null){
            String strValue = castString(obj);
            if(StringUtil.isNotEmpty(strValue)){
                try {
                    doubleValue = Double.parseDouble(strValue);
                } catch (NumberFormatException e) {
                    doubleValue=defaultValue;
                }
            }
       }
       return doubleValue;
    }


    /**
     * 转为long 类型 默认值为0
     */
    public static long castLong(Object obj) {
        return castLong(obj,0);
    }

    /**
     * 转为long 类型 可以指定默认值
     */
    public static long castLong(Object obj, long defaultValue) {
        long doubleValue  = defaultValue;

        if(obj!=null){
            String strValue = castString(obj);
            if(StringUtil.isNotEmpty(strValue)){
                try {
                    doubleValue = Long.parseLong(strValue);
                } catch (NumberFormatException e) {
                    doubleValue=defaultValue;
                }
            }
        }
        return doubleValue;
    }

    /**
     * 转为int 类型 默认值为0
     */
    public static int castInt(Object obj) {
        return castInt(obj,0);
    }

    /**
     * 转为int 类型 可以指定默认值
     */
    public static int castInt(Object obj, int defaultValue) {
        int doubleValue  = defaultValue;

        if(obj!=null){
            String strValue = castString(obj);
            if(StringUtil.isNotEmpty(strValue)){
                try {
                    doubleValue = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    doubleValue=defaultValue;
                }
            }
        }
        return doubleValue;
    }



    /**
     * 转为int 类型 默认值为0
     */
    public static boolean castBoolean(Object obj) {
        return castBoolean(obj,false);
    }

    /**
     * 转为int 类型 可以指定默认值
     */
    public static boolean castBoolean(Object obj, boolean defaultValue) {
        boolean booleanValue  = defaultValue;
        if(obj!=null){
            booleanValue = Boolean.parseBoolean(castString(obj));
        }
        return booleanValue;
    }

}
