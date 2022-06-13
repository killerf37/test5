package com.ginfon.main;

import com.ginfon.manage.server.GfScadaServer;

/**
 * @description: 通过线程启动socket server，否则会因为阻塞，无法正常启动web服务
 * @author: curtain
 * @create: 2021-07-06 17:38
 **/
public class GfScadaServerThread implements Runnable{

	private GfScadaServer gfScadaServer;

	public GfScadaServerThread(GfScadaServer gfScadaServer) {
		this.gfScadaServer = gfScadaServer;
	}

	@Override
	public void run() {
		gfScadaServer.launch();
	}
}
