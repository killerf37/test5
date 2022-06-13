package com.ginfon.scada.gateway.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 	用于建立Socket连接的线程对象。
 * @author Mark
 *
 */
public final class SocketConnector implements Runnable {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SocketConnector.class);
	
	private String name;
	
	private String ip;
	
	private int port;
	
	private EventLoopGroup workGroup;
	
	private ScadaChannelInitializer initializer;
	
	private ScadaSocketContext socketContext;
	
	
	private volatile Channel channel;
	
	private volatile boolean running = false;
	
	private volatile boolean isConnected = false;
	
	
	public SocketConnector(ScadaSocketContext socketContext, String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.socketContext = socketContext;
		//	实例化连接构造器。
		this.initializer = new ScadaChannelInitializer(this);
	}
	
	@Override
	public void run() {
        if(this.running)
            return;
        
        this.running = true;
        
        if(this.isConnected)
        	return;
        
        if(this.workGroup != null)
        	this.workGroup.shutdownGracefully();
        this.workGroup = new NioEventLoopGroup(4);
        
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(this.workGroup).channel(NioSocketChannel.class).
        	option(ChannelOption.SO_KEEPALIVE, true).
        	handler(this.initializer);
        while(this.running)
        	this.connect(bootstrap);
	}
	
    private void connect(Bootstrap bootstrap) {
        try{
//        	LOGGER.info("正在连接至服务器：[{}:{}]……", this.getIp(), this.getPort());
            ChannelFuture f = bootstrap.connect(this.ip, this.port).sync();
            this.isConnected = true;
            this.channel = f.channel();
            Thread.sleep(2000);
            f.channel().closeFuture().sync();

            this.isConnected = false;
            this.channel = null;
        }catch (Exception e){
            this.isConnected = false;
            this.channel = null;
            //	连接失败的异常就不要输出了
            if(e instanceof java.net.ConnectException) {
//            	LOGGER.info("连接至服务器：[{}:{}]失败！", this.getIp(), this.getPort());
            }else {
            	e.printStackTrace();	
            }
        }
    }
    
    void shutdown() {
    	try {
    		this.running = false;
        	this.isConnected = false;
        	if(this.channel != null)
        		this.channel.disconnect();
        	if(this.workGroup != null)
        		this.workGroup.shutdownGracefully();	
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    }
    
    /**
     * 	是否连接上。
     * @return
     */
    public boolean isConnected() {
    	return this.isConnected;
    }
    
    /**
     * 	获取该连接的名字。
     * @return
     */
    public String getChannelName() {
    	return this.name;
    }
    
    /**
     * 	获取目标IP地址。
     * @return
     */
    public String getIp() {
    	return this.ip;
    }
    
    /**
     * 	获取目标端口。
     * @return
     */
    public int getPort() {
    	return this.port;
    }
    
    public ScadaSocketContext getSocketContext() {
    	return this.socketContext;
    }
    
    /**
     * 	获取对应的处理器。
     * @return
     */
    public ChannelHandler getChannelHandler() {
    	return this.socketContext.getChannelHandler(this.name);
    }
    
    /**
     * 	发送消息。
     * @param msg	消息对象
     * @return
     */
    public ChannelFuture send(byte[] msg) {
    	if(this.channel != null)
    		return this.channel.writeAndFlush(Unpooled.copiedBuffer(msg));
    	return null;
    }
}
