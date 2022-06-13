package com.ginfon.scada.gateway.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 	用于建立连接的初始化器。
 * @author Mark
 *
 */
class ScadaChannelInitializer extends ChannelInitializer<NioSocketChannel> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScadaChannelInitializer.class);
	
	private SocketConnector socketConnector;
	
	
	public ScadaChannelInitializer(SocketConnector socketConnector) {
		this.socketConnector = socketConnector;
	}

	@Override
	protected void initChannel(NioSocketChannel ch) throws Exception {
		//	输出日志
//		LOGGER.info("连接至服务器：[{}:{}]", this.socketConnector.getIp(), this.socketConnector.getPort());
		ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 2, 2, -4, 0));
		ch.pipeline().addLast(new IdleStateHandler(5, 5, 5));
		//	找到对应的处理器。
		ch.pipeline().addLast(this.socketConnector.getChannelHandler());
	}

}
