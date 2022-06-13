package com.ginfon.scada.gateway.websocket.service;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ginfon.main.ScadaClientContext;
import com.ginfon.scada.gateway.websocket.controller.WebsocketRequestContoller;

/**
 * 	每隔一段时间向Websocket客户端推送连接状态信息。
 * @author Mark
 *
 */
@Component
public class ServerConnStatusPushServiceImpl implements Runnable, DisposableBean {
	
	@Autowired
	private ScadaClientContext scadaClientContext;
	
	@Autowired
	private WebsocketRequestContoller websocketRequestContoller;
	
	/**
	 * 	用来判断是否还要继续运行下去。
	 */
	private volatile boolean running = true;
	
	public ServerConnStatusPushServiceImpl() {
		
	}
	
	
	public void launch() {
		this.scadaClientContext.getExecutor().execute(this);
	}
	

	@Override
	public void run() {
		while(this.running) {
			try {
				this.websocketRequestContoller.requestConnectionStatus();
				Thread.sleep(1500);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void destroy() throws Exception {
		this.running = false;
	}

}
