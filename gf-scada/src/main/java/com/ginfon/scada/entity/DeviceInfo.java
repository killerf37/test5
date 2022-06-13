package com.ginfon.scada.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/12/17/17:09
 * @Description:
 */
public class DeviceInfo {
    private Long id;
    private String name;
    private Integer lineNo;
    private Integer devicePartNo;
    private Integer deviceType;
    private Integer deviceStatus;
    private Integer errCode;
    private List<Integer> errList;

    public DeviceInfo(ScadaStatusDTO deviceInfo)
    {
        errList=new ArrayList<>();
        deviceType=deviceInfo.getDeviceType();
        devicePartNo=deviceInfo.getDeviceNo();
        lineNo= Integer.parseInt(deviceInfo.getLineNo());
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(Integer deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public List<Integer> getErrList() {
        return errList;
    }

    public void setErrList(List<Integer> errList) {
        this.errList = errList;
    }

    public void addToErrList(Integer err)
    {this.errList.add(err);}

    public void removeFromErrList(Integer err)
    {
        if (this.errList.contains(err))
        {
            this.errList.remove(err);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public Integer getDevicePartNo() {
        return devicePartNo;
    }

    public void setDevicePartNo(Integer devicePartNo) {
        this.devicePartNo = devicePartNo;
    }
}
