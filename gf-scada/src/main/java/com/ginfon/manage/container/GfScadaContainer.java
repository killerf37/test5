package com.ginfon.manage.container;

import com.ginfon.main.ScadaClientContext;
import com.ginfon.sfclient.channel.SFScadaMessageFrame;
import com.sun.jna.platform.win32.Sspi;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @description: 金峰总控scada容器类
 * @author: curtain
 * @create: 2021-07-12 11:06
 **/
@Component
public class GfScadaContainer {

	public GfScadaContainer() {

	}

	private static final Map<String, NioSocketChannel> gfWheelClientMap = new <String, NioSocketChannel>ConcurrentHashMapV8(8);

	private Map<String, ConcurrentHashMap<String, Integer>> deviceStatusMap = new ConcurrentHashMap<>();

	private Map<String, ConcurrentHashMap<String, Integer>> deviceFaultMap = new ConcurrentHashMap<>();

	private Map<String, ConcurrentHashMap<String, Integer>> sfDeviceFaultMap = new ConcurrentHashMap<>();

	private Map<String, ConcurrentHashMap<String, Integer>> lineStatusMap = new ConcurrentHashMap<>();

	private Map<String, ConcurrentHashMap<String, Integer>> runtimeInfoMap = new ConcurrentHashMap<>();

	private Map<String, ConcurrentHashMap<Integer, Integer>> deviceSettingMap = new ConcurrentHashMap<>();

	private Map<String,ConcurrentHashMap<String,String>> deviceErrInfoMap=new ConcurrentHashMap<>();
	//设备在线情况，key为ip，值为上次的时间
	private Map<String,Long> online=new ConcurrentHashMap<>();
	//所有分线设备的通道，key为ip，值为通道
	private Map<String, Channel> deviceBLmap=new ConcurrentHashMap<>();

	public void addDeviceBLmap(String ip, Channel channel){
		deviceBLmap.put(ip,channel);
	}

	public void removeDeviceBLmap(String ip)
	{
		if (deviceBLmap.containsKey(ip))
		{
			deviceBLmap.remove(ip);
		}
	}

	public Map<String, Channel> getDeviceBLmap() {
		return deviceBLmap;
	}

	public Map<String, Long> getOnline() {
		return online;
	}

	public void setOnline(Map<String, Long> online) {
		this.online = online;
	}

	/**
	 * 刷新连接
	 * @param ip 地址
	 * @param timec 时间戳
	 */
	public void refreshOnline(String ip,long timec)
	{
		this.online.put(ip,timec);
	}

	// 顺丰400消息，用以区分上传。aliasName-(启动类型：1整线，2单机)。
	private Map<String, Integer> startTypeMap = new ConcurrentHashMap<>();

	// 缓存顺丰发送的400，421，434消息，用以响应。aliasName-(未处理的消息队列)。
	private Map<String, ConcurrentLinkedQueue<SFScadaMessageFrame>> sfFrameMap = new ConcurrentHashMap<>();

	// 顺丰421消息，用以区分上传。aliasName-(操作对象类型：1整线，2单机)。
	private Map<String, Integer> operateTypeMap = new ConcurrentHashMap<>();

	public static void put(String id, NioSocketChannel socketChannel) {
		gfWheelClientMap.put(id, socketChannel);
	}

	public static NioSocketChannel get(String id) {
		return gfWheelClientMap.get(id);
	}

	public static Map<String, NioSocketChannel> getChannelMap() {
		return gfWheelClientMap;
	}

	public Map<String, ConcurrentHashMap<String, Integer>> getDeviceStatusMap() {
		return deviceStatusMap;
	}

	public void setDeviceStatusMap(String aliasName,ConcurrentHashMap<String, Integer> deviceStatusMap) {
		this.deviceStatusMap.put(aliasName,deviceStatusMap);
	}

	public void setDeviceErrInfoMap(String aliasName,ConcurrentHashMap<String,String> deviceErrInfo){
		deviceErrInfoMap.put(aliasName,deviceErrInfo);
	}

	public Map<String,ConcurrentHashMap<String,String>> getDeviceErrInfoMap(){return deviceErrInfoMap;}

	public Map<String, ConcurrentHashMap<String, Integer>> getsfDeviceFaultMap() {
		return sfDeviceFaultMap;
	}

	public void setsfDeviceFaultMap(String aliasName,ConcurrentHashMap<String, Integer> sfDeviceFaultMap) {
		this.sfDeviceFaultMap.put(aliasName,sfDeviceFaultMap);
	}

	public Map<String, ConcurrentHashMap<String, Integer>> getDeviceFaultMap() {
		return deviceFaultMap;
	}

	public void setDeviceFaultMap(String aliasName,ConcurrentHashMap<String, Integer> deviceFaultMap) {
		this.deviceFaultMap.put(aliasName,deviceFaultMap);
	}

	public Map<String, ConcurrentHashMap<String, Integer>> getLineStatusMap() {
		return lineStatusMap;
	}

	public void setLineStatusMap(String aliasName,ConcurrentHashMap<String, Integer> lineStatusMap) {
		this.lineStatusMap.put(aliasName,lineStatusMap);
	}

	public Map<String, ConcurrentHashMap<String, Integer>> getRuntimeInfoMap() {
		return runtimeInfoMap;
	}

	public void setRuntimeInfoMap(String aliasName,ConcurrentHashMap<String, Integer> runtimeInfoMap) {
		this.runtimeInfoMap.put(aliasName,runtimeInfoMap);
	}

	public Map<String, ConcurrentHashMap<Integer, Integer>> getDeviceSettingMap() {
		return deviceSettingMap;
	}

	public void setDeviceSettingMap(String aliasName,ConcurrentHashMap<Integer, Integer> deviceSettingMap) {
		this.deviceSettingMap.put(aliasName,deviceSettingMap);
	}

	public Map<String, Integer> getStartTypeMap() {
		return startTypeMap;
	}

	public void setStartTypeMap(String aliasName,Integer startType) {
		this.startTypeMap.put(aliasName,startType);
	}

	public Map<String, Integer> getOperateTypeMap() {
		return operateTypeMap;
	}

	public void setOperateTypeMap(String aliasName,Integer operateType) {
		this.operateTypeMap.put(aliasName,operateType);
	}

	public Map<String, ConcurrentLinkedQueue<SFScadaMessageFrame>> getSfFrameMap() {
		return sfFrameMap;
	}

	public ConcurrentLinkedQueue<SFScadaMessageFrame> getSfFrameMapByKey(String aliasName) {
		return sfFrameMap.get(aliasName);
	}

	public void setSfFrameMap(String aliasName, ConcurrentLinkedQueue<SFScadaMessageFrame> list) {
		this.sfFrameMap.put(aliasName,list);
	}
}
