package com.ginfon.scada.gateway.plcserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/10/19/13:55
 * @Description:
 */
@Component
public class plcserver implements Runnable{
    @Value("${scada.socket.server.plcport1}")
    private int port1;

    @Value("${scada.socket.server.plcport2}")
    private int port2;

    @Value("${scada.socket.server.plcport3}")
    private int port3;

    @Autowired
    private SortingDeviceContainer sortingDeviceContainer;

    @Autowired
    private PlcCompent plcCompent;



//    public plcserver(SortingDeviceContainer sc)
//    {
//        sortingDeviceContainer=sc;
//    }

    @Override
    public void run(){
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception
                {
                    socketChannel.pipeline().addLast(new MyPLCDecoder());
                    socketChannel.pipeline().addLast(new plcserverAdapter(sortingDeviceContainer,plcCompent));
                }
            })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .option(ChannelOption.SO_SNDBUF,32*1024)
                    .option(ChannelOption.SO_RCVBUF,32*1024)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture future1=bootstrap.bind(port1).sync();
            ChannelFuture future2=bootstrap.bind(port2).sync();
            ChannelFuture future3=bootstrap.bind(port3).sync();
            //sortingDeviceContainer.addctx("2101",future1.channel());
            //sortingDeviceContainer.addctx("2102",future2.channel());
            //sortingDeviceContainer.addctx("2103",future3.channel());
            future1.channel().closeFuture().sync();
            future2.channel().closeFuture().sync();
            future3.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
