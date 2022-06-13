package com.ginfon.scada.gateway.plcserver;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/10/20/16:35
 * @Description:
 */
@Component
public class SortingDeviceContainer {

    private HashMap<String, Channel> devicemap;

    public SortingDeviceContainer()
    {
        devicemap=new HashMap<>();
    }

//    @PostConstruct
//    private void containerLoad()
//    {
//        devicemap=new HashMap<>();
//    }

    /**
     * 添加连接对象
     * @param ctxname
     * @param ctx
     */
    public void addctx(String ctxname,Channel ctx)
    {
        if (devicemap!=null)
        {
            devicemap.put(ctxname,ctx);
        }
    }

    /**
     * 移除连接对象
     * @param ctxname
     */
    public void removectx(String ctxname)
    {
        if (devicemap!=null&&devicemap.containsKey(ctxname))
        {
            devicemap.remove(ctxname);
        }
    }

    /**
     * 获取设备连接对象map
     * @return
     */
    public HashMap<String, Channel> getdevicemap()
    {
        return this.devicemap;
    }

    /**
     * 检查
     * @param ctxname
     * @return
     */
    public boolean checkctx(String ctxname)
    {
        if (devicemap!=null&&ctxname!=null)
        {
            if (devicemap.containsKey(ctxname))
            {
                return true;
            }else
            {
                return false;
            }
        }else
        {
            return false;
        }
    }


    /**
     * 获取端口名称
     * @param socketAddress
     * @return
     */
    public String portName(SocketAddress socketAddress)
    {
        String adress=socketAddress.toString();
        if (!"".equalsIgnoreCase(adress))
        {
            int index=adress.lastIndexOf(":");
            if (index>0)
            {
                String port=adress.substring(index+1);
                return port;
            }else
            {
                return null;
            }
        }else
        {
            return null;
        }
    }

}
