package com.aotain.ud1exec.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将字符串转换为TLV对象 
 *  
 */  
public class TlvUtils {

        static Logger logger = LoggerFactory.getLogger(TlvUtils.class);
        /**
         * 将16进制字符串转换为TLV对象列表
         * @param hexString
         * @return
         */
    public static List<Tlv> builderTlvList(String hexString) {  
        List<Tlv> tlvs = new ArrayList<Tlv>();
  
        int position = 0;  
        while (position != StringUtils.length(hexString)) {
            String _hexTag = getTag(hexString, position);  
            position += _hexTag.length();  
  
            LPositon l_position = getLengthAndPosition(hexString, position);  
            int _vl = l_position.get_vL();  
  
            position = l_position.get_position();  
  
            String _value = StringUtils.substring(hexString, position, position + _vl * 2);
  
            position = position + _value.length();  
  
            tlvs.add(new Tlv(_hexTag, _vl, _value));  
        }  
        return tlvs;  
    }  
      
    /** 
     * 将16进制字符串转换为TLV对象MAP 
     * @param hexString 
     * @return 
     */  
    public static Map<String, Tlv> builderTlvMap(String hexString) {  

        try {
            Map<String, Tlv> tlvs = new HashMap<String, Tlv>();

            int position = 0;
            while (position != hexString.length()) {
                String _hexTag = getTag(hexString, position);

                position += _hexTag.length();

                LPositon l_position = getLengthAndPosition(hexString, position);

                int _vl = l_position.get_vL();
                position = l_position.get_position();
                String _value = hexString.substring(position, position + _vl * 2 -4);
                position = position + _value.length();
                String _tag = String.valueOf(Integer.valueOf(_hexTag,16));
                tlvs.put(_tag, new Tlv(_tag, _vl, _value));
            }
            return tlvs;
        }catch (Exception e){
            logger.error("when parse to tlv cause error "+e);
            logger.error("the original hex msg is "+hexString);
            return null;
        }
    }
      
    /** 
     * 返回最后的Value的长度 
     *  
     * @param hexString 
     * @param position 
     * @return  
     */  
    private static LPositon getLengthAndPosition(String hexString, int position) {  
        String firstByteString = hexString.substring(position, position + 2);
        int i = Integer.parseInt(firstByteString, 16);
        String hexLength = "";

        if (((i >>> 7) & 1) == 0) {
            hexLength = hexString.substring(position, position + 2);
            position = position + 2;
        } else {
            // 当最左侧的bit位为1的时候，取得后7bit的值，
            int _L_Len = i & 127;
            position = position + 2;
            hexLength = hexString.substring(position, position + _L_Len * 2 );
            // position表示第一个字节，后面的表示有多少个字节来表示后面的Value值
            position = position + _L_Len * 2;
        }
        return new LPositon(Integer.parseInt(hexLength, 16), position);

    }
  
    /** 
     * 取得子域Tag标签 
     *  
     * @param hexString 
     * @param position 
     * @return 
     */  
    private static String getTag(String hexString, int position) {  
        String firstByte = StringUtils.substring(hexString, position, position + 2);
        int i = Integer.parseInt(firstByte, 16);
        if ((i & 0x1f) == 0x1f) {
            return hexString.substring(position, position + 4);

        } else {
            return hexString.substring(position, position + 2);
        }
    }

}