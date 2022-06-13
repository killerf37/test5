package com.ginfon.scada.entity;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/12/17/13:52
 * @Description:
 */
public class DeviceFaultDTO {
    private BigInteger ID;
    private String lineNo;
    private Integer deviceType;
    private Integer deviceNo;
    private Integer faultIndex;
    private Integer fault;
    private Timestamp faultTriggerTime;
    private Timestamp faultReleaseTime;

    private String lineName;
    private String typeDescrib;
    private String devicePhysicalNo;
    private String faultDescrib;
    private BigInteger clientId;

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getTypeDescrib() {
        return typeDescrib;
    }

    public void setTypeDescrib(String typeDescrib) {
        this.typeDescrib = typeDescrib;
    }

    public String getDevicePhysicalNo() {
        return devicePhysicalNo;
    }

    public void setDevicePhysicalNo(String devicePhysicalNo) {
        this.devicePhysicalNo = devicePhysicalNo;
    }

    public String getFaultDescrib() {
        return faultDescrib;
    }

    public void setFaultDescrib(String faultDescrib) {
        this.faultDescrib = faultDescrib;
    }

    public BigInteger getClientId() {
        return clientId;
    }

    public void setClientId(BigInteger clientId) {
        this.clientId = clientId;
    }

    public DeviceFaultDTO() {
    }

    public DeviceFaultDTO(ScadaStatusDTO statusDTO, Integer faultCode)
    {
        lineNo=statusDTO.getLineNo();
        deviceType=statusDTO.getDeviceType();
        deviceNo=statusDTO.getDeviceNo();
        faultTriggerTime=statusDTO.getFaultTriggerTime();
        fault=faultCode;
    }

    public BigInteger getID() {
        return ID;
    }

    public void setID(BigInteger ID) {
        this.ID = ID;
    }

    public String getlineNo() {
        return lineNo;
    }

    public void setlineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public Integer getdeviceType() {
        return deviceType;
    }

    public void setdeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getdeviceNo() {
        return deviceNo;
    }

    public void setdeviceNo(Integer deviceNo) {
        this.deviceNo = deviceNo;
    }

    public Integer getfaultIndex() {
        return faultIndex;
    }

    public void setfaultIndex(Integer faultIndex) {
        this.faultIndex = faultIndex;
    }

    public Integer getfault() {
        return fault;
    }

    public void setfault(Integer fault) {
        this.fault = fault;
    }

    public Timestamp getfaultTriggerTime() {
        return faultTriggerTime;
    }

    public void setfaultTriggerTime(Timestamp faultTriggerTime) {
        this.faultTriggerTime = faultTriggerTime;
    }

    public Timestamp getfaultReleaseTime() {
        return faultReleaseTime;
    }

    public void setfaultReleaseTime(Timestamp faultReleaseTime) {
        this.faultReleaseTime = faultReleaseTime;
    }
}
