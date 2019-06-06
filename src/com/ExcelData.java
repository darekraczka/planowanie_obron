package com;

import java.net.Inet4Address;

public class ExcelData {
    public Integer getDay() {
        return day;
    }


    public Integer getHour() {
        return hour;
    }

    public String getLeader() {
        return leader;
    }

    public String getPro() {
        return pro;
    }

    public String getRec() {
        return rec;
    }

    private Integer day;


    public  Integer getMonth() {
        return month;
    }

    public  Integer getYear() {
        return year;
    }

    public void setDay(String day) {
        this.day = Integer.parseInt(day);
    }

    public void setMonth(String month) {
        this.month = Integer.parseInt(month);
    }

    public void setYear(String year) {
        this.year = Integer.parseInt(year);
    }

    public void setHour(String hour) {
        this.hour = Integer.parseInt(hour);
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public void setRec(String rec) {
        this.rec = rec;
    }

    private Integer month;
    private Integer year;

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = Integer.parseInt(minutes);
    }

    private Integer minutes;
    private Integer hour;
    private String leader;
    private String pro;
    private String rec;
}
