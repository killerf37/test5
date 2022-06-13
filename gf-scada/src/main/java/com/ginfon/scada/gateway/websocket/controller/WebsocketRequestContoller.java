package com.ginfon.scada.gateway.websocket.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ginfon.core.web.BaseController;
import com.ginfon.main.ScadaClientContext;
import com.ginfon.scada.config.Constants;
import com.ginfon.scada.gateway.socket.ScadaSocketContext;
import com.ginfon.scada.gateway.websocket.config.dto.ConnectionStatus;
import com.ginfon.scada.gateway.websocket.service.WebsocketPushServiceImpl;
import com.ginfon.scada.util.ByteUtil;
import com.ginfon.scada.util.SerialNumber;

/**
 * 	Websocket的请求都会在这里被处理。
 * @author Mark
 *
 */
@Controller
public class WebsocketRequestContoller extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketRequestContoller.class);
	
	@Autowired
	private ScadaSocketContext scadaSocketContext;
	
	@Autowired
	private WebsocketPushServiceImpl webSocketPushService;
	
	@Autowired
	private ScadaClientContext scadaClientContext;
	
	/**
	 * 	用于累加报文序列号的原子计数器。
	 */
	private SerialNumber serialNumber;
	
	
	public WebsocketRequestContoller() {
		this.serialNumber = new SerialNumber();
	}

	/**
	 * 	请求WCS和金峰云的连接状态。
	 */
	@MessageMapping(Constants.REQUEST_URL_FOR_SERVER_CONN_STATUS)
	public void requestConnectionStatus() {
		boolean wcs = this.scadaSocketContext.getConnector("WCS").isConnected();
		boolean yun = this.scadaSocketContext.getConnector("YUN").isConnected();
		//	创建结构体
		ConnectionStatus ic = new ConnectionStatus(wcs, yun);
		this.webSocketPushService.pushMessage(Constants.TOPIC_URL_FOR_SERVER_CONN_STATUS, ic);
	}
	
	/**
	 * 对应功能码203。设置线体堵包功能启用或者禁用。
	 * @param request
	 */
	@MessageMapping(Constants.REQUEST_URL_FOR_CONTROL_BLOCKING)
	public void requestSwitchBlocking(JSONObject request) {
		//	目标线体号
		String lineNo = request.getString("lineNo");
		//	1是禁用2是启用
		String funtype = request.getString("funtype");
		//	报警时长
		String cloggedtime = request.getString("time");
		
		byte[] requestMsg = new byte[16];
		//	FF FF
		requestMsg[0] = -1;
		requestMsg[1] = -1;
		//	长度
		requestMsg[2] = 0;
		requestMsg[3] = 0x10;
		//	序列号
		ByteUtil.intToByte(this.serialNumber.get(), requestMsg, 4, 2);
		//	报文体长度
		requestMsg[6] = 0;
		requestMsg[7] = 0x0A;
		//	报文体的功能码
		ByteUtil.intToByte(203, requestMsg, 8, 2);
		//	目标线体号
		ByteUtil.intToByte(Integer.parseInt(lineNo), requestMsg, 10, 2);
		//	设定时长
		ByteUtil.intToByte(Integer.parseInt(cloggedtime), requestMsg, 12, 2);
		//	启用还是禁用
		ByteUtil.intToByte(Integer.parseInt(funtype), requestMsg, 14, 2);
		//	发送消息
		this.scadaSocketContext.getConnector("WCS").send(requestMsg);
		//
		LOGGER.info("[功能码:203]向WCS发送报文：[{}]", ByteUtil.bytesToString16(requestMsg));
	}
	
	/**
	 * 	对应功能码103。启动或者停止线体。
	 * @param request
	 */
	@MessageMapping(Constants.REQUEST_URL_FOR_CONTROL_LINE_DEVICE)
	public void requestControlLine(JSONObject request) {
		
		if(!this.checkConnectionStatus())
			return;
		
		//	目标线体号
		String lineNo = request.getString("lineNo");
		//	1启动2停止3左偏4回中5右偏
		String funtype = request.getString("funtype");
		//	创建报文数组
		byte[] requestMsg = new byte[16];
		//	FF FF
		requestMsg[0] = -1;
		requestMsg[1] = -1;
		//	长度
		requestMsg[2] = 0;
		requestMsg[3] = 0x10;
		//	序列号
		ByteUtil.intToByte(this.serialNumber.get(), requestMsg, 4, 2);
		//	报文体长度
		requestMsg[6] = 0;
		requestMsg[7] = 0x0A;
		//	报文体的功能码
		ByteUtil.intToByte(103, requestMsg, 8, 2);
		//	目标线体号
		ByteUtil.intToByte(Integer.parseInt(lineNo), requestMsg, 10, 2);
		ByteUtil.intToByte(0, requestMsg, 12, 2);
		//	1启动2停止3左偏4回中5右偏
		ByteUtil.intToByte(Integer.parseInt(funtype), requestMsg, 14, 2);
		//	发送消息
		this.scadaSocketContext.getConnector("WCS").send(requestMsg);
		//
		LOGGER.info("[功能码:103]向WCS发送报文：[{}]", ByteUtil.bytesToString16(requestMsg));
	}

	/**
	 * 对应功能码202。查询是否启用堵包状态。
	 * 
	 * @param request
	 */
	@MessageMapping(Constants.REQUEST_URL_FOR_BLOCKING_STATUS)
	public void requestBlockingStatus(JSONObject request) {
		
		if(!this.checkConnectionStatus())
			return;
		
		JSONArray lineNo = request.getJSONArray("lineNo");
		//	根据线体号码计算报文整体长度
		byte[] requestMsg = new byte[10 + lineNo.size() * 4];
		//	FF FF
		requestMsg[0] = -1;
		requestMsg[1] = -1;
		//	长度
		ByteUtil.intToByte(requestMsg.length, requestMsg, 2, 2);
		//	序列号
		ByteUtil.intToByte(this.serialNumber.get(), requestMsg, 4, 2);
		//	报文体长度
		ByteUtil.intToByte(requestMsg.length - 6, requestMsg, 6, 2);
		//	报文体的功能码
		ByteUtil.intToByte(202, requestMsg, 8, 2);
		//	目标线体号
		for(int i = 0; i < lineNo.size(); i++) {
			ByteUtil.intToByte(Integer.parseInt(lineNo.getString(i)), requestMsg, 10 + (i * 4), 2);
		}
		//	发送消息
		this.scadaSocketContext.getConnector("WCS").send(requestMsg);
		//	
		LOGGER.info("[功能码:202]向WCS发送报文：[{}]", ByteUtil.bytesToString16(requestMsg));
	}
	
	/**
	 * 	对应功能码201。请求输送线运行状态。
	 */
	@MessageMapping(Constants.REQUEST_URL_FOR_LING_STATUS)
	public void requestLineStatus(JSONObject request) {
		
		if(!this.checkConnectionStatus())
			return;
		
		//	取出线体号
		String lineNo = request.getString("lineNo");
		//	取出设备号
		String deviceNo = request.getString("deviceNo");
		
		if(lineNo.equals("all") && deviceNo.equals("all")) {
			//	创建报文数组
			byte[] requestMsg = new byte[10 + this.scadaClientContext.getDeviceMap().size() * 4];
			//	FF FF
			requestMsg[0] = -1;
			requestMsg[1] = -1;
			//	长度
			ByteUtil.intToByte(requestMsg.length, requestMsg, 2, 2);
			//	序列号
			ByteUtil.intToByte(this.serialNumber.get(), requestMsg, 4, 2);
			//	报文体长度
			ByteUtil.intToByte(requestMsg.length - 6, requestMsg, 6, 2);
			//	报文体的功能码
			ByteUtil.intToByte(201, requestMsg, 8, 2);
			
			int index = 0;
			//	取出所有的线体
			for(Map.Entry<String, String> entry : this.scadaClientContext.getDeviceMap().entrySet()) {
				//	拼凑报文
				ByteUtil.intToByte(Integer.parseInt(entry.getKey()), requestMsg, 10 + (index * 4), 2);
				index++;
			}
			//	发送消息
			this.scadaSocketContext.getConnector("WCS").send(requestMsg);
			//	
			LOGGER.info("[功能码:201]向WCS发送报文：[{}]", ByteUtil.bytesToString16(requestMsg));
		}
	}
	
	/**
	 * 	对应功能码205。设置线体的休眠时长。<br>
	 * @param request
	 */
	@MessageMapping(Constants.REQUEST_URL_FOR_SET_SLEEP_TIME)
	public void requestControlLineSleepTime(JSONObject request) {
		if(!this.checkConnectionStatus())
			return;
		String lineNo = request.getString("lineNo");
		String cloggedTime = request.getString("time");
		//	创建报文数组，该请求固定16位长度
		byte[] requestMsg = new byte[16];
		//	FF FF
		requestMsg[0] = -1;
		requestMsg[1] = -1;
		//	长度
		requestMsg[2] = 0x00;
		requestMsg[3] = 0x10;
		//	序列号
		ByteUtil.intToByte(this.serialNumber.get(), requestMsg, 4, 2);
		//	报文体的长度
		requestMsg[6] = 0x00;
		requestMsg[7] = 0x0A;
		//	报文体的功能码
		ByteUtil.intToByte(205, requestMsg, 8, 2);
		//	设置线体号码
		ByteUtil.intToByte(Integer.parseInt(lineNo), requestMsg, 10, 2);
		//	没意义的0位
		requestMsg[12] = 0x00;
		requestMsg[13] = 0x00;
		//	设置时间
		ByteUtil.intToByte(Integer.parseInt(cloggedTime), requestMsg, 14, 2);
		//	发送消息
		this.scadaSocketContext.getConnector("WCS").send(requestMsg);
		LOGGER.info("[功能码:205]向WCS发送报文：[{}]", ByteUtil.bytesToString16(requestMsg));
	}
	
	/**
	 * 	对应功能码206。设置线体的启动时长。
	 * @param request
	 */
	@MessageMapping(Constants.REQUEST_URL_FOR_SET_START_TIME)
	public void requestControlLineStartTime(JSONObject request) {
		if(!this.checkConnectionStatus())
			return;
		String lineNo = request.getString("lineNo");
		String cloggedTime = request.getString("time");
		//	创建报文数组，该请求固定16位长度
		byte[] requestMsg = new byte[16];
		//	FF FF
		requestMsg[0] = -1;
		requestMsg[1] = -1;
		//	长度
		requestMsg[2] = 0x00;
		requestMsg[3] = 0x10;
		//	序列号
		ByteUtil.intToByte(this.serialNumber.get(), requestMsg, 4, 2);
		//	报文体的长度
		requestMsg[6] = 0x00;
		requestMsg[7] = 0x0A;
		//	报文体的功能码
		ByteUtil.intToByte(206, requestMsg, 8, 2);
		//	设置线体号码
		ByteUtil.intToByte(Integer.parseInt(lineNo), requestMsg, 10, 2);
		//	没意义的0位
		requestMsg[12] = 0x00;
		requestMsg[13] = 0x00;
		//	设置时间
		ByteUtil.intToByte(Integer.parseInt(cloggedTime), requestMsg, 14, 2);
		//	发送消息
		this.scadaSocketContext.getConnector("WCS").send(requestMsg);
		LOGGER.info("[功能码:206]向WCS发送报文：[{}]", ByteUtil.bytesToString16(requestMsg));
	}
	
	/**
	 * 	对应功能码207。查询线体设定的休眠时长。
	 * @param request
	 */
	@MessageMapping(Constants.REQUEST_URL_FOR_SLEEP_TIME)
	public void requestLineSleepTime(JSONObject request) {
		if(!this.checkConnectionStatus())
			return;
		JSONArray lineNo = request.getJSONArray("lineNo");
		//	根据线体号码计算报文整体长度
		byte[] requestMsg = new byte[10 + lineNo.size() * 4];
		//	FF FF
		requestMsg[0] = -1;
		requestMsg[1] = -1;
		//	长度
		ByteUtil.intToByte(requestMsg.length, requestMsg, 2, 2);
		//	序列号
		ByteUtil.intToByte(this.serialNumber.get(), requestMsg, 4, 2);
		//	报文体长度
		ByteUtil.intToByte(requestMsg.length - 6, requestMsg, 6, 2);
		//	报文体的功能码
		ByteUtil.intToByte(207, requestMsg, 8, 2);
		//	目标线体号
		for(int i = 0; i < lineNo.size(); i++) {
			ByteUtil.intToByte(Integer.parseInt(lineNo.getString(i)), requestMsg, 10 + (i * 4), 2);
		}
		//	发送消息
		this.scadaSocketContext.getConnector("WCS").send(requestMsg);
		//	
		LOGGER.info("[功能码:207]向WCS发送报文：[{}]", ByteUtil.bytesToString16(requestMsg));
	}
	
	/**
	 * 	对应功能码208。查询线体设定的启动时长。
	 * @param request
	 */
	@MessageMapping(Constants.REQUEST_URL_FOR_START_TIME)
	public void requestLineStartTime(JSONObject request) {
		if(!this.checkConnectionStatus())
			return;
		JSONArray lineNo = request.getJSONArray("lineNo");
		//	根据线体号码计算报文整体长度
		byte[] requestMsg = new byte[10 + lineNo.size() * 4];
		//	FF FF
		requestMsg[0] = -1;
		requestMsg[1] = -1;
		//	长度
		ByteUtil.intToByte(requestMsg.length, requestMsg, 2, 2);
		//	序列号
		ByteUtil.intToByte(this.serialNumber.get(), requestMsg, 4, 2);
		//	报文体长度
		ByteUtil.intToByte(requestMsg.length - 6, requestMsg, 6, 2);
		//	报文体的功能码
		ByteUtil.intToByte(208, requestMsg, 8, 2);
		//	目标线体号
		for(int i = 0; i < lineNo.size(); i++) {
			ByteUtil.intToByte(Integer.parseInt(lineNo.getString(i)), requestMsg, 10 + (i * 4), 2);
		}
		//	发送消息
		this.scadaSocketContext.getConnector("WCS").send(requestMsg);
		//	
		LOGGER.info("[功能码:208]向WCS发送报文：[{}]", ByteUtil.bytesToString16(requestMsg));
	}
	
	/**
	 * 	对应功能码211。
	 * @param request
	 */
	@MessageMapping(Constants.REQUEST_URL_FOR_CONTROL_COMMAND)
	public void controlCommand(JSONObject request) {
		String lineNo = request.getString("lineNo");
		String funtype = request.getString("funtype");
		String enable = request.getString("enable");
		//	创建报文数组，该请求固定16位长度
		byte[] requestMsg = new byte[16];
		//	FF FF
		requestMsg[0] = -1;
		requestMsg[1] = -1;
		//	长度
		requestMsg[2] = 0x00;
		requestMsg[3] = 0x10;
		//	序列号
		ByteUtil.intToByte(this.serialNumber.get(), requestMsg, 4, 2);
		//	报文体的长度
		requestMsg[6] = 0x00;
		requestMsg[7] = 0x0A;
		//	报文体的功能码
		ByteUtil.intToByte(211, requestMsg, 8, 2);
		//	设置线体号码
		ByteUtil.intToByte(Integer.parseInt(lineNo), requestMsg, 10, 2);
		//	设置要启用或禁用的功能选项
		ByteUtil.intToByte(Integer.parseInt(funtype), requestMsg, 12, 2);
		//	设置是启用还是禁用。
		ByteUtil.intToByte(Integer.parseInt(enable), requestMsg, 14, 2);
		//	发送消息
		this.scadaSocketContext.getConnector("WCS").send(requestMsg);
		LOGGER.info("[功能码:211]向WCS发送报文：[{}]", ByteUtil.bytesToString16(requestMsg));
	}
	
	private boolean checkConnectionStatus() {
		boolean isConnect = this.scadaSocketContext.getConnector("WCS").isConnected();
		if(!isConnect) {
			this.disConnectError("SCADA服务连接异常，请稍后刷新重试！");
		}
		return isConnect;
	}
	
	private void disConnectError(String msg) {
		this.webSocketPushService.pushMessage(Constants.TOPIC_URL_FOR_SCADA_ERROR,  msg);
	}

}
