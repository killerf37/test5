package com.ginfon.sfclient.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ginfon.main.ScadaLauncher;
import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.sfclient.channel.SFScadaClient;
import com.ginfon.sfclient.channel.SFScadaMessageFrame;
import com.ginfon.sfclient.service.ISfScadaCommadService;
import com.ginfon.sfclient.util.ByteUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.ginfon.manage.container.GfScadaContainer.getChannelMap;

/**
 * 顺丰SCADA所有对接功能的实现。
 *
 * @author Mark
 */
@Service
public class SFScadaCommandServiceImpl implements ISfScadaCommadService {


	private static final Logger LOGGER = LoggerFactory.getLogger(SFScadaCommandServiceImpl.class);

	@Autowired
	private SFScadaClient sfScadaClient;

	@Autowired
	private ScadaLauncher scadaLauncher;

	@Autowired
	private GfScadaContainer gfScadaContainer;

	public SFScadaCommandServiceImpl() {
	}

	//总控的线体编号
	@Value("${scada.socket.sfServer.manageLineNo}")
	private String manageLineNo;
	//分控的线体编号
	@Value("${scada.socket.sfServer.lineNo}")
	private String lineNo;

	@Value("scada.socket.sfServer.supplier")
	private String supplier;


	/**
	 * SCADA<->ZXFJ<br>
	 * 心跳消息，这个没有任何需要注意的地方。<br>
	 * 示例：
	 */
	@Override
	public byte[] scadaHeartBeat(SFScadaMessageFrame frame) {
		//	TODO 回复一个心跳
		byte[] msg = new byte[33];

		//	=======	固定头
		msg[0] = -1;
		msg[1] = -1;
		ByteUtil.intToByte(msg.length, msg, 2, 2);
		int sn = this.sfScadaClient.serialNumber().get();
		//	计算本次消息的序列号
		ByteUtil.intToByte(sn, msg, 4, 4);
		long time = System.currentTimeMillis();
		ByteUtil.longToByte(time, msg, 9, 8);
		System.arraycopy(manageLineNo.getBytes(), 0, msg, 17, 10);//总控的线体编号
		ByteUtil.intToByte(6, msg, 27, 2);
		ByteUtil.intToByte(1, msg, 29, 2);
		ByteUtil.intToByte(4, msg, 31, 2);
		msg[8] = ByteUtil.getXor(msg);
		return msg;
	}

	/**
	 * @Description: 线体级别的启动停止
	 * @Param: [frame]
	 * @return: byte[]
	 * @Author: swenson
	 * @Date: 2021/5/27
	 */
	@Override
	public byte[] scadaStartAndStop(SFScadaMessageFrame frame) {
		LOGGER.info("[SCADA->ZXFJ]启动停止消息400:{}", ByteUtil.bytesToString16(frame.getSrcMsg()));
		byte[] body = frame.getMsgBody();
		String strbody = ByteUtil.bytesToString16(body);
		if (body.length >= 11) {
			LOGGER.info("SCADA->ZXFJ 400 报文长度为" + ByteUtil.byteToInt(body[0], body[1]));
			int msgtype = ByteUtil.byteToInt(body[2], body[3]);
//			int startType = ByteUtil.byteToInt(body[4], body[5]);//启动类型
//			gfScadaContainer.setStartTypeMap(frame.getLineNo(),startType);
			if (msgtype == 400) {
				ConcurrentLinkedQueue<SFScadaMessageFrame> queue = gfScadaContainer.getSfFrameMapByKey(frame.getLineNo());
				if (queue.isEmpty()) {
					queue = new ConcurrentLinkedQueue<>();
				}
				queue.offer(frame);
				gfScadaContainer.setSfFrameMap(frame.getLineNo(),queue);
				sengMsgToClient(frame, msgtype);
			} else {
				LOGGER.info("SCADA->ZXFJ 报文类型异常:" + strbody);
			}
		} else {
			LOGGER.info("SCADA->ZXFJ启动停止消息400报文长度实际长度有问题,长度为" + body.length + ",内容为" + strbody);
		}
		return null;
	}

	/**
	 * SCADA->ZXFJ<br> 初始话消息
	 *
	 * @param
	 * @return
	 * @author SWENSON
	 * @created 2021/5/11
	 * 初始化消息<br>
	 */
	@Override
	public byte[] scadaInit(SFScadaMessageFrame frame) {
		LOGGER.info("[SCADA->ZXFJ]初始化消息:2 :{}", ByteUtil.bytesToString16(frame.getSrcMsg()));
		byte[] body = frame.getMsgBody();
		String strbody = ByteUtil.bytesToString16(body);
		if (body.length >= 12) {
			if (ByteUtil.byteToInt(body[0], body[1]) == 12) {
				int msgtype = ByteUtil.byteToInt(body[2], body[3]);
				if (msgtype == 2) {
					long initTime = ByteUtil.byteToLong(body[4], body[5], body[6], body[7], body[8], body[9], body[10], body[11]);
					String[] sfLineNos = this.lineNo.split(";");
					int msgLength = sfLineNos.length * 10 + 10 + 27;
					byte[] back = new byte[msgLength];

					//	=======	固定头
					back[0] = -1;
					back[1] = -1;
					ByteUtil.intToByte(back.length, back, 2, 2);
					int sn = this.sfScadaClient.serialNumber().get();
					//	计算本次消息的序列号
					ByteUtil.intToByte(sn, back, 4, 4);
					long time = System.currentTimeMillis();
					ByteUtil.longToByte(time, back, 9, 8);
					System.arraycopy(sfLineNos[0].getBytes(), 0, back, 17, 10);
					ByteUtil.intToByte(sfLineNos.length * 10 + 10, back, 27, 2);
					ByteUtil.intToByte(3, back, 29, 2);
					ByteUtil.intToByte(sfLineNos.length, back, 31, 2);
					System.arraycopy(lineNo.replaceAll(";", "").getBytes(), 0, back, 33, sfLineNos.length * 10);
					System.arraycopy(supplier.getBytes(), 0, back, msgLength - 5, 4);
					back[8] = ByteUtil.getXor(back);
					this.sfScadaClient.connector().channel().writeAndFlush(Unpooled.wrappedBuffer(back));
					LOGGER.info("[ZXFJ->SCADA]初始化响应消息:3 报文:{}", ByteUtil.bytesToString16(back));
				} else {
					LOGGER.info("SCADA->ZXFJ 报文类型异常:" + strbody);
				}
			} else {
				LOGGER.info("SCADA->ZXFJ报文内容长度有问题,长度标明为" + ByteUtil.byteToInt(body[0], body[1]) + ",内容为" + strbody);
			}
		} else {
			LOGGER.info("SCADA->ZXFJ初始化报文长度实际长度有问题,长度为" + body.length + ",内容为" + strbody);
		}
		return null;
	}

