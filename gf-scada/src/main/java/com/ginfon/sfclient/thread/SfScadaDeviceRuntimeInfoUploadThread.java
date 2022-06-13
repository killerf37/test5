package com.ginfon.sfclient.thread;

import com.ginfon.main.ScadaLauncher;
import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.scada.util.SerialNumber;
import com.ginfon.sfclient.channel.SFScadaClient;
import com.ginfon.sfclient.util.ByteUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 设备运行时数据上报 335（client->SCADA）
 * @auther: Curtain
 * @date: 2021/7/14 13:55
 */
public class SfScadaDeviceRuntimeInfoUploadThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SfScadaDeviceFaultUploadThread.class);

    private SFScadaClient sfScadaClient;
    private ScadaLauncher scadaLauncher;
    private GfScadaContainer gfScadaContainer;
    /**
     * 序列号生成器。
     */
    private SerialNumber serialNumber;
    public SfScadaDeviceRuntimeInfoUploadThread(SFScadaClient sfScadaClient, ScadaLauncher scadaLauncher, GfScadaContainer gfScadaContainer) {
        this.sfScadaClient = sfScadaClient;
        this.gfScadaContainer = gfScadaContainer;
        this.serialNumber = new SerialNumber();
    }

    @Override
    public void run() {
        //目前只有主控可以上传
        Map<String, ConcurrentHashMap<String, Integer>> map = gfScadaContainer.getRuntimeInfoMap();
        for(String aliasName : map.keySet()){
            ConcurrentHashMap<String, Integer> runtimeInfoMap = map.get(aliasName);
            int cpuRate = runtimeInfoMap.get("cpuRate");
            int diskRate = runtimeInfoMap.get("diskRate");
            int memRate = runtimeInfoMap.get("memRate");
            int diskLeft = runtimeInfoMap.get("diskLeft");
            int memLeft = runtimeInfoMap.get("memLeft");
            //组装响应报文
            int msgLength = 31 + 6 * 5 + 1 * 3 + 4;
            byte[] message = new byte[msgLength];
            message[0] = -1;
            message[1] = -1;
            ByteUtil.intToByte(msgLength, message, 2, 2);
            ByteUtil.intToByte(serialNumber.get(), message, 4, 4);
            ByteUtil.longToByte(System.currentTimeMillis(), message, 9, 8);
            System.arraycopy(aliasName.getBytes(), 0, message, 17, 10);
            ByteUtil.intToByte(msgLength, message, 27, 2);
            ByteUtil.intToByte(335, message, 29, 2);

            ByteUtil.intToByte(1, message, 31, 2);//设备类型
            ByteUtil.intToByte(1, message, 33, 2);//设备编号
            ByteUtil.intToByte(10, message, 35, 2);//上报数据类型
            ByteUtil.intToByte(cpuRate, message, 37, 1);//上报数据类型

            ByteUtil.intToByte(1, message, 38, 2);//设备类型
            ByteUtil.intToByte(1, message, 40, 2);//设备编号
            ByteUtil.intToByte(13, message, 42, 2);//上报数据类型
            ByteUtil.intToByte(diskRate, message, 44, 1);//上报数据类型

            ByteUtil.intToByte(1, message, 45, 2);//设备类型
            ByteUtil.intToByte(1, message, 47, 2);//设备编号
            ByteUtil.intToByte(11, message, 49, 2);//上报数据类型
            ByteUtil.intToByte(memRate, message, 51, 1);//上报数据类型

            ByteUtil.intToByte(1, message, 52, 2);//设备类型
            ByteUtil.intToByte(1, message, 54, 2);//设备编号
            ByteUtil.intToByte(14, message, 56, 2);//上报数据类型
            ByteUtil.intToByte(diskLeft, message, 58, 2);//上报数据类型

            ByteUtil.intToByte(1, message, 60, 2);//设备类型
            ByteUtil.intToByte(1, message, 62, 2);//设备编号
            ByteUtil.intToByte(12, message, 64, 2);//上报数据类型
            ByteUtil.intToByte(memLeft, message, 66, 2);//上报数据类型

            this.sfScadaClient.connector().channel().writeAndFlush(Unpooled.wrappedBuffer(message));
        }
    }
}
