package com.ginfon.manage.server;

import com.ginfon.manage.GfScadaChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description: 总控SCADA服务端
 * @author: curtain
 * @create: 2021-06-23 15:40
 **/
@Component
public class GfScadaServer {

	@Value("${server.socket.port}")
	private int port ;
	@Autowired
	private GfScadaChannelInitializer gfScadaChannelInitializer;

	public void launch() {
		/**
		 * Netty 负责装领导的事件处理线程池
		 */
		EventLoopGroup leader = new NioEventLoopGroup();
		/**
		 * Netty 负责装码农的事件处理线程池
		 */
		EventLoopGroup coder = new NioEventLoopGroup();
		try {
			/**
			 * 服务端启动引导器
			 */
			ServerBootstrap server = new ServerBootstrap();
			server.group(leader, coder)//把事件处理线程池添加进启动引导器
					.channel(NioServerSocketChannel.class)//设置通道的建立方式,这里采用Nio的通道方式来建立请求连接
					.childHandler(gfScadaChannelInitializer)
					/**
					 * 用来配置一些channel的参数，配置的参数会被ChannelConfig使用
					 * BACKLOG用于构造服务端套接字ServerSocket对象，
					 * 标识当服务器请求处理线程全满时，
					 * 用于临时存放已完成三次握手的请求的队列的最大长度。
					 * 如果未设置或所设置的值小于1，Java将使用默认值50
					 */
					.option(ChannelOption.SO_BACKLOG, 128)
					/**
					 * 是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）
					 * 并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。
					 */
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			/**
			 * 服务端绑定端口并且开始接收进来的连接请求
			 */
			ChannelFuture channelFuture = server.bind(port).sync();
			/**
			 * 查看一下操作是不是成功结束了
			 */
			if (channelFuture.isSuccess()){
				//如果没有成功结束就处理一些事情,结束了就执行关闭服务端等操作
				System.out.println("服务端启动成功!");
				System.out.println(channelFuture.channel().isActive());
			}
			/**
			 * 关闭服务端
			 */
			channelFuture.channel().closeFuture().sync();
			System.out.println("服务端即将关闭!");
		} catch (Exception e){
			System.out.println(e.getCause().getMessage());
		}finally {
			/**
			 * 关闭事件处理组
			 */
			leader.shutdownGracefully();
			coder.shutdownGracefully();
			System.out.println("服务端已关闭!");
		}

	}
}
