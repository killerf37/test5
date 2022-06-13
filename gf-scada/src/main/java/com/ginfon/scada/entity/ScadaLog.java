package com.ginfon.scada.entity;

//Scada事件信息
public class ScadaLog {

    private String lineNo;

    private Integer deviceType;

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    //设备编号
    private String deviceNo;
    //消息类型
    private String msgType;
    //事件类型
    private Long eventType;
    //事件描述
    private String eventDesc;
    //触发事件
    private String triggerTime;

    private String startTime;

    private String endTime;

    public String getdeviceNo() {
        return deviceNo;
    }

    public void setdeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Long getEventType() {
        return eventType;
    }

    public void setEventType(Long eventType) {
        this.eventType = eventType;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
