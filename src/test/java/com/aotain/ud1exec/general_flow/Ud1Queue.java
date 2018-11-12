package com.aotain.ud1exec.general_flow;

import java.util.Map;

public class Ud1Queue extends BaseVo{


    private int probetype;
   private int packettype;
   private int packetsubtype;
   private Long receivedtime;
   private String receivedip;
   private String sendip;
   private Map<String,String> data;

    public void setProbetype(int probetype) {
        this.probetype = probetype;
    }

    public void setPackettype(int packettype) {
        this.packettype = packettype;
    }

    public void setPacketsubtype(int packetsubtype) {
        this.packetsubtype = packetsubtype;
    }

    public void setReceivedtime(Long receivedtime) {
        this.receivedtime = receivedtime;
    }

    public void setReceivedip(String receivedip) {
        this.receivedip = receivedip;
    }

    public void setSendip(String sendip) {
        this.sendip = sendip;
    }

    public void setdata(Map<String,String> data) {
        this.data = data;
    }
    public int getProbetype() {
        return probetype;
    }

    public int getPackettype() {
        return packettype;
    }

    public int getPacketsubtype() {
        return packetsubtype;
    }

    public Long getReceivedtime() {
        return receivedtime;
    }

    public String getReceivedip() {
        return receivedip;
    }

    public String getSendip() {
        return sendip;
    }

    public Map<String,String> getData() {
        return data;
    }
}
