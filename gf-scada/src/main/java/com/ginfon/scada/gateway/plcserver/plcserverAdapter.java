package com.ginfon.scada.gateway.plcserver;

import com.ginfon.scada.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/10/19/13:53
 * @Description:plc连接的类
 */
public class plcserverAdapter extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(plcserverAdapter.class);

    private SortingDeviceContainer sortingDeviceContainer;

    private PlcCompent plcCompent;

    public plcserverAdapter(SortingDeviceContainer sc, PlcCompent compent)
    {
        sortingDeviceContainer=sc;
        plcCompent=compent;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //do something msg
        ByteBuf buf = (ByteBuf)msg;
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        if (data!=null&&data.length>0)
        {
            LOGGER.info("收到PLC报文:"+ ByteUtil.bytesToString16(data));
            int type=0;
            byte[] effmsg=plcCompent.operate(data,type);
            if (sortingDeviceContainer.getdevicemap().containsKey("2723"))
            {
                LOGGER.info("2723存在,转发给2723"+ ByteUtil.bytesToString16(effmsg));
                sortingDeviceContainer.getdevicemap().get("2723").writeAndFlush(Unpooled.copiedBuffer(effmsg));
                LOGGER.info("转发给2723完成");
            }
        }else
        {
            LOGGER.info("收到PLC报文为空");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (sortingDeviceContainer.checkctx(sortingDeviceContainer.portName(ctx.channel().localAddress())))
        {
            sortingDeviceContainer.removectx(sortingDeviceContainer.portName(ctx.channel().localAddress()));
        }
        ctx.close();
    }

    /**
     * 客户端连接会触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        String local=sortingDeviceContainer.portName(ctx.channel().localAddress());
        sortingDeviceContainer.addctx(local,ctx.channel());
        LOGGER.info("PLC{}连接上",local);
    }

    /**
     * 客户端断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        String local=sortingDeviceContainer.portName(ctx.channel().localAddress());
        if (sortingDeviceContainer.checkctx(local))
        {
            sortingDeviceContainer.removectx(local);
        }
        LOGGER.info("PLC{}断开",local);
    }
}
