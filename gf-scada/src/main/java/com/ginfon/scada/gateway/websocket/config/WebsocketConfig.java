package com.ginfon.scada.gateway.websocket.config;

import com.ginfon.scada.gateway.socket.util.StringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
	
	/**
	 * 	心跳周期
	 */
	private static long HEART_BEAT = 10000;
	
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		ThreadPoolTaskScheduler te = new ThreadPoolTaskScheduler();
		te.setPoolSize(1);
		te.setThreadNamePrefix("wss-heartbeat-thread-");
		te.initialize();
		// 订阅Broker名称
		registry.enableSimpleBroker("/topic","").setHeartbeatValue(new long[] { HEART_BEAT, HEART_BEAT })
				.setTaskScheduler(te);
		// 全局使用的消息前缀（客户端订阅路径上会体现出来）
		registry.setApplicationDestinationPrefixes("");
	}
	
	// registerStompEndpoints方法表示注册STOMP协议的节点，并指定映射的URL。
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 注册STOMP协议节点，同时指定使用SockJS协议。
		registry.addEndpoint("/websocket/scada").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/websocket/jktest").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/websocket/comindex").setAllowedOrigins("*").withSockJS();
	}

	@Bean(name = "stringUtil")
	public StringUtil messageUtils() {
		StringUtil messageUtils = new StringUtil();
		return messageUtils;
	}
}
