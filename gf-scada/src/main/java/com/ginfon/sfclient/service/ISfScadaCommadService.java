package com.ginfon.sfclient.service;

import com.ginfon.sfclient.channel.SFScadaMessageFrame;

public interface ISfScadaCommadService {

	/**
	 * 心跳消息 1（SCADA <-> client）
	 * @param frame
	 */
	byte[] scadaHeartBeat(SFScadaMessageFrame frame);

	/**
	 * 初始化消息 2（SCADA -> client）
	 * @param frame
	 */
	byte[] scadaInit(SFScadaMessageFrame frame);

	/**
	 * 启动/停止 400（SCADA ->client）
	 * @param frame
	 */
	byte[] scadaStartAndStop(SFScadaMessageFrame frame);

	/**
	 * 功能操作消息 421（SCADA->client）
	 * @param frame
	 */
	byte[] scadaOperate(SFScadaMessageFrame frame);

	/**
	 * 设备参数设置 434（SCADA->client）
	 * @param frame
	 */
	byte[] scadaSetting(SFScadaMessageFrame frame);
}
