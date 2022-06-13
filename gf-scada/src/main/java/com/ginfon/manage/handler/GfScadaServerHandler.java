package com.ginfon.manage.handler;

import com.alibaba.fastjson.JSONObject;
import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.scada.gateway.websocket.service.WebsocketPushServiceImpl;
//import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @description: 连接处理类
 * @author: curtain
 * @create: 2021-06-23 15:50
 **/
@Component
@ChannelHandler.Sharable
public class GfScadaServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(GfScadaServerHandler.class);


	/**
	 * 03的明文
	 */
	private final String ASCII_03 = "";
	/**
	 * 线程池。
	 */
	private ExecutorService executor = new ThreadPoolExecutor(0, 100, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory("DataHandlerThread-"));

	@Autowired
	private GfScadaContainer gfScadaContainer;

	@Autowired
    private WebsocketPushServiceImpl websocketPushService;



	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof String){
			String jsonStr = (String) msg;
			LOGGER.info("【收到分控发来的数据：{}】", jsonStr);
			String ip=getipStr(ctx);
			gfScadaContainer.refreshOnline(ip,System.currentTimeMillis());
			gfScadaContainer.addDeviceBLmap(ip,ctx.channel());
//			ctx.writeAndFlush(jsonStr+ASCII_03);
			if(isJson(jsonStr)){
				JSONObject jo = JSONObject.parseObject(jsonStr);
				executor.execute(new DataHandler(ctx,jo,gfScadaContainer,websocketPushService));
			}
		}else{
			LOGGER.error("其他类型的消息，略...");
		}
	}

	/**
	 * 连接成功后，自动执行该方法
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		String ip = inetSocketAddress.getAddress().getHostAddress();
		int port = inetSocketAddress.getPort();
		gfScadaContainer.addDeviceBLmap(ip,ctx.channel());
		LOGGER.info("Socekt:与[{}:{}]成功建立连接！",ip,port);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		gfScadaContainer.removeDeviceBLmap(getipStr(ctx));
		/**
		 * 异常捕获
		 */
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("通道注册事件");
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		System.out.println("channelInactive");
		gfScadaContainer.removeDeviceBLmap(getipStr(ctx));
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			if (state == IdleState.READER_IDLE) {
				// 在规定时间内没有收到客户端的上行数据, 主动断开连接
				ctx.disconnect();
				LOGGER.info("读取空闲触发了");
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	public boolean isJson(String content) {
		try {
			JSONObject.parseObject(content);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取IP地址
	 * @param ctx 通道对象
	 * @param ip IP地址
	 * @param port 端口号
	 */
	public String getipStr(ChannelHandlerContext ctx)
	{
		InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		String ip = inetSocketAddress.getAddress().getHostAddress();
		//int port = inetSocketAddress.getPort();
		return ip;
	}
}
