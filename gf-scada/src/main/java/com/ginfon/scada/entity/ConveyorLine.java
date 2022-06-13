package com.ginfon.scada.entity;

/**
 * 	输送线在数据库中的定义。
 * @author Mark
 *
 */
public class ConveyorLine {
	
	private Integer lineNo;
	
	private String plc;
	
	private Integer type;
	//改为IP地址了
	private String proudctId;
	
	private String deviceId;
	
	private String deviceName;
	
	private String beltLine;
	
	private Integer blockingDetectionNum;
	
	private Integer length;
	
	public ConveyorLine() {
		
	}

	public Integer getLineNo() {
		return lineNo;
	}

	public void setLineNo(Integer lineNo) {
		this.lineNo = lineNo;
	}

	public String getPlc() {
		return plc;
	}

	public void setPlc(String plc) {
		this.plc = plc;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getProudctId() {
		return proudctId;
	}

	public void setProudctId(String proudctId) {
		this.proudctId = proudctId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getBeltLine() {
		return beltLine;
	}

	public void setBeltLine(String beltLine) {
		this.beltLine = beltLine;
	}

	public Integer getBlockingDetectionNum() {
		return blockingDetectionNum;
	}

	public void setBlockingDetectionNum(Integer blockingDetectionNum) {
		this.blockingDetectionNum = blockingDetectionNum;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}
}
