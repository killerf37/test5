package com.ginfon.manage;

import com.ginfon.manage.handler.GfScadaServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


/**
 * @Description:通道连接初始化
 * @auther: Curtain
 * @date: 2021/6/23 16:45
 */
@Component
public class GfScadaChannelInitializer extends ChannelInitializer<NioSocketChannel> {

	@Autowired
	private GfScadaServerHandler gfScadaServerHandler;

	/**
	 * 03的明文
	 */
	private final String ASCII_03 = "";

	@Override
	protected void initChannel(NioSocketChannel ch) throws Exception {
		try {
			//	获取远程客户端的IP地址。
			InetSocketAddress inSocket = (InetSocketAddress) ch.remoteAddress();
			String ip = inSocket.getAddress().getHostAddress();
			System.out.println(ip+":"+inSocket.getPort());

			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast("idleStateHandler", new IdleStateHandler(4, 4, 10, TimeUnit.SECONDS));
			//  创建分隔符缓冲对象ASCII_03作为分割符
			ByteBuf delimiter = Unpooled.copiedBuffer(ASCII_03.getBytes());
			pipeline.addLast(new DelimiterBasedFrameDecoder(1024*10, delimiter));
			pipeline.addLast(new StringEncoder());
			pipeline.addLast(new StringDecoder());
			pipeline.addLast(gfScadaServerHandler);
			//	按照IP找到该设备的对象
			/*DeflectableWheelLine line = this.context.getSortingDeviceContainer().getSingleLine();
			//	IP相同的情况下
			if(line.getPlcIpAddress().equals(ip)) {
				PlcChannelContext ctx = line.getPlcChannelContext(super.getName());
				if(ctx == null) {
					ctx = new PlcChannelContext(super.getName(), line);
					line.addPlcChannelContext(ctx);
				}
				this.addChannelHandler(ch, ctx);
			}
			//	找不到就断开连接。
			else {
				ch.close();
			}*/
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
