package com.ginfon.scada.jikong;

import com.ginfon.main.ScadaClientContext;
import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.scada.gateway.websocket.service.ScadaMsgHandleServiceImpl;
import com.ginfon.scada.gateway.websocket.service.WebsocketPushServiceImpl;
import com.ginfon.scada.service.IFaultLogService;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/12/16/13:29
 * @Description:
 */
@Component
public class jikongServerCompent {

    /**
     * 用来执行定时任务
     */
    private ScheduledExecutorService threadPool;

    private jikongThread jikongThread;

    private Map<String, ConcurrentHashMap<String, Integer>> deviceStatusMap = new ConcurrentHashMap<>();

    private Map<String, ConcurrentHashMap<String, Integer>> deviceFaultMap = new ConcurrentHashMap<>();

    @Autowired
    private GfScadaContainer gfScadaContainer;

    @Autowired
    private IFaultLogService faultLogService;

    @Autowired
    private WebsocketPushServiceImpl websocketPushService;

    @Autowired
    private ScadaMsgHandleServiceImpl scadaMsgHandleService;

    public void initializationCompleted()
    {
        deviceStatusMap=new ConcurrentHashMap<>();
        deviceFaultMap = new ConcurrentHashMap<>();
        jikongThread=new jikongThread(gfScadaContainer,faultLogService,deviceStatusMap,deviceFaultMap,websocketPushService,scadaMsgHandleService);
        threadPool= Executors.newScheduledThreadPool(10, new DefaultThreadFactory("jiKongThread-"));
        threadPool.scheduleAtFixedRate(jikongThread,10,5, TimeUnit.SECONDS);
//        ScadaMsgHandleServiceImpl msgHandleService = scadaMsgHandleService;
//        threadPool.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                if (msgHandleService.Audioflag)
//                {
//                    //Audioplayer.textToSpeech("输送线发生了异常");
//
//                    msgHandleService.Audioflag=false;
//                }
//            }
//        },10,5,TimeUnit.SECONDS);
    }
}
