package com.ginfon.scada.gateway.websocket.config.dto;

import java.util.List;

/**
 * @Author: James
 * @Date: 2020/3/20 13:35
 * @Description: 设备状态
 */
public class DeviceStatus {
	// 设备类型
	private int type;
	// 设备编号
	private String sn;
	// 设备状态
	private int status;
	// 设备其他状态，主要指格口的其他状态
	private int status1;
	// 状态描述
	private String description;
	// 触发时间
	private String triggerTime;
	// 小车所在区域
	private int trayArea;
	// 设备状态，多种重合
	private List<Integer> statusNew;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus1() {
		return status1;
	}

	public void setStatus1(int status1) {
		this.status1 = status1;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTriggerTime() {
		return triggerTime;
	}

	public void setTriggerTime(String triggerTime) {
		this.triggerTime = triggerTime;
	}

	public int getTrayArea() {
		return trayArea;
	}

	public void setTrayArea(int trayArea) {
		this.trayArea = trayArea;
	}

	public List<Integer> getStatusNew() {
		return statusNew;
	}

	public void setStatusNew(List<Integer> bts) {
		this.statusNew = bts;
	}
}
