package com.ginfon.sfclient.thread;

import com.ginfon.main.ScadaLauncher;
import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.scada.util.SerialNumber;
import com.ginfon.sfclient.channel.SFScadaClient;
import com.ginfon.sfclient.channel.SFScadaMessageFrame;
import com.ginfon.sfclient.util.ByteUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @description: 顺丰scada部分消息定时回复响应线程类
 * @author: curtain
 * @create: 2021-07-19 13:42
 **/
public class SfScadaResponseThread implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(SfScadaResponseThread.class);

	private GfScadaContainer gfScadaContainer;

	private SFScadaClient sfScadaClient;
	private ScadaLauncher scadaLauncher;

	/**
	 * 序列号生成器。
	 */
	private SerialNumber serialNumber;

	public SfScadaResponseThread(SFScadaClient sfScadaClient, GfScadaContainer gfScadaContainer, ScadaLauncher scadaLauncher) {
		this.sfScadaClient = sfScadaClient;
		this.gfScadaContainer = gfScadaContainer;
		this.scadaLauncher = scadaLauncher;
		this.serialNumber = new SerialNumber();
	}

	@Override
	public void run() {
		Map<String, ConcurrentHashMap<String, Integer>> deviceStatusMap = gfScadaContainer.getDeviceStatusMap();//获取单机设备状态Map
		Map<String, ConcurrentHashMap<String, Integer>> lineStatusMap = gfScadaContainer.getLineStatusMap();//获取整线状态Map
		Map<String, ConcurrentLinkedQueue<SFScadaMessageFrame>> sfFrameMap = gfScadaContainer.getSfFrameMap();
		for (String key : sfFrameMap.keySet()) {
			ConcurrentLinkedQueue<SFScadaMessageFrame> queue = sfFrameMap.get(key);
			while (!queue.isEmpty()) {
				SFScadaMessageFrame frame = queue.poll();
				int functionCode = frame.getFunctionCode();
				switch (functionCode) {
					case 400:
						startAndStopRes(deviceStatusMap, lineStatusMap, frame);//300
						break;
					case 421:
						funcStatusRes(frame);//321
						break;
					case 434:
						deviceSettingRes(frame);//334
						break;
				}
			}
		}
	}

	/**
	 * @Description: 启动/停止响应 300（client->SCADA）
	 * @auther: Curtain
	 * @date: 2021/7/19 13:45
	 */
	private void startAndStopRes(Map<String, ConcurrentHashMap<String, Integer>> deviceStatusMap, Map<String, ConcurrentHashMap<String, Integer>> lineStatusMap, SFScadaMessageFrame frame) {
		//开始解析SFScadaMessageFrame
		byte[] msgBody = frame.getMsgBody();
		String aliasName = frame.getLineNo();
		for (int i = 4; i < msgBody.length; i += 11) {//可能会传多个设备控制命令
			int operateDeviceType = ByteUtil.byteToInt(msgBody[i], msgBody[i + 1]);
//			int startAndStop = ByteUtil.byteToInt(msgBody[i + 2]);
//			int level = ByteUtil.byteToInt(msgBody[i + 3], msgBody[i + 4]);
//			int speed = ByteUtil.byteToInt(msgBody[i + 5], msgBody[i + 6]);
			int deviceType = ByteUtil.byteToInt(msgBody[i + 7], msgBody[i + 8]);
			int deviceNo = ByteUtil.byteToInt(msgBody[i + 9], msgBody[i + 10]);

			//组装响应报文
			int msgLength = 31 + 7;
			byte[] message = new byte[msgLength];
			message[0] = -1;
			message[1] = -1;
			if (operateDeviceType == 2) {//单机
				//根据顺丰发来的设备类型，定位到金峰对应设备，并在map中找出相应状态
				ConcurrentHashMap<String, Integer> singleMap = deviceStatusMap.get(aliasName);//根据线体号，获取相应的Map(aliasName_gfDeviceType_deviceNo_0)
				int gfDeviceType = this.scadaLauncher.getGfDeviecTypeMap().get(deviceType);
				ByteUtil.intToByte(msgLength, message, 2, 2);
				ByteUtil.intToByte(serialNumber.get(), message, 4, 4);
				ByteUtil.longToByte(System.currentTimeMillis(), message, 9, 8);
				System.arraycopy(aliasName.getBytes(), 0, message, 17, 10);
				ByteUtil.intToByte(msgLength - 27, message, 27, 2);
				ByteUtil.intToByte(300, message, 29, 2);
				byte[] body = new byte[7];
				int gfStatus = singleMap.get(aliasName + "_" + gfDeviceType + "_" + deviceNo + "_0");
				gfStatus = gfStatus & 0xFF;
				int sfDeviceType = scadaLauncher.getDeviceTypemMap().get(gfDeviceType);
				int sfStatus = scadaLauncher.getDeviceStatusConvertMap().get(sfDeviceType + "_" + gfStatus);
				ByteUtil.intToByte(operateDeviceType, body, i * 7, 2);
				ByteUtil.intToByte(sfStatus, body, i * 7 + 2, 2);
				ByteUtil.intToByte(sfDeviceType, body, i * 7 + 3, 2);
				ByteUtil.intToByte(deviceNo, body, i * 7 + 5, 2);
				System.arraycopy(body, 0, message, 31, 7);
				this.sfScadaClient.connector().channel().writeAndFlush(Unpooled.wrappedBuffer(message));
			} else {//整线
				ConcurrentHashMap<String, Integer> lineMap = lineStatusMap.get(aliasName);//根据线体号，获取相应的Map
				ByteUtil.intToByte(msgLength, message, 2, 2);
				ByteUtil.intToByte(serialNumber.get(), message, 4, 4);
				ByteUtil.longToByte(System.currentTimeMillis(), message, 9, 8);
				System.arraycopy(aliasName.getBytes(), 0, message, 17, 10);
				ByteUtil.intToByte(msgLength - 27, message, 27, 2);
				ByteUtil.intToByte(300, message, 29, 2);
				byte[] body = new byte[7];
				int state = lineMap.values().iterator().next();
				state = convertResult(state & 0xFF);
				ByteUtil.intToByte(operateDeviceType, body, 0, 2);
				ByteUtil.intToByte(state, body, 2, 2);
				ByteUtil.intToByte(0, body, 3, 2);
				ByteUtil.intToByte(0, body, 5, 2);
				System.arraycopy(body, 0, message, 31, 7);
				this.sfScadaClient.connector().channel().writeAndFlush(Unpooled.wrappedBuffer(message));
			}
		}
	}

	private int convertResult(int result) {
		switch (result) {
			case 2:
				result = 1;
			case 1:
				result = 2;
			case 9:
				result = 3;
				break;
		}
		return result;
	}

	/**
	 * @Description: 功能状态上报 321（client->SCADA） ，原样返回即可
	 * @auther: Curtain
	 * @date: 2021/7/19 13:54
	 */
	private void funcStatusRes( SFScadaMessageFrame frame) {
		//开始解析SFScadaMessageFrame
		byte[] msgBody = frame.getMsgBody();
		String aliasName = frame.getLineNo();
		//组装响应报文
		int msgLength = 27 + msgBody.length;
		byte[] message = new byte[msgLength];
		message[0] = -1;
		message[1] = -1;
		ByteUtil.intToByte(msgLength, message, 2, 2);
		ByteUtil.intToByte(serialNumber.get(), message, 4, 4);
		ByteUtil.longToByte(System.currentTimeMillis(), message, 9, 8);
		System.arraycopy(aliasName.getBytes(), 0, message, 17, 10);
		ByteUtil.intToByte(msgBody.length, message, 27, 2);
		ByteUtil.intToByte(321, message, 29, 2);
		int cnt = 0;
		for (int i = 4; i < msgBody.length; i += 9) {//可能会传多个设备控制命令
			int operateDeviceType = ByteUtil.byteToInt(msgBody[i], msgBody[i + 1]);
			int deviceType = ByteUtil.byteToInt(msgBody[i + 2], msgBody[i + 3]);
			int deviceNo = ByteUtil.byteToInt(msgBody[i + 4], msgBody[i + 5]);
			int operateType = ByteUtil.byteToInt(msgBody[i + 6], msgBody[i + 7]);
			int operateFlag = ByteUtil.byteToInt(msgBody[i + 8]);
			byte[] body = new byte[9];
			ByteUtil.intToByte(operateDeviceType, body, 0, 2);
			ByteUtil.intToByte(deviceType, body, 2, 2);
			ByteUtil.intToByte(deviceNo, body, 4, 2);
			ByteUtil.intToByte(operateType, body, 6, 2);
			ByteUtil.intToByte(operateFlag, body, 8, 1);
			System.arraycopy(body, 0, message, 31 + 9 * cnt++, 9);
		}
		this.sfScadaClient.connector().channel().writeAndFlush(Unpooled.wrappedBuffer(message));
	}

	/**
	 * @Description: 设备参数上报 334（client->SCADA），原样返回即可
	 * @auther: Curtain
	 * @date: 2021/7/19 13:54
	 */
	private void deviceSettingRes(SFScadaMessageFrame frame) {
		//开始解析SFScadaMessageFrame
		byte[] msgBody = frame.getMsgBody();
		String aliasName = frame.getLineNo();
		//组装响应报文
		int msgLength = 27 + msgBody.length;
		byte[] message = new byte[msgLength];
		message[0] = -1;
		message[1] = -1;
		ByteUtil.intToByte(msgLength, message, 2, 2);
		ByteUtil.intToByte(serialNumber.get(), message, 4, 4);
		ByteUtil.longToByte(System.currentTimeMillis(), message, 9, 8);
		System.arraycopy(aliasName.getBytes(), 0, message, 17, 10);
		ByteUtil.intToByte(msgBody.length, message, 27, 2);
		ByteUtil.intToByte(334, message, 29, 2);
		int cnt = 0;
		for (int i = 4; i < msgBody.length; i += 4) {//可能会传多个设备控制命令
			int settingType = ByteUtil.byteToInt(msgBody[i], msgBody[i + 1]);
			int settingValue = ByteUtil.byteToInt(msgBody[i + 2], msgBody[i + 3]);
			byte[] body = new byte[4];
			ByteUtil.intToByte(settingType, body, 0, 2);
			ByteUtil.intToByte(settingValue, body, 2, 2);
			System.arraycopy(body, 0, message, 31 + 4 * cnt++, 4);
		}
		this.sfScadaClient.connector().channel().writeAndFlush(Unpooled.wrappedBuffer(message));
	}
}
