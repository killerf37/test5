package com.ginfon.sfclient.thread;

import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.sfclient.channel.SFScadaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
public class SfScadaDeviceFaultUploadThread implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(SfScadaDeviceFaultUploadThread.class);

	private SFScadaClient sfScadaClient;
	private static final int INTV = 2000;
	private static final int INTV_NORMAL = 10000;

	private GfScadaContainer gfScadaContainer;

	private boolean isStop = false;


	public SfScadaDeviceFaultUploadThread(SFScadaClient sfScadaClient, GfScadaContainer gfScadaContainer) {
		this.sfScadaClient = sfScadaClient;
		this.gfScadaContainer = gfScadaContainer;
	}

	@Override
	public void run() {
		while (!isStop) {
			long s = System.currentTimeMillis();
			try {
				//获取故障map
				Map<String, ConcurrentHashMap<String, Integer>> maps = gfScadaContainer.getsfDeviceFaultMap();
				boolean noFault = true;
                Iterator<Map.Entry<String,ConcurrentHashMap<String, Integer>>> iterator = maps.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry<String,ConcurrentHashMap<String, Integer>> entry = iterator.next();
                    if(noFault && entry.getValue().size() > 0){//只要有故障，置为false
                        noFault = false;
                    }
                    sfScadaClient.submitDeviceFault(entry.getKey(),entry.getValue());
                }
				if (noFault) {
					long l = INTV_NORMAL - (System.currentTimeMillis() - s);
					TimeUnit.MILLISECONDS.sleep(l <= 0 ? INTV_NORMAL : l);
				} else {
					long l = INTV - (System.currentTimeMillis() - s);
					TimeUnit.MILLISECONDS.sleep(l <= 0 ? INTV : l);
				}
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (Exception ex) {
				LOGGER.error("顺丰定时上传故障信息线程异常", ex);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isStop() {
		return isStop;
	}

	public void setStop(boolean stop) {
		isStop = stop;
	}

}
