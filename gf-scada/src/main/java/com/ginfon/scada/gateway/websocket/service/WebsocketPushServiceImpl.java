package com.ginfon.scada.gateway.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 	前端Websocket的数据推送服务。
 * @Author: James
 * @Date: 2020/3/20 14:20
 * @Description: 向websocket客户端推送消息
 */
@Component
public class WebsocketPushServiceImpl {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketPushServiceImpl.class);
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	
	public WebsocketPushServiceImpl() {
		
	}
	
	/**
	 * 	推送各种数据。
	 * @param url
	 * @param msg
	 */
	public void pushMessage(String url, Object msg) {
//		LOGGER.info("向Websocket客户端推送消息：[{}]-[{}]", url, msg.getClass());
		this.messagingTemplate.convertAndSend(url, msg);
	}
}
