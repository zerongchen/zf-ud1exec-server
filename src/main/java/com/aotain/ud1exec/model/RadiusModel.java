package com.aotain.ud1exec.model;

import com.aotain.ud1exec.utils.StringUtil;
import com.aotain.ud1exec.utils.UcParseUtil;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

@Setter
public class RadiusModel {
    private Long createTime;
    private String createIp;
    private String userName;
    private String nasIpAddress;
    private String nasPort;
    private String framedIpAddress;
    private String filterId;
    private String vendorSpecific;
    private String nasIdentifier;
    private Integer acctStatusType;
    private Long acctInputOctets;
    private Long acctOutputOctets;
    private String acctSessionId;
    private Long acctSessionTime;
    private Long acctInputPackets;
    private Long acctOutputPackets;
    private Integer acctTerminateCause;
    private Long eventTimestamp;
    private String framedInterfaceId;
    private String framedIpv6Prefix;
    private String delegatedIpv6Prefix;
    private String natFramedIpAddress;
    private String startPort;
    private String endPort;

    public Long getCreateTime() {
        return createTime;
    }

    public String getCreateIp() {
        return createIp;
    }

    public String getUserName() {
        if(StringUtils.isEmpty(userName)){
            return "";
        }
        return userName;
    }

    public String getNasIpAddress() {
        if(StringUtils.isEmpty(nasIpAddress)){
            return "";
        }
        return nasIpAddress;
    }

    public String getNasPort() {
        if(StringUtils.isEmpty(nasPort)){
            return "";
        }
        return nasPort;
    }

    public String getFramedIpAddress() {
        if(StringUtils.isEmpty(framedIpAddress)){
            return "";
        }
        return framedIpAddress;
    }

    public String getFilterId() {
        if(StringUtils.isEmpty(filterId)){
            return "";
        }
        return filterId;
    }

    public String getVendorSpecific() {
        if(StringUtils.isEmpty(vendorSpecific)){
            return "";
        }
        return vendorSpecific;
    }

    public String getNasIdentifier() {
        if(StringUtils.isEmpty(nasIdentifier)){
            return "";
        }
        return nasIdentifier;
    }

    public Integer getAcctStatusType() {
        if(acctStatusType==null){
            return 0;
        }
        return acctStatusType;
    }

    public Long getAcctInputOctets() {
        if (acctInputOctets==null){
            return 0l;
        }
        return acctInputOctets;
    }

    public Long getAcctOutputOctets() {
        if(acctOutputOctets==null){
            return 0l;
        }
        return acctOutputOctets;
    }

    public String getAcctSessionId() {
        if(StringUtils.isEmpty(acctSessionId)){
            return "";
        }
        return acctSessionId;
    }

    public Long getAcctSessionTime() {
        if(acctSessionTime==null){
            return 0l;
        }
        return acctSessionTime;
    }

    public Long getAcctInputPackets() {
        if(acctInputPackets==null){
            return 0l;
        }
        return acctInputPackets;
    }

    public Long getAcctOutputPackets() {
        if(acctOutputPackets==null){
            return 0l;
        }
        return acctOutputPackets;
    }

    public Integer getAcctTerminateCause() {
        if(acctTerminateCause==null){
            return 0;
        }
        return acctTerminateCause;
    }

    public Long getEventTimestamp() {
        if(eventTimestamp==null){
            return 0l;
        }
        return eventTimestamp;
    }

    public String getFramedInterfaceId() {
        if(StringUtils.isEmpty(framedInterfaceId)){
            return "";
        }
        return framedInterfaceId;
    }

    public String getFramedIpv6Prefix() {
        if(StringUtils.isEmpty(framedIpv6Prefix)){
            return "";
        }
        return framedIpv6Prefix;
    }

    public String getDelegatedIpv6Prefix() {
        if(StringUtils.isEmpty(delegatedIpv6Prefix)){
            return "";
        }
        return delegatedIpv6Prefix;
    }

    public String getStartPort() {
        if(StringUtils.isEmpty(startPort)){
            return "0";
        }
        return startPort;
    }

    public String getNatFramedIpAddress() {
        if(StringUtils.isEmpty(natFramedIpAddress)){
            return "";
        }
        return natFramedIpAddress;
    }

    public String getEndPort() {
        if(StringUtils.isEmpty(endPort)){
            return "0";
        }
        return endPort;
    }

    public String toHdfsObj(){
        return getCreateTime()+"|"+getCreateIp()+"|"+getUserName()+"|"+getNasIpAddress()+"|"+getNasPort()+"|"+
                getFramedIpAddress()+"|"+getFilterId()+"|"+getVendorSpecific()+"|"+getNasIdentifier()+"|"+getAcctStatusType()+"|"+
                getAcctInputOctets()+"|"+getAcctOutputOctets()+"|"+getAcctSessionId()+"|"+getAcctSessionTime()+"|"+getAcctInputPackets()+"|"+
                getAcctOutputPackets()+"|"+getAcctTerminateCause()+"|"+getEventTimestamp()+"|"+getFramedInterfaceId()+"|"+getFramedIpv6Prefix()+"|"+
                getDelegatedIpv6Prefix()+"|"+getNatFramedIpAddress()+"|"+getStartPort()+"|"+getEndPort();
    }

    public String toTdObj(){
        return getUserName()+"|"+((StringUtils.isEmpty(getStartPort()) || "0".equals(getStartPort()))?getFramedIpAddress():getNatFramedIpAddress())+"|"+Integer.toHexString(Integer.parseInt(getStartPort()))+"|"+Integer.toHexString(Integer.parseInt(getEndPort()))+"|"+
                UcParseUtil.inet_pton(getFramedIpv6Prefix())+"|"+UcParseUtil.inet_pton(getDelegatedIpv6Prefix())+"|"+ UcParseUtil.parseTimestamp(getEventTimestamp());
    }

}
