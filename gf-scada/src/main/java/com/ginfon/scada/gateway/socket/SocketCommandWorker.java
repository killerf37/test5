package com.ginfon.scada.gateway.socket;

import com.ginfon.scada.gateway.common.SpringContextUtils;
import com.ginfon.scada.gateway.socket.channel.SocketChannelHandler;
import com.ginfon.scada.gateway.websocket.service.ScadaMsgHandleServiceImpl;

/**
 * 	异步加工处理报文。
 * @author Mark
 *
 */
public class SocketCommandWorker implements Runnable {

	/**
	 * 	功能代码。
	 */
	private int functionCode;
	
	/**
	 * 	消息体。
	 */
	private byte[] msgBody;
	
	/**
	 * 	PLC的原始消息
	 */
	private byte[] srcMsg;
	
	/**
	 * 	消息序列号。
	 */
	private int index;
	
	/**
	 * 	请求时间。
	 */
	private long time;
	
	
	private SocketChannelHandler channelHandler;
	
	public SocketCommandWorker(byte[] srcMsg, byte[] msgBody, int index, int functionCode, long time, SocketChannelHandler channelHandler) {
		this.srcMsg = srcMsg;
		this.msgBody = msgBody;
		this.functionCode = functionCode;
		this.channelHandler = channelHandler;
		this.time = time;
		this.index = index;
	}
	
	@Override
	public void run() {
		ScadaMsgHandleServiceImpl service = SpringContextUtils.getBean(ScadaMsgHandleServiceImpl.class);
		service.handle(this.channelHandler, this.srcMsg, this.msgBody, this.index, this.functionCode, this.time);
	}
}
