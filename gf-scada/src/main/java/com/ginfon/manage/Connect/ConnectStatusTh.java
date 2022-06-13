package com.ginfon.manage.Connect;

import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.scada.gateway.websocket.service.WebsocketPushServiceImpl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2022/01/11/13:59
 * @Description:
 */
public class ConnectStatusTh implements Runnable {

    private GfScadaContainer gfScadaContainer;

    private WebsocketPushServiceImpl websocketPushService;

    /**
     * 各IP地址的连接状态
     */
    private Map<String, Integer> connectStatusMap;

    Byte[] aa=new Byte[5];


    public ConnectStatusTh(GfScadaContainer gfScadaContainer1, WebsocketPushServiceImpl websocketPushService1, Map<String, Integer> connectMap1) {
        gfScadaContainer = gfScadaContainer1;
        websocketPushService = websocketPushService1;
        connectStatusMap = connectMap1;
    }


    @Override
    public void run() {
        Map<String, Long> connectMap = gfScadaContainer.getOnline();
        if (connectMap != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long nowTimeSpan = System.currentTimeMillis();
            String dateStr = dateFormat.format(nowTimeSpan);
            Set<String> keys = connectMap.keySet();
            for (String key : keys) {
                Long lastime = connectMap.get(key);
                Long deltime = nowTimeSpan - lastime;
                if (deltime > 12000) {
                    if (connectStatusMap.containsKey(key)) {
                        connectStatusMap.put(key, 0);
                    }
                } else {
                    if (connectStatusMap.containsKey(key)) {
                        connectStatusMap.put(key, 1);
                    } else {
                        connectStatusMap.put(key, 1);
                    }
                }
            }
            //装载结束状态连接map
        } else {
            //没有获取到连接状态map，默认全失联

        }
        websocketPushService.pushMessage("/connect/status", connectStatusMap);
    }
}
