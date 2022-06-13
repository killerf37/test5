package com.ginfon.scada.gateway.wcservice;

import com.ginfon.scada.gateway.plcserver.SortingDeviceContainer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/10/12/16:08
 * @Description:
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private SortingDeviceContainer sortingDeviceContainer;

    private SCADACompent scadaCompent;

    public ServerHandler(SortingDeviceContainer sc, SCADACompent scadaCompent1)
    {
        sortingDeviceContainer=sc;
        scadaCompent=scadaCompent1;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //do something msg
        ByteBuf buf = (ByteBuf)msg;
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        if (data!=null&&data.length>0)
        {
            scadaCompent.Proxymsg(data);
        }else
        {

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
    }

    /**
     * 客户端断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (sortingDeviceContainer.checkctx(sortingDeviceContainer.portName(ctx.channel().localAddress())))
        {
            sortingDeviceContainer.removectx(sortingDeviceContainer.portName(ctx.channel().localAddress()));
        }
    }
}
