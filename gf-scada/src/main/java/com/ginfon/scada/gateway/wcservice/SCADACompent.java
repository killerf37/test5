package com.ginfon.scada.gateway.wcservice;

import com.ginfon.scada.gateway.plcserver.SortingDeviceContainer;
import com.ginfon.scada.util.ByteUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/10/12/16:28
 * @Description:
 */
@Component
public class SCADACompent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SCADACompent.class);

    @Autowired
    private com.ginfon.scada.gateway.wcservice.SCADAService SCADAService;

    @Autowired
    private SortingDeviceContainer sortingDeviceContainer;

    @PostConstruct
    private void launch()
    {
        try{
            Thread clientThread = new Thread(SCADAService);
            clientThread.setName("SCADA listen thread.");
            clientThread.start();
        }catch (Exception e){

        }
    }

    /**
     * 转发报文
     * @param data
     */
    public void Proxymsg(byte[] data)
    {
        if (data.length>10)
        {
            if (data[0]==-1&&data[1]==-1)
            {
                int length= ByteUtil.byteToInt(data[2],data[3]);
                if (length==data.length)
                {
                    int msgtype= ByteUtil.byteToInt(data[8],data[9]);
                    switch (msgtype)
                    {
                        case 1://心跳消息
                        case 103://设备系统控制

                        case 203://设置堵包功能时间

                        case 205://设置线体休眠时间

                        case 206://设置线体依次启动时间

                        case 211://启动/禁用功能

                            if (sortingDeviceContainer.checkctx("2101")){
                                sortingDeviceContainer.getdevicemap().get("2101").writeAndFlush(Unpooled.copiedBuffer(data));
                            }else
                            {

                            }

                            break;

                        case 201://设备系统状态请求

                        case 202://堵包功能状态请求

                        case 207://查询线体休眠时间

                        case 208://查询线体启动时间
                            if (sortingDeviceContainer.checkctx("2102")){
                                sortingDeviceContainer.getdevicemap().get("2102").writeAndFlush(Unpooled.copiedBuffer(data));
                            }else
                            {

                            }
                            break;

                        default :

                            break;
                    }
                }
            }
        }else
        {
            if (data.length>=6)
            {

            }else
            {

            }
        }
    }
}

class sendResult implements ChannelFutureListener {
    private boolean issuc;
    public sendResult(boolean isc)
    {
        isc=issuc;
    }
    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (channelFuture!=null)
        {
            if (channelFuture.isSuccess())
            {
                issuc=true;
            }else
            {
                issuc=false;
            }
        }
    }
}
