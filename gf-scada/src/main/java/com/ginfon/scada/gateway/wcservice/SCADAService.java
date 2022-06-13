package com.ginfon.scada.gateway.wcservice;

import com.ginfon.scada.gateway.plcserver.SortingDeviceContainer;
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
 * @Date: 2021/10/09/15:38
 * @Description:
 */
@Component
public class SCADAService implements Runnable{

    @Value("${scada.socket.server.port}")
    private int port;

    @Autowired
    private SortingDeviceContainer sortingDeviceContainer;

    @Autowired
    private SCADACompent scadaCompent;

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
                    socketChannel.pipeline().addLast(new ServerHandler(sortingDeviceContainer,scadaCompent));
                }
            })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .option(ChannelOption.SO_SNDBUF,32*1024)
                    .option(ChannelOption.SO_RCVBUF,32*1024)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture future=bootstrap.bind(port).sync();
            sortingDeviceContainer.getdevicemap().put("2723",future.channel());
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
