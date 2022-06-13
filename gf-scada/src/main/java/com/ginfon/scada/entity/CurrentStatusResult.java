package com.ginfon.scada.entity;

import com.ginfon.core.annotation.Excel;

public class CurrentStatusResult {
    private int id;//id
    /**
     * 	线体号，对应JB/CB这些名称而不是数字号码
     */
    @Excel(name = "线号")
    private String parentlineName;//线体号
    /**
     * 	皮带号，对应某个线体下的指定皮带号码。
     */
    @Excel(name = "编号")
    private String lineName;//皮带号
    /**
     * 	状态描述文本。
     */
    @Excel(name = "状态")
    private String stateId;//异常状态
    /**
     * 	触发状态，触发还是解除。
     */
    @Excel(name = "触发解除")
    private String type;//活动状态
    /**
     * 	触发时间。
     */
    @Excel(name = "触发时间")
    private String startime;//开始时间
    /**
     * 	结束时间。
     */
    @Excel(name = "结束时间")
    private String endtime;//结束时间
    /**
     * 	班次。
     */
    @Excel(name = "班次")
    private String classes;//班次

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParentlineName() {
        return parentlineName;
    }

    public void setParentlineName(String parentlineName) {
        this.parentlineName = parentlineName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartime() {
        return startime;
    }

    public void setStartime(String startime) {
        this.startime = startime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    @Override
    public String toString() {
        return "CurrentStatusResult{" +
                "id=" + id +
                ", parentlineName='" + parentlineName + '\'' +
                ", lineName='" + lineName + '\'' +
                ", stateId='" + stateId + '\'' +
                ", type='" + type + '\'' +
                ", startime='" + startime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", classes='" + classes + '\'' +
                '}';
    }
}
