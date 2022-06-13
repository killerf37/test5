package com.ginfon.sfclient.channel;

import com.ginfon.sfclient.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SFScadaClientChannelHandler extends ChannelInboundHandlerAdapter {

    private SFScadaClient sfScadaClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(SFScadaClientChannelHandler.class);

    public static final String HEART_BEAT = "heart beat!";

    public SFScadaClientChannelHandler(SFScadaClient sfScadaClient) {
        this.sfScadaClient = sfScadaClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
//        ping(ctx.channel());
    }

    private void ping(Channel channel) {
        ScheduledFuture<?> future = channel.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                if (channel.isActive()) {
                    System.out.println("sending heart beat to the server...");
                    try {
                        channel.writeAndFlush(HEART_BEAT);
                    } catch (Exception e) {
                        System.out.println(e.getCause().getMessage());
                    }
                } else {
                    System.err.println("The connection had broken, cancel the task that will send a heart beat.");
                    channel.closeFuture();
                    throw new RuntimeException();
                }
            }
        }, 4, TimeUnit.SECONDS);

        future.addListener(new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()) {
                    ping(channel);
                }else{
                    channel.pipeline().fireChannelInactive();
                }
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        byteBuf.release();
        LOGGER.debug("【【【SF SCADA发送的报文】】】"+ ByteUtil.bytesToString16(data));
//        ctx.writeAndFlush(Unpooled.wrappedBuffer("Heartbeat2".getBytes()));
        this.sfScadaClient.execute(() -> {
            SFScadaClient client = this.sfScadaClient;
            //	创建报文体
            SFScadaMessageFrame frame = SFScadaMessageFrame.create(data);
            client.sfScadaChannelRead(frame);
        });


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case WRITER_IDLE:
                    ctx.writeAndFlush(Unpooled.wrappedBuffer("Heartbeat".getBytes()));
                    break;
                case ALL_IDLE:
                    break;
                default:
                    break;
            }
        }
    }

}
