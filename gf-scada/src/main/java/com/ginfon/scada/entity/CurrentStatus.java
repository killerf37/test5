package com.ginfon.scada.entity;

public class CurrentStatus {

    private int id;//id
    private Integer parentlineNo;//线体号
    private String lineNo;//皮带号
    private int stateId;//状态码
    private int type;//活动状态
    private String startime;//开始时间
    private String endtime;//结束时间
    private String classes;//班次

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getStartime() {
        return startime;
    }

    public void setStartime(String startime) {
        this.startime = startime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public Integer getParentlineNo() {
        return parentlineNo;
    }

    public void setParentlineNo(Integer parentlineNo) {
        this.parentlineNo = parentlineNo;
    }

    @Override
    public String toString() {
        return "CurrentStatus{" +
                "id=" + id +
                ", parentlineNo=" + parentlineNo +
                ", lineNo=" + lineNo +
                ", stateId=" + stateId +
                ", type=" + type +
                ", startime='" + startime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", classes='" + classes + '\'' +
                '}';
    }
}
