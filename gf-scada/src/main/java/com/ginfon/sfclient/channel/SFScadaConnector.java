package com.ginfon.sfclient.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SFScadaConnector {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SFScadaConnector.class);
	

	@Autowired
	private SFScadaClient sfScadaClient;
	
	/**
	 * 	Netty相关。
	 */
	private Bootstrap bootstrap;
	
	/**
	 * 	线程组。
	 */
	private EventLoopGroup workGroup;
	
	/**
	 * 	连接通道。
	 */
	private Channel channel;
	
	/**
	 * 	是否在运行。
	 */
	private volatile boolean running = false;
	
	/**
	 * 	线程。
	 */
	private Thread thread;

	@Value("${scada.socket.sfServer.ip}")
	private String sf_scada_ip;

	@Value("${scada.socket.sfServer.port}")
	private int sf_scada_port;

	@Value("${scada.socket.sfServer.reconnect}")
	private int sf_scada_reconnect;

	
	public SFScadaConnector() {}
	
	public void start() {
		if(this.thread == null) 
			this.thread = new Thread(()->{this.run();}, "GFScadaConnector");
		else
			return;
		this.bootstrap = new Bootstrap();
		this.workGroup = new NioEventLoopGroup(1);
		this.thread.start();
	}
	
	private void run() {
		this.running = true;
		this.bootstrap.group(this.workGroup)
		.channel(NioSocketChannel.class)
		.option(ChannelOption.SO_KEEPALIVE, true)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 2, 2, -4, 0));
				ch.pipeline().addLast("ping", new IdleStateHandler(4, 4, 10, TimeUnit.SECONDS));
				ch.pipeline().addLast(new StringDecoder());
				ch.pipeline().addLast(new StringEncoder());
				ch.pipeline().addLast(new SFScadaClientChannelHandler(sfScadaClient));
			}
		});
		//
		String ip = this.sf_scada_ip;
		int port = this.sf_scada_port;
		long time = this.sf_scada_reconnect;
		
		while(this.running) {
			try {
				ChannelFuture cf = this.bootstrap.connect(ip, port);
				cf.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if (!future.isSuccess()) {
							final EventLoop loop = future.channel().eventLoop();
							loop.schedule(new Runnable() {
								@Override
								public void run() {
									//LOGGER.error("顺丰SCADA服务端链接不上，开始重连操作...");
									start();
								}
							}, 2L, TimeUnit.SECONDS);
						} else {
							LOGGER.info("顺丰SCADA数据提交客户端启动成功...");
						}
					}
				});
				this.channel = cf.channel();
				//	阻塞在这里。
				cf.channel().closeFuture().sync();
			}catch (Exception e) {

				try {
					Thread.sleep(time);
				}catch (InterruptedException o) {
					o.printStackTrace();
				}
			}
			//	关闭连接
			if(this.channel != null)
				this.channel.close();
			this.channel = null;
		}
	}
	
	/**
	 * 	获取连接通道。
	 * @return	{@link Channel}
	 */
	public Channel channel() {
		return this.channel;
	}
	
	/**
	 * 	按理说这个不应该被调用。
	 */
	public void shutDown() {
		if(this.channel != null)
			this.channel.close();
		this.channel = null;
        if(this.workGroup != null)
            this.workGroup.shutdownGracefully();
        this.running = false;
	}
}
