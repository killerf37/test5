package com.ginfon.scada.gateway.websocket.config.dto;

public class LineCloggedStatus {
	// 线体编号
	private String sn;
	// 堵包时间
	private int cloggedtime;
	// 线体堵塞状态
	private int status;

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public int getCloggedtime() {
		return cloggedtime;
	}

	public void setCloggedtime(int cloggedtime) {
		this.cloggedtime = cloggedtime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
