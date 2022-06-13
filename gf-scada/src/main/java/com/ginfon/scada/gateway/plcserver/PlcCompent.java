package com.ginfon.scada.gateway.plcserver;

import com.ginfon.scada.util.ByteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/10/19/14:08
 * @Description:
 */
@Component
public class PlcCompent {
    @Autowired
    private plcserver plcService;

    @PostConstruct
    private void launch()
    {
        try{
            Thread.sleep(1000);
            Thread clientThread = new Thread(plcService);
            clientThread.setName("PLC listen thread.");
            clientThread.start();
        }catch (Exception e){

        }
    }


    /**
     * PLC发送过来的数据处理
     * @param msg
     */
    public byte[] operate(byte[] msg,Integer type)
    {
        if(msg!=null&&msg.length>0)
        {
            if (msg.length>=10)
            {
                if(msg[0]==-1&&msg[1]==-1)
                {
                    int typeint= ByteUtil.byteToInt(msg[8],msg[9]);
                    int msgLength= ByteUtil.byteToInt(msg[2],msg[3]);
                    type=typeint;
                    switch (typeint)
                    {
                        case 1://心跳消息
                            if (msgLength==12)
                            {
                                byte[] hearts=new byte[msgLength];
                                System.arraycopy(msg,0,hearts,0,hearts.length);
                                return hearts;
                            }else
                            {
                                return null;
                            }
                        case 102://格口推送
                            return effectiveMsg(msgLength,msg);
                        case 301://设备状态
                            return effectiveMsg(msgLength,msg);
                        case 302://按键触发
                            return effectiveMsg(msgLength,msg);
                        case 303://摆臂/摆轮动作触发
                            return effectiveMsg(msgLength,msg);
                        case 204://堵包功能状态答复
                            return effectiveMsg(msgLength,msg);
                        case 209://线体休眠时间答复
                            return effectiveMsg(msgLength,msg);
                        case 210://线体启动时间答复
                            return effectiveMsg(msgLength,msg);
                        default:
                            return effectiveMsg(msgLength,msg);
                    }
                }else
                {
                    return null;
                }
            }else
            {
                return null;
            }
        }else
        {
            return null;
        }
    }


    /**
     * 截取有效消息长度
     * @param msgLength
     * @param msg
     * @return
     */
    public byte[] effectiveMsg(int msgLength,byte[] msg)
    {
        if (msgLength<=msg.length)
        {
            int effectiveMsgLength=msgLength;
            for (int i=msgLength;i>10;i-=6)
            {
                if(msg[i-1]==0&&msg[i-2]==0&&msg[i-3]==0&&msg[i-4]==0&&msg[i-5]==0&&msg[i-6]==0)
                {
                    effectiveMsgLength=i-6;
                }else
                {
                    break;
                }
            }
            if (msgLength>effectiveMsgLength)
            {
                ByteUtil.intToByte(effectiveMsgLength,msg,2,2);
                ByteUtil.intToByte(effectiveMsgLength-6,msg,6,2);
                byte[] statusbytes=new byte[effectiveMsgLength];
                System.arraycopy(msg,0,statusbytes,0,effectiveMsgLength);
                return statusbytes;
            }else
            {
                return msg;
            }
        }else
        {
            return null;
        }
    }
}
