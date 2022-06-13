package com.ginfon.sfclient.thread;

import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.sfclient.channel.SFScadaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SfScadaDeviceStatusUploadThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SfScadaDeviceStatusUploadThread.class);

    private SFScadaClient sfScadaClient;

    private GfScadaContainer gfScadaContainer;

    public SfScadaDeviceStatusUploadThread(SFScadaClient sfScadaClient, GfScadaContainer gfScadaContainer) {
        this.sfScadaClient = sfScadaClient;
        this.gfScadaContainer = gfScadaContainer;
    }

    @Override
    public void run() {
        try {
            //获取设备状态Map
            Map<String, ConcurrentHashMap<String, Integer>> maps = gfScadaContainer.getDeviceStatusMap();
            Iterator<Map.Entry<String,ConcurrentHashMap<String, Integer>>> iterator = maps.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,ConcurrentHashMap<String, Integer>> entry = iterator.next();
                sfScadaClient.submitDeviceStateInfo(entry.getKey(),entry.getValue());
            }
        } catch (Exception ex) {
            LOGGER.error("顺丰定时上传状态信息线程异常", ex);
        }
    }
}
