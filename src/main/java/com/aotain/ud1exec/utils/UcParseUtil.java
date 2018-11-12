package com.aotain.ud1exec.utils;

import com.aotain.ud1exec.model.RadiusModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

public class UcParseUtil {

    static Logger logger = LoggerFactory.getLogger(UcParseUtil.class);
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static Map<String, Tlv> decodeBase64(String data){
        data = data.replaceAll("\r|\n", "");
        byte[] asBytes=null;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            asBytes = decoder.decode(data);
        } catch (Exception e) {
            logger.error("parse BASE64 cause ERROR "+e);
            logger.error("The BASE64 original msg is "+data);
            return null;
        }
        byte[] contentB =null;
        try {
            contentB = new byte[asBytes.length-18];
            for(int i=0; i<asBytes.length-18; i++){
                contentB[i] = asBytes[i+18];
            }
        }catch (Exception e){
            logger.error(" cutting Uc package cause error "+e);
            return null;
        }
        Map<String, Tlv> map = TlvUtils.builderTlvMap(bytesToHexFun1(contentB));
        return map;
    }

    public static RadiusModel pasrseToRadiusModel( Map<String,Tlv> map){

        try {

            if (map==null) return null;
            final RadiusModel model = new RadiusModel();
            map.forEach(new BiConsumer<String, Tlv>() {
                @Override
                public void accept( String s, Tlv tlv ) {
                    if(Integer.parseInt(tlv.getTag())==RadiusConstant.USER_NAME.intValue()){
                        tlv.setValue(hexStringToString(tlv.getValue()));
                        model.setUserName(tlv.getValue());
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.NAS_IP_ADDRESS.intValue()){
                        tlv.setValue(String.valueOf(longToIp2(Long.valueOf(tlv.getValue(),16))));
                        model.setNasIpAddress(tlv.getValue());
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.NAS_PORT.intValue()){
                        tlv.setValue(hexStringToString(tlv.getValue()));
                        model.setNasPort(tlv.getValue());
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.FRAMED_IP_ADDRESS.intValue()){
                        tlv.setValue(String.valueOf(longToIp2(Long.valueOf(tlv.getValue(),16))));
                        model.setFramedIpAddress(tlv.getValue());
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.FILTER_ID.intValue()){
                        tlv.setValue(hexStringToString(tlv.getValue()));
                        model.setFilterId(tlv.getValue());
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.VENDOR_SPECIFIC.intValue()){
                        Map<String, Tlv> map = TlvUtils.builderTlvMap(tlv.getValue());
                        Tlv typeTlv = map.get("120");
                        typeTlv.setValue(String.valueOf(Integer.valueOf(typeTlv.getValue(),16)));
                        Tlv valueTlv = map.get("121");
                        valueTlv.setValue(hexStringToString(valueTlv.getValue()));
                        model.setVendorSpecific(typeTlv.getValue()+","+valueTlv.getValue());
                        String[] infos = valueTlv.getValue().split(";");
                        if(infos.length==5){
                            model.setNatFramedIpAddress(infos[1]);
                            model.setStartPort(infos[2]);
                            model.setEndPort(infos[3]);
                        }
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.NAS_IDENTIFIER.intValue()){
                        tlv.setValue(hexStringToString(tlv.getValue()));
                        model.setNasIdentifier(tlv.getValue());
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.ACCT_STATUS_TYPE.intValue()){
                        tlv.setValue(String.valueOf(Integer.valueOf(tlv.getValue(),16)));
                        model.setAcctStatusType(Integer.valueOf(tlv.getValue()));
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.ACCT_INPUT_OCTETS.intValue()){
                        tlv.setValue(String.valueOf(Long.valueOf(tlv.getValue(),16)));
                        model.setAcctInputOctets(Long.valueOf(tlv.getValue()));
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.ACCT_OUTPUT_OCTETS.intValue()){
                        tlv.setValue(String.valueOf(Long.valueOf(tlv.getValue(),16)));
                        model.setAcctOutputOctets(Long.valueOf(tlv.getValue()));
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.ACCT_SESSION_ID.intValue()){
                        tlv.setValue(hexStringToString(tlv.getValue()));
                        model.setAcctSessionId(tlv.getValue());
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.ACCT_SESSION_TIME.intValue()){
                        tlv.setValue(String.valueOf(Long.valueOf(tlv.getValue(),16)));
                        model.setAcctSessionTime(Long.valueOf(tlv.getValue()));
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.ACCT_INPUT_PACKETS.intValue()){
                        tlv.setValue(String.valueOf(Long.valueOf(tlv.getValue(),16)));
                        model.setAcctInputPackets(Long.valueOf(tlv.getValue()));
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.ACCT_OUTPUT_PACKETS.intValue()){
                        tlv.setValue(String.valueOf(Long.valueOf(tlv.getValue(),16)));
                        model.setAcctOutputPackets(Long.valueOf(tlv.getValue()));
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.ACCT_TERMINATE_CAUSE.intValue()){
                        tlv.setValue(String.valueOf(Integer.valueOf(tlv.getValue(),16)));
                        model.setAcctTerminateCause(Integer.valueOf(tlv.getValue()));
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.EVENT_TIMESTAMP.intValue()){
                        tlv.setValue(String.valueOf(Long.valueOf(tlv.getValue(),16)));
                        model.setEventTimestamp(Long.valueOf(tlv.getValue()));
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.FRAMED_INTERFACE_ID.intValue()){
                        tlv.setValue(hexStringToString(tlv.getValue()));
                        model.setFramedInterfaceId(tlv.getValue());
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.FRAMED_IPV6_PREFIX.intValue()){
                        tlv.setValue(hexStringToString(tlv.getValue()));
                        model.setFramedIpv6Prefix(tlv.getValue());
                    }else if(Integer.parseInt(tlv.getTag())==RadiusConstant.DELEGATED_IPV6_PREFIX.intValue()){
                        tlv.setValue(hexStringToString(tlv.getValue()));
                        model.setDelegatedIpv6Prefix(tlv.getValue());
                    }
                    else {
                        tlv.setValue(hexStringToString(tlv.getValue()));
                    }
                }
            });
            return model;
        }catch (Exception e){
            logger.error("when turn to mode error: "+e);
            return null;
        }
    }

    /**
     * 十六进制转字符串
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                logger.error("hex string change to string error ",e);
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            logger.error("hex string change to string error ",e1);

        }
        return s;
    }

    /**
     * 网络序转字节序
     * @param b
     * @return
     */
    public static int HBytesToInt(byte b[]) {
        return    b[3] & 0xff
                | (b[2] & 0xff) << 8
                | (b[1] & 0xff) << 16
                | (b[0] & 0xff) << 24;
    }

    public static long HBytesToLong(byte array[]) {
        return ((((long) array[ 0] & 0xff) << 56)
                | (((long) array[ 1] & 0xff) << 48)
                | (((long) array[ 2] & 0xff) << 40)
                | (((long) array[ 3] & 0xff) << 32)
                | (((long) array[ 4] & 0xff) << 24)
                | (((long) array[ 5] & 0xff) << 16)
                | (((long) array[ 6] & 0xff) << 8)
                | (((long) array[ 7] & 0xff) << 0));
    }

    /**
     * 字符串转十六进制
     * @param bytes
     * @return
     */
    public static String bytesToHexFun1(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for(byte b : bytes) { // 使用除与取余进行转换
            if(b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }
            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }

        return new String(buf);
    }

    public static String longToIp2(long ip) {

        return ((ip >> 24) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
    }

    /**
     * 0x816fa8c0
     * numeric to presentatio for ipv6
     * @return
     */
    public static String inet_pton(String hex){



        return hex;
    }
    public static String parseTimestamp(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time*1000);
        SimpleDateFormat format =   new SimpleDateFormat( "MMM d, yyyy HH:mm:ss.000000000" , Locale.ENGLISH);
        String date = format.format(cal.getTime());
        return date;
    }

}
