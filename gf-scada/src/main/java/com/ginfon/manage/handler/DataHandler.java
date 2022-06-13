package com.ginfon.manage.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.scada.gateway.common.SpringContextUtils;
import com.ginfon.scada.gateway.websocket.service.WebsocketPushServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 分控数据处理类
 * @author: curtain
 * @create: 2021-07-12 11:37
 **/
public class DataHandler implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(GfScadaServerHandler.class);

	public DataHandler(ChannelHandlerContext ctx, JSONObject object,GfScadaContainer gfScadaContainer,WebsocketPushServiceImpl websocketPushService1) {
		this.ctx = ctx;
		this.object = object;
		this.gfScadaContainer = gfScadaContainer;
		this.websocketPushService=websocketPushService1;
	}

	private ChannelHandlerContext ctx;

	private JSONObject object;

	private GfScadaContainer gfScadaContainer;

	private WebsocketPushServiceImpl websocketPushService;
	@Override
	public void run() {
		if (object != null) {
			try {
				if (object.getInteger("type") != null) {
					if (object.getInteger("type") == -2) {
						LOGGER.info("【分控客户端-{}连接成功，{}】",object.getString("aliasName") , "IP地址：" + object.getString("ip"));
						// 注册服务，通道缓存
						String aliasName = object.getString("aliasName");
						if (StringUtils.isNotEmpty(aliasName)) {
							if (GfScadaContainer.get(aliasName) == null) {
								GfScadaContainer.put(aliasName, (NioSocketChannel) ctx.channel());
							} else {//重新注册
								if (!GfScadaContainer.get(aliasName).isActive()) {
									GfScadaContainer.put(aliasName, (NioSocketChannel) ctx.channel());
								}
							}
						}
					} else {
						this.work();
					}
				} else {
					LOGGER.info("【收到分控客户端发送的数据：{}】",object.toJSONString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void work() {
		int type= object.getInteger("type");//消息类型
		String aliasName= object.getString("aliasName");//客户端别名
        String ip=object.getString("ip");
		LOGGER.info("【收到分控客户端发送的数据：type为{},别名为{},ip为{}】",type,aliasName,ip);
		//缓存分控发送的数据，待顺丰客户端线程发送
		ConcurrentHashMap<String,Integer> deviceStatusMap = JSONObject.parseObject(object.getString("deviceStatusMap"), ConcurrentHashMap.class);
		ConcurrentHashMap<String,Integer> deviceFaultMap = JSONObject.parseObject(object.getString("deviceFaultMap"), ConcurrentHashMap.class);
		ConcurrentHashMap<String,Integer> lineStatusMap = JSONObject.parseObject(object.getString("lineStatusMap"), ConcurrentHashMap.class);
		ConcurrentHashMap<String,Integer> runtimeInfoMap = JSONObject.parseObject(object.getString("runtimeInfoMap"), ConcurrentHashMap.class);
		ConcurrentHashMap<Integer,Integer> deviceSettingMap = JSONObject.parseObject(object.getString("deviceSettingMap"), ConcurrentHashMap.class);
		ConcurrentHashMap<String,String> errinfo=JSONObject.parseObject(object.getString("errInfo"), ConcurrentHashMap.class);

		LOGGER.info("【ip为{},设备状态组数{}】",ip,deviceStatusMap.size());

		gfScadaContainer.setDeviceStatusMap(ip,deviceStatusMap);
		gfScadaContainer.setDeviceFaultMap(ip,deviceFaultMap);
		gfScadaContainer.setLineStatusMap(ip,lineStatusMap);
		gfScadaContainer.setRuntimeInfoMap(ip,runtimeInfoMap);
		gfScadaContainer.setDeviceSettingMap(ip,deviceSettingMap);
		gfScadaContainer.setDeviceErrInfoMap(ip,errinfo);
		gfScadaContainer.setsfDeviceFaultMap(aliasName,deviceFaultMap);
	}
}
