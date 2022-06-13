package com.ginfon.sfclient.channel;


/**
 * 	基本的通信消息体。
 * @author Mark
 *
 */
public class BaseSocketMessageFrame {

	/**
	 * 	请求时间。
	 */
	private long time;
	
	/**
	 * 	完整的源数据报文。
	 */
	private byte[] srcMsg;
	
	/**
	 * 	去掉消息头的消息体。
	 */
	private byte[] msgBody;
	
	/**
	 * 	这条报文的序列号。放在这里只是为了省得再算了。
	 */
	private int sn;
	
	/**
	 * 	功能代码
	 */
	private int functionCode;

    public BaseSocketMessageFrame(byte[] srcMsg, byte[] msgBody, int sn, int functionCode, long time) {
		this.srcMsg = srcMsg;
		this.msgBody = msgBody;
		this.sn = sn;
		this.functionCode = functionCode;
		this.time = time;
	}

	
	public long getTime() {
		return time;
	}

	public byte[] getSrcMsg() {
		return srcMsg;
	}

	public byte[] getMsgBody() {
		return msgBody;
	}

	public int getSN() {
		return sn;
	}

	public int getFunctionCode() {
		return functionCode;
	}
}
