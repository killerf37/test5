package com.ginfon.manage.Connect;

import com.ginfon.main.ScadaClientContext;
import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.scada.entity.ConveyorLine;
import com.ginfon.scada.gateway.websocket.service.WebsocketPushServiceImpl;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2022/01/11/14:31
 * @Description:
 */
@Component
public class ConnectIniCompent implements InitializingBean, ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private GfScadaContainer gfScadaContainer;

    @Autowired
    private WebsocketPushServiceImpl websocketPushService;

    @Autowired
    private ScadaClientContext scadaClientContext;

    /**
     * 各IP地址的连接状态
     */
    private Map<String,Integer> connectMap=new ConcurrentHashMap<>();

    /**
     * 线程池
     */
    private ExecutorService executor;

    /**
     * 定时执行线程
     */
    private ScheduledExecutorService fixRateTh;

    private ConnectStatusTh connectStatusTh;

    @PostConstruct
    public void iniFirstload()
    {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        fixRateTh= Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("ConnectTh"));
        List<ConveyorLine> listConvery=scadaClientContext.getBailunInfo();
        if (listConvery!=null)
        {
            for (ConveyorLine conveyorLine:listConvery)
            {
                connectMap.put(conveyorLine.getProudctId(),0);
            }
        }
        Map<String,String> ss=scadaClientContext.getDeviceNameIp();
        connectStatusTh=new ConnectStatusTh(gfScadaContainer,websocketPushService,connectMap);
        fixRateTh.scheduleAtFixedRate(connectStatusTh,5,1,TimeUnit.SECONDS);
    }
}