	@Override
	public byte[] scadaOperate(SFScadaMessageFrame frame) {
		LOGGER.info("[SCADA->ZXFJ]操作消息421:{}", ByteUtil.bytesToString16(frame.getSrcMsg()));
		byte[] body = frame.getMsgBody();
		String strbody = ByteUtil.bytesToString16(body);
		if (body.length >= 13) {
			LOGGER.info("SCADA->ZXFJ 421 报文长度为" + ByteUtil.byteToInt(body[0], body[1]));
			int msgtype = ByteUtil.byteToInt(body[2], body[3]);
			if (msgtype == 421) {
				ConcurrentLinkedQueue<SFScadaMessageFrame> queue = gfScadaContainer.getSfFrameMapByKey(frame.getLineNo());
				if (queue.isEmpty()) {
					queue = new ConcurrentLinkedQueue<>();
				}
				queue.offer(frame);
				gfScadaContainer.setSfFrameMap(frame.getLineNo(),queue);
//				int operateType = ByteUtil.byteToInt(body[4], body[5]);//启动类型
//				gfScadaContainer.setStartTypeMap(frame.getLineNo(), operateType);
				sengMsgToClient(frame, msgtype);
			} else {
				LOGGER.info("SCADA->ZXFJ 报文类型异常:" + strbody);
			}
		} else {
			LOGGER.info("SCADA->ZXFJ操作消息421报文长度实际长度有问题,长度为" + body.length + ",内容为" + strbody);
		}
		return null;
	}

	/**
	 * @Description:[SCADA->ZXFJ]参数设定434
	 * @auther: Curtain
	 * @date: 2021/7/14 14:06
	 * @param:
	 * @return:
	 */
	@Override
	public byte[] scadaSetting(SFScadaMessageFrame frame) {
		LOGGER.info("[SCADA->ZXFJ]参数设定434:{}", ByteUtil.bytesToString16(frame.getSrcMsg()));
		byte[] body = frame.getMsgBody();
		String strbody = ByteUtil.bytesToString16(body);
		if (body.length >= 13) {
			LOGGER.info("SCADA->ZXFJ 434 报文长度为" + ByteUtil.byteToInt(body[0], body[1]));
			int msgtype = ByteUtil.byteToInt(body[2], body[3]);
			if (msgtype == 434) {
				String sfLineNo = frame.getLineNo();
				ConcurrentLinkedQueue<SFScadaMessageFrame> queue = gfScadaContainer.getSfFrameMapByKey(sfLineNo);
				if (queue.isEmpty()) {
					queue = new ConcurrentLinkedQueue<>();
				}
				queue.offer(frame);
				gfScadaContainer.setSfFrameMap(sfLineNo,queue);
				if (sfLineNo.contains("SYS")) {
					// 全场所有线体设定
					Map<String, NioSocketChannel> map = gfScadaContainer.getChannelMap();
					for (NioSocketChannel nsc : map.values()) {
						JSONObject jo = new JSONObject();
						jo.put("frame", frame);
						jo.put("type", msgtype);
						nsc.writeAndFlush(jo.toJSONString());
						LOGGER.info("总控->分控发送报文成功，消息类型：【{}】，报文内容:【{}】", msgtype, jo.toJSONString());
					}
				} else {
					//本机线体设定
					sengMsgToClient(frame, msgtype);
				}
			} else {
				LOGGER.info("SCADA->ZXFJ 报文类型异常:" + strbody);
			}
		} else {
			LOGGER.info("SCADA->ZXFJ参数设定434报文长度实际长度有问题,长度为" + body.length + ",内容为" + strbody);
		}
		return null;
	}

	private void sengMsgToClient(SFScadaMessageFrame frame, int msgtype) {
		NioSocketChannel nsc = gfScadaContainer.get(frame.getLineNo());//根据线体号即别名找出对应的socketchannel，再由分控发送控制指令给plc
		JSONObject jo = new JSONObject();
		jo.put("frame", frame);
		jo.put("type", msgtype);
		nsc.writeAndFlush(jo.toJSONString());
		LOGGER.info("总控->分控发送报文成功，消息类型：【{}】，报文内容:【{}】", msgtype, jo.toJSONString());
	}

}
