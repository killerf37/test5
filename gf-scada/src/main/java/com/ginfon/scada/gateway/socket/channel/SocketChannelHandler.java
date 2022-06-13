package com.ginfon.scada.gateway.socket.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ginfon.scada.gateway.socket.SocketCommandWorker;
import com.ginfon.scada.util.ByteUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * 	处理收到的Socket报文。
 * @author Mark
 *
 */
@Sharable
public class SocketChannelHandler extends ChannelInboundHandlerAdapter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SocketChannelHandler.class);
	
	private String name;
	
	public SocketChannelHandler(String name) {
		this.name = name;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("Socekt:与[{}:{}]成功建立连接！");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//	取得收到消息的时间，这个需要尽快拿到，否则容易产生时间差影响判断。
		long time = System.currentTimeMillis();
		if(msg instanceof ByteBuf) {
			ByteBuf buf = (ByteBuf) msg;
			byte[] body = new byte[buf.readableBytes()];
			buf.readBytes(body);
			//
			//	先判断这个报文是否符合协议，万一有傻逼发了奇奇怪怪的错误报文呢？
			if(this.checkMessage(body)) {
				LOGGER.info("Socket:[{}]收到报文：[{}]", this.name, ByteUtil.bytesToString16(body));
				//	消息体的长度
				int msgBodyLen = ByteUtil.byteToInt(body[6], body[7]);
				//	
				//	消息的序列号
				int index = ByteUtil.byteToInt(body[4], body[5]);
				//	如果消息长度和描述长度一致就行
				if(msgBodyLen + 6 == body.length) {
					//	把消息体拷贝出来
					byte[] msgBody = new byte[body.length - 6];
					System.arraycopy(body, 6, msgBody, 0, msgBody.length);
					//	交给其它类去处理
					SocketCommandWorker worker = new SocketCommandWorker(body, msgBody, index, ByteUtil.byteToInt(msgBody[2], msgBody[3]), time, this);
					//	临时方案
					new Thread(worker).start();
				}else {
					
				}
			}
			//
			buf.release();
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		//LOGGER.debug("该发心跳了");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.debug("发生异常：", cause);
	}
	
	/**
	 * 	检查报文是否为该处理器处理，或者说是否符合协议规范。
	 * @param ctx	上下文对象
	 * @param msg	消息
	 * @return		符合规范=true
	 */
	private boolean checkMessage(byte[] msg) {
		if(msg == null)
			return false;
		//	报文头不符合协议。
		if (ByteUtil.byteToInt(msg[0], msg[1]) != 0xFFFF || msg.length <= 6)
			return false;
		return true;
	}
}
