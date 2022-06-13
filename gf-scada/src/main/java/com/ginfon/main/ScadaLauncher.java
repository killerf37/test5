package com.ginfon.main;

import com.ginfon.manage.server.GfScadaServer;
import com.ginfon.scada.jikong.jikongServerCompent;
import com.ginfon.scada.service.IConveyorLineService;
import com.ginfon.sfclient.channel.SFScadaClient;
import com.ginfon.sfclient.channel.SFScadaConnector;
import com.ginfon.sfclient.entity.DeviceStatus;
import com.ginfon.sfclient.entity.DeviceType;
import com.ginfon.sfclient.entity.RuntimeInfo;
import com.ginfon.sfclient.service.IDeviceStatusService;
import com.ginfon.sfclient.service.IDeviceTypeService;
import com.ginfon.sfclient.service.IRuntimeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @description: Scada相关发射器
 * @author: curtain
 * @create: 2021-07-13 17:21
 **/
@Component
public class ScadaLauncher {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScadaLauncher.class);

	@Autowired
	private IConveyorLineService conveyorLineService;

	@Autowired
	private GfScadaServer gfScadaServer;
	@Autowired
	private SFScadaClient sfScadaClient;

	@Autowired
	private jikongServerCompent jkServerCompent;

	@Autowired
	private IDeviceTypeService deviceTypeService;

	@Autowired
	private IDeviceStatusService deviceStatusService;

	@Autowired
	private IRuntimeInfoService runtimeInfoService;
	/**
	 * 定长线程池。
	 */
	private final Executor executor;
	/**
	 * 以金峰分拣类型作为key,顺丰设备类型value。
	 */
	private HashMap<Integer, Integer> deviceTypemMap;
	/**
	 * 以顺丰分拣类型作为key,金峰设备类型value。
	 */
	private HashMap<Integer, Integer> gfDeviecTypeMap;

	/**
	 * 以金峰设备类型为key,元素名称为value
	 */
	private HashMap<Integer,String> gfElement;

	private HashMap<String, Integer> deviceStatusConvertMap;

	private HashMap<Integer, Integer> runtimeInfoIntvMap;

	public ScadaLauncher() {
		this.executor = Executors.newFixedThreadPool(64);
		this.deviceTypemMap = new HashMap<>();
		this.gfDeviecTypeMap = new HashMap<>();
		this.gfElement=new HashMap<>();
		this.deviceStatusConvertMap = new HashMap<>();
		this.runtimeInfoIntvMap = new HashMap<>();
	}

	@PostConstruct
	public void launch() {
		//启动总控SCADA服务
		this.executor.execute(new GfScadaServerThread(gfScadaServer));
		//启动顺丰-SCADA客户端，接收指令，上传数据用
		sfScadaClient.initializationCompleted();
		//启动集控解析线程
		jkServerCompent.initializationCompleted();
		//数据库设备相关内容初始化，缓存待用
		initDeviceData();
	}

	private void initDeviceData() {
		//设备类型
		List<DeviceType> deviceTypeList = deviceTypeService.selectDeviceTypeList();
		for (DeviceType dto : deviceTypeList) {
			this.deviceTypemMap.put(dto.ginFonDeviceType, dto.deviceType);
			this.gfDeviecTypeMap.put(dto.deviceType, dto.ginFonDeviceType);
			this.gfElement.put(dto.ginFonDeviceType,dto.eleName);
		}

		//设备状态
		List<DeviceStatus> deviceStatusList = deviceStatusService.selectDeviceStatusList();
		for (DeviceStatus dto : deviceStatusList) {
			if (dto.ginFonDeviceStatus != 0) {
				this.deviceStatusConvertMap.put(dto.deviceType + "_" + dto.ginFonDeviceStatus, dto.deviceStatus);
			}
		}
		//运行时数据提交频率
		List<RuntimeInfo> runtimeInfoList = runtimeInfoService.selectRuntimeInfoList();
		for (RuntimeInfo dto : runtimeInfoList) {
			this.runtimeInfoIntvMap.put(dto.dataType,dto.dataUploadFrequency);
		}
	}

	/**
	 * 获取异步执行器。
	 *
	 * @return
	 */
	public Executor getExecutor() {
		return this.executor;
	}

	public HashMap<Integer, Integer> getDeviceTypemMap() {
		return deviceTypemMap;
	}

	public HashMap<Integer, Integer> getGfDeviecTypeMap() {
		return gfDeviecTypeMap;
	}

	public HashMap<String, Integer> getDeviceStatusConvertMap() {
		return deviceStatusConvertMap;
	}

	public HashMap<Integer,String> getGfElement(){return gfElement;}
}
