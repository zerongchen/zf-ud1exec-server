package com.aotain.ud1exec.general_flow;


public class GeneralFlowModel extends BaseVo {


    private long r_starttime;
    private long r_endtime;
    private long usergroupno;
    private long apptype;
    private long appid;
    private long appnamelength;
    private String appname;
    private long appusernum;
    private long apptraffic_up;
    private long apptraffic_dn;
    private long apppacketsnum;
    private long appsessionsnum;
    private long appnewsessionnum;


    public long getR_starttime() {
        return r_starttime;
    }

    public long getR_endtime() {
        return r_endtime;
    }

    public long getUsergroupno() {
        return usergroupno;
    }

    public long getApptype() {
        return apptype;
    }

    public long getAppid() {
        return appid;
    }

    public long getAppnamelength() {
        return appnamelength;
    }

    public String getAppname() {
        return appname;
    }

    public long getAppusernum() {
        return appusernum;
    }

    public long getApptraffic_up() {
        return apptraffic_up;
    }

    public long getApptraffic_dn() {
        return apptraffic_dn;
    }

    public long getApppacketsnum() {
        return apppacketsnum;
    }

    public long getAppsessionsnum() {
        return appsessionsnum;
    }

    public long getAppnewsessionnum() {
        return appnewsessionnum;
    }

    public void setR_starttime(long r_starttime) {
        this.r_starttime = r_starttime;
    }

    public void setR_endtime(long r_endtime) {
        this.r_endtime = r_endtime;
    }

    public void setUsergroupno(long usergroupno) {
        this.usergroupno = usergroupno;
    }

    public void setApptype(long apptype) {
        this.apptype = apptype;
    }

    public void setAppid(long appid) {
        this.appid = appid;
    }

    public void setAppnamelength(long appnamelength) {
        this.appnamelength = appnamelength;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public void setAppusernum(long appusernum) {
        this.appusernum = appusernum;
    }

    public void setApptraffic_up(long apptraffic_up) {
        this.apptraffic_up = apptraffic_up;
    }

    public void setApptraffic_dn(long apptraffic_dn) {
        this.apptraffic_dn = apptraffic_dn;
    }

    public void setApppacketsnum(long apppacketsnum) {
        this.apppacketsnum = apppacketsnum;
    }

    public void setAppsessionsnum(long appsessionsnum) {
        this.appsessionsnum = appsessionsnum;
    }

    public void setAppnewsessionnum(long appnewsessionnum) {
        this.appnewsessionnum = appnewsessionnum;
    }

    public String getString(){
        StringBuilder sb = new StringBuilder();
        sb.append(r_starttime).append("|").
                append(r_endtime).append("|").
                append(usergroupno).append("|").
                append(apptype).append("|").
                append(appid).append("|").
                append(appname).append("|").
                append(appusernum).append("|").
                append(apptraffic_up).append("|").
                append(apptraffic_dn).append("|").
                append(apppacketsnum).append("|").
                append(appsessionsnum).append("|").
                append(appnewsessionnum);
        return sb.toString();
    }
}
