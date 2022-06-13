package com.ginfon.scada.entity;

/**
 * @Author: James
 * @Date: 2020/3/27 13:09
 * @Description:
 */
public class CurrentVoltage {
    // 电流值
    private int current;
    // 电压值
    private int voltage;
    // 触发时间
    private String triggerTime;

    private String number;

    //平均电流值
    private int avgCurrent;

    //平均电压值
    private int avgVoltage;

    //时间
    private String times;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getAvgCurrent() {
        return avgCurrent;
    }

    public void setAvgCurrent(int avgCurrent) {
        this.avgCurrent = avgCurrent;
    }

    public int getAvgVoltage() {
        return avgVoltage;
    }

    public void setAvgVoltage(int avgVoltage) {
        this.avgVoltage = avgVoltage;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }
}
