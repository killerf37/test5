package com.ginfon.scada.gateway.websocket.config.dto;

/**
 * 	SCADA客户端与WCS还有金峰云的连接状态的结构体。
 * @author Mark
 *
 */
public class ConnectionStatus {

	private boolean wcs;
	private boolean yun;
	
	public ConnectionStatus(boolean wcs, boolean yun) {
		this.wcs = wcs;
		this.yun = yun;
	}
	
	public boolean isConnectWcs() {
		return this.wcs;
	}
	
	public boolean isConnectYun() {
		return this.yun;
	}

}
