package com.ginfon.sfclient.channel;

import com.ginfon.main.ScadaLauncher;
import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.scada.util.SerialNumber;
import com.ginfon.sfclient.service.impl.SFScadaCommandServiceImpl;
import com.ginfon.sfclient.thread.SfScadaDeviceFaultUploadThread;
import com.ginfon.sfclient.thread.SfScadaDeviceRuntimeInfoUploadThread;
import com.ginfon.sfclient.thread.SfScadaDeviceStatusUploadThread;
import com.ginfon.sfclient.thread.SfScadaResponseThread;
import com.ginfon.sfclient.util.ByteUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.*;


/**
 * 用来提交SCADA信息。
 *
 * @author Mark
 */
@Component
public class SFScadaClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(SFScadaClient.class);

	/**
	 *
	 */
	@Autowired
	private SFScadaConnector connector;

	@Autowired
	private GfScadaContainer gfScadaContainer;

	@Autowired
	private ScadaLauncher scadaLauncher;

	@Autowired
	private SFScadaCommandServiceImpl sfScadaCommandService;

	private SfScadaDeviceFaultUploadThread sfScadaDeviceFaultUploadThread;

	private SfScadaDeviceStatusUploadThread sfScadaDeviceStatusUploadThread;

	private SfScadaDeviceRuntimeInfoUploadThread sfScadaDeviceRuntimeInfoUploadThread, sfScadaDeviceRuntimeInfoUploadThread5, sfScadaDeviceRuntimeInfoUploadThread900;

	private SfScadaResponseThread sfScadaResponseThread;
	/**
	 * 序列号生成器。
	 */
	private SerialNumber serialNumber;

	/**
	 * 用来执行定时任务的。
	 */
	private ScheduledExecutorService threadPool;

	/**
	 * 线程池。
	 */
	private ExecutorService executor;


	public SFScadaClient() {
		//	序列号由4位byte组成，因此这里不需要设置最大值。
		this.serialNumber = new SerialNumber();
	}

	public void initializationCompleted() {
		//	启动连接
		this.connector.start();
		//	初始化线程池，最多4个线程的固定线程池。
		this.sfScadaDeviceFaultUploadThread = new SfScadaDeviceFaultUploadThread(this, gfScadaContainer);

		this.sfScadaDeviceStatusUploadThread = new SfScadaDeviceStatusUploadThread(this, gfScadaContainer);

		this.sfScadaDeviceRuntimeInfoUploadThread = new SfScadaDeviceRuntimeInfoUploadThread(this, scadaLauncher,gfScadaContainer);
		this.sfScadaResponseThread = new SfScadaResponseThread(this,gfScadaContainer,scadaLauncher);

        //	启动定时任务
		this.threadPool = Executors.newScheduledThreadPool(5, new DefaultThreadFactory("SfScadaInfoUploadThread-"));
		this.threadPool.scheduleAtFixedRate(this.sfScadaDeviceStatusUploadThread, 90, 120, TimeUnit.SECONDS);
		this.threadPool.scheduleAtFixedRate(this.sfScadaDeviceRuntimeInfoUploadThread, 2, 1, TimeUnit.SECONDS);
		this.threadPool.scheduleAtFixedRate(this.sfScadaResponseThread, 60, 2, TimeUnit.SECONDS);//同一时刻，只有一个线程在执行。

		this.executor = Executors.newFixedThreadPool(1, new DefaultThreadFactory("GfScadaDeviceFaultUploadThread-"));
		this.executor.execute(sfScadaDeviceFaultUploadThread);

	}


	/**
	 * 收到顺丰WCS发来的消息，处理报文。
	 *
	 * @param frame
	 */
	protected void sfScadaChannelRead(SFScadaMessageFrame frame) {
		int code = frame.getFunctionCode();
		byte[] resultMsg = null;
		if (code != 1) {
			LOGGER.info("顺丰Status端口发送报文调用功能函数:【{}】",code);
		}
		try {
			switch (code) {
				case 2://   初始化消息 2（SCADA -> client）
                    resultMsg = sfScadaCommandService.scadaInit(frame);
					break;
				case 400:// 启动/停止 400（SCADA ->client）
                    resultMsg = sfScadaCommandService.scadaStartAndStop(frame);
					break;
				case 421:// 功能操作消息 421（SCADA->client）
                    resultMsg = sfScadaCommandService.scadaOperate(frame);
					break;
				case 434:// 设备参数设置 434（SCADA->client）
                    resultMsg = sfScadaCommandService.scadaSetting(frame);
					break;
			}
			//	如果有返回值就考虑发给顺丰
            if (resultMsg.length > 0 && this.connector().channel() != null) {//	报文长度不为0且连接正常
                this.connector().channel().writeAndFlush(Unpooled.wrappedBuffer(resultMsg));
                LOGGER.info("【成功响应顺丰报文，功能码为：{}，内容为：{}】",code,ByteUtil.bytesToString16(resultMsg));
            } else {
                LOGGER.error("可能顺丰WCS客户端channel为空或者报文为空");
            }
		} catch (Exception e) {
			LOGGER.error(e.getCause().getMessage());
		}
	}

	/**
	 * 异步执行任务。
	 *
	 * @param runnable
	 */
	protected void execute(Runnable runnable) {
		this.executor.execute(runnable);
	}


	/**
	 * [ZXFJ->SCADA]<br>
	 * 提交运行时状态信息。335<br>
	 * 详细信息见标准化SCADA需求规格说明 6.2.3.14。<br>
	 * 每隔2s上传一次。
	 */
	public void submitRunntimeStatus() {
		ByteArrayOutputStream outArray = new ByteArrayOutputStream((28 + 4 + 6 + 2) * 10);

//        DeflectableWheelLine line = this.sortingDeviceContainer.getSingleLine();

		SerialNumber serialNumber = this.serialNumber;

		//	状态1：	2字节-电机电流
		{
			//	总长度
			byte[] body = new byte[40];
			//	固定头
			body[0] = -1;
			body[1] = -1;
			//	序列号
//            ByteUtil.intToByte(serialNumber.get(), body, 4, 4);
			//	时间戳

			//	线体编号
			//	a.设备类型

			//	b.设备编号
			//	c.设备状态
		}


		//	1.设备类型
		//	2.设备编号
		//	3.设备状态
		{
			//	一共17字节。
			//	状态1：	2字节-电机电流

			//	状态2：	2字节-电机频率
			//	状态3：	2字节-电机转速
			//	状态4：	2字节-电机温度

			//	状态9：	2字节-CPU温度（0.1℃）
			//	状态10：	1字节-CPU利用率（%）
			//	状态11：	1字节-内存利用率（%）
			//	状态12：	2字节-剩余内存（0.1GB）
			//	状态13：	1字节-磁盘利用率（%）
			//	状态14：	2字节-磁盘剩余空间（0.1GB）
		}
		//	循环
	}

	/**
	 * [ZXFJ->SCADA]<br>
	 * 提交启动/停止响应。300<br>
	 * 详细信息见标准化SCADA需求规格说明 6.2.3.5。<br>
	 * 响应400
	 */
	public void submitRunOrStopStatus() {

	}


	/**
	 * [ZXFJ->SCADA]<br>
	 * 功能状态上报 321<br>
	 * 详细信息见标准化SCADA需求规格说明 6.2.3.7。<br>
	 * 响应421
	 */
	public void operateMessageAck() {

	}

	/**
	 * [ZXFJ->SCADA]<br>
	 * 设备故障上报。330<br>
	 * 详细信息见标准化SCADA需求规格说明 6.2.3.8。<br>
	 */
	public void submitDeviceFault(String aliasName, ConcurrentHashMap<String, Integer> faultmap) {
		int size = faultmap.size();
		int msgLength = 31 + size * 7;
		byte[] message = new byte[msgLength];
		message[0] = -1;
		message[1] = -1;
		ByteUtil.intToByte(msgLength, message, 2, 2);
		ByteUtil.intToByte(serialNumber.get(), message, 4, 4);
		ByteUtil.longToByte(System.currentTimeMillis(), message, 9, 8);
		System.arraycopy(aliasName.getBytes(), 0, message, 17, 10);
		ByteUtil.intToByte(330, message, 29, 2);
		ByteUtil.intToByte(msgLength - 27, message, 27, 2);
		byte[] body = new byte[size * 7];
		for (String key : faultmap.keySet()) {
			int i = 0;
			String[] infos = key.split("_");
			int deviceType = Integer.parseInt(infos[1]);
			int deviceNo = Integer.parseInt(infos[2]);
			int index = Integer.parseInt(infos[3]);
			ByteUtil.intToByte(this.scadaLauncher.getDeviceTypemMap().get(deviceType), body, i * 7, 2);
			ByteUtil.intToByte(index, body, i * 7 + 2, 1);
			ByteUtil.intToByte(deviceNo, body, i * 7 + 3, 2);
			ByteUtil.intToByte(faultmap.get(key), body, i * 7 + 5, 2);
			i += 1;
		}
		System.arraycopy(body, 0, message, 31, size * 7);
		this.connector.channel().writeAndFlush(message);
	}

	/**
	 * [ZXFJ->SCADA]<br>
	 * 状态上报 331<br>
	 * 详细信息见标准化SCADA需求规格说明 6.2.3.9。<br>
	 * 2s推送一次
	 */
	public void submitDeviceStateInfo(String aliasName, ConcurrentHashMap<String, Integer> statusMap) {
		int size = statusMap.size();
		int msgLength = 31 + size * 6;
		byte[] message = new byte[msgLength];
		message[0] = -1;
		message[1] = -1;
		ByteUtil.intToByte(msgLength, message, 2, 2);
		ByteUtil.intToByte(serialNumber.get(), message, 4, 4);
		ByteUtil.longToByte(System.currentTimeMillis(), message, 9, 8);
		System.arraycopy(aliasName.getBytes(), 0, message, 17, 10);
		ByteUtil.intToByte(msgLength - 27, message, 27, 2);
		ByteUtil.intToByte(331, message, 29, 2);
		byte[] body = new byte[size * 6];
		int i = 0;
		for (String key : statusMap.keySet()) {
			String[] infos = key.split("_");
			// TODO: 2021/5/30 总控 按照线体号提交
			String sflineNo = infos[0];
			int deviceType = Integer.parseInt(infos[1]);
			int deviceNo = Integer.parseInt(infos[2]);
			int sfDeviceType = this.scadaLauncher.getDeviceTypemMap().get(deviceType);
			int sfStatus = getSfStatusByGfStatus(sfDeviceType, statusMap.get(key));
			ByteUtil.intToByte(sfDeviceType, body, i * 6, 2);
			ByteUtil.intToByte(deviceNo, body, i * 6 + 2, 2);
			ByteUtil.intToByte(sfStatus, body, i * 6 + 4, 2);
			i += 1;
		}
		System.arraycopy(body, 0, message, 31, size * 6);
		this.connector.channel().writeAndFlush(Unpooled.wrappedBuffer(message));
	}


	/**
	 * [ZXFJ->SCADA]<br>
	 * 事件上报 332<br>
	 * 详细信息见标准化SCADA需求规格说明 6.2.3.10。<br>
	 */
	public void submitEventInfo() {

	}


	/**
	 * [ZXFJ->SCADA]<br>
	 * 电压耗电量上报 333<br>
	 * 详细信息见标准化SCADA需求规格说明 6.2.3.11。<br>
	 * 2s推送一次
	 */
	public void submitPowerInfo() {

	}


	/**
	 * [ZXFJ->SCADA]<br>
	 * 设备参数上报 334<br>
	 * 详细信息见标准化SCADA需求规格说明 6.2.3.13。<br>
	 * 响应434
	 */
	public void DeviceSettingInfoAck() {

	}

	/**
	 * 获取序列号生成器，线程安全可以直接用{@link SerialNumber#get()}获取自增的序列号。
	 *
	 * @return
	 */
	public SerialNumber serialNumber() {
		return this.serialNumber;
	}


	public SFScadaConnector connector() {
		return this.connector;
	}

	public int getSfStatusByGfStatus(int sfDeviceType, int gfStatus) {
		String status = ByteUtil.toBinaryString(ByteUtil.intToByte(gfStatus, 2));
		//低八位 按值取对应顺丰状态值
		int low_status = gfStatus % 256;
		int low_sfStatus = this.scadaLauncher.getDeviceStatusConvertMap().get(sfDeviceType + "_" + low_status);
		//高8位  15 16 位为摆轮状态位 内部使用 其他 同顺丰直接取值
		String high_status = status.substring(2, 8);
		int high_sfStatus = Integer.parseInt(high_status, 2) * 256;
		return low_sfStatus + high_sfStatus;
	}
}
