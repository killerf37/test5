package com.ginfon.scada.gateway.socket;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ginfon.main.ScadaClientContext;
import com.ginfon.scada.gateway.socket.channel.SocketChannelHandler;
import com.ginfon.scada.gateway.websocket.service.ScadaMsgHandleServiceImpl;

import io.netty.channel.ChannelHandler;


@Component
public final class ScadaSocketContext implements DisposableBean {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScadaSocketContext.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private ScadaMsgHandleServiceImpl scadaFunctionServiceImpl;
	
	@Autowired
	private ScadaClientContext scadaClientContext;
	
	/**
	 * 	通道处理器的Map
	 */
	private final HashMap<String, ChannelHandler> channelHandlerMap;
	
	/**
	 * 	连接器的Map
	 */
	private final HashMap<String, SocketConnector> connectorMap;
	
	
	public ScadaSocketContext() {
		this.channelHandlerMap = new HashMap<>();
		this.connectorMap = new HashMap<>();
	}
	
	
	public void launch() {
		
		LOGGER.info("准备建立Socket连接……");
		
		String[] name = this.env.getProperty("socket.name").split(";");
		String[] ip = this.env.getProperty("socket.ip").split(";");
		String[] port = this.env.getProperty("socket.port").split(";");
		
		if(name.length == ip.length && name.length == port.length) {
			//	创建客户端连接
			for(int i = 0; i < name.length; i++) {
				//	创建通道处理器
				ChannelHandler channel = new SocketChannelHandler(name[i]);
				//	保存
				this.channelHandlerMap.put(name[i], channel);
				//	创建连接去吧
				this.createSocketConnect(name[i], ip[i], port[i]);
			}
		}else {
			LOGGER.error("Socket连接配置异常，参数数量不一致！");
		}
	}
	
	/**
	 * 	创建Socket连接。
	 * @param name
	 * @param ip
	 * @param port
	 */
	private void createSocketConnect(String name, String ip, String port) {
		try {
			int newPort = Integer.parseInt(port);
			SocketConnector connector = new SocketConnector(this, name, ip, newPort);
			this.scadaClientContext.getExecutor().execute(connector);
			//	缓存起来
			this.connectorMap.put(name, connector);
		}catch (Exception e) {
			LOGGER.error("建立Socket连接发生异常：", e);
		}
	}
	
	/**
	 * 	按名称查找对应的通讯处理器。
	 * @param key
	 * @return
	 */
	public ChannelHandler getChannelHandler(String key) {
		return this.channelHandlerMap.get(key);
	}
	
	/**
	 * 	按名称查找对应的连接器。
	 * @param key
	 * @return
	 */
	public SocketConnector getConnector(String key) {
		return this.connectorMap.get(key);
	}
	
	/**
	 * 	获取Scada功能服务实现对象。
	 * @return
	 */
	public ScadaMsgHandleServiceImpl getScadaFunctionService() {
		return this.scadaFunctionServiceImpl;
	}

	@Override
	public void destroy() throws Exception {
		//	被销毁。
		//	关闭所有的连接
		for(Map.Entry<String, SocketConnector> entry : this.connectorMap.entrySet()) {
			entry.getValue().shutdown();
		}
	}
}
