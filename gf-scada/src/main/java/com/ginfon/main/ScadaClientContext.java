package com.ginfon.main;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import com.ginfon.manage.server.GfScadaServer;
import com.ginfon.scada.entity.ErrDTO;
import com.ginfon.scada.entity.MsgToMap;
import com.ginfon.scada.service.IErrInfoDescribService;
import com.ginfon.scada.service.IMsgToMapService;
import com.ginfon.sfclient.entity.DeviceType;
import com.ginfon.sfclient.service.IDeviceTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ginfon.scada.entity.ConveyorLine;
import com.ginfon.scada.gateway.socket.ScadaSocketContext;
import com.ginfon.scada.gateway.websocket.service.ServerConnStatusPushServiceImpl;
import com.ginfon.scada.service.IConveyorLineService;


/**
 * 	客户端上下文静态资源存放的地方。
 * @author Mark
 *
 */
@Component
public class ScadaClientContext implements DisposableBean {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScadaClientContext.class);
	
	/**
	 * 所有线体号容器
	 */
	private Map<String, String[]> lineMap = new HashMap<String, String[]>();
	/**
	 * 线体名称与代号
	 */
	private Map<String, String> deviceMap = new HashMap<String, String>();

	private Map<String, String> mapDevice = new HashMap<String, String>();

	private Map<String,String> deviceToMap=new HashMap<String, String>();

	private List<ConveyorLine> bailunInfo=new ArrayList<>();

	public Map<String, String> getDeviceNameIp() {
		return deviceNameIp;
	}

	/**
	 * 场地命名线体名称与IP地址的对应关系：2ZA-192.168.0.1
	 */
	private Map<String,String> deviceNameIp=new HashMap<>();

	public List<ConveyorLine> getBailunInfo() {
		return bailunInfo;
	}

	private Map<String,String> lineip=new LinkedHashMap<>();

	/**
	 * 报文与地图对应关系---lineNo<报文号,地图编号>
	 */
	private Map<Integer,Map<Integer,String>> msgToMap;

	@Value("${linedevice.lineip}")
	private String config;

	@Autowired
	private ScadaSocketContext scadaSocketContext;
	
	@Autowired
	private ServerConnStatusPushServiceImpl serverConnStatusPushServiceImpl;
	
    @Autowired
    private IConveyorLineService conveyorLineService;

	@Autowired
	private IMsgToMapService msgToMapService;

	@Autowired
	private IErrInfoDescribService errInfoDescribService;

	@Autowired
	private IDeviceTypeService deviceTypeService;

	/**
	 * 	定长线程池。
	 */
	private final Executor executor;
	
	
	public ScadaClientContext() {
		this.lineMap = new HashMap<String, String[]>();
		this.deviceMap = new HashMap<String, String>();
		this.mapDevice=new HashMap<String, String>();
		this.deviceToMap=new HashMap<>();
		this.msgToMap=new HashMap<Integer, Map<Integer, String>>();
		this.executor = Executors.newFixedThreadPool(64);
	}
	
	@PostConstruct
	public void start() {
		//
		this.loadLineDeviceConfiguration();
		//
		loadMsgToMap();
		//加载线体地址
		//loadlineip();
		//
		this.scadaSocketContext.launch();

		this.serverConnStatusPushServiceImpl.launch();
	}
	
	
	/**
	 * 将配置文件线体号内容读取至HasMap
	 */
	public void loadLineDeviceConfiguration() {
		String lineName, deviceNo;
		//	加载数据库
		List<ConveyorLine> list = conveyorLineService.selectConveyorLine();

		List<ConveyorLine> listBaiLun = conveyorLineService.selectBaiLunLine();

		bailunInfo =listBaiLun;
		for (ConveyorLine line : listBaiLun){
            this.mapDevice.put(line.getDeviceId(),String.valueOf(line.getLineNo()));
            this.deviceToMap.put(String.valueOf(line.getLineNo()),line.getDeviceName());
            this.deviceNameIp.put(line.getDeviceId(),line.getProudctId());
        }

		//	拼接line
		StringBuilder lineStr1 = new StringBuilder();
		StringBuilder lineStr2 = new StringBuilder();
		StringBuilder lineStr3 = new StringBuilder();
		for (ConveyorLine line : list) {
			lineStr1.append(line.getDeviceName()).append(",");
			lineStr2.append(line.getBeltLine()).append(";");

			// 判断线体号是不是单位数字（小于10）
			if (line.getLineNo() < 10)
				lineStr3.append(line.getDeviceName()).append(",0").append(line.getLineNo()).append(";");
			else
				lineStr3.append(line.getDeviceName()).append(",").append(line.getLineNo()).append(";");
		}
		lineStr1.deleteCharAt(lineStr1.length() - 1);
		lineStr2.deleteCharAt(lineStr2.length() - 1);
		lineStr3.deleteCharAt(lineStr3.length() - 1);

		lineName = lineStr1.toString() + ":" + lineStr2.toString();
		deviceNo = lineStr3.toString();
		if (lineName != null && lineName.length() != 0 && deviceNo != null && deviceNo.length() != 0) {
			String[] strmap = lineName.split(":");
			String[] strdevice = deviceNo.split(";");
			if (strmap.length == 2) {
				String[] lineHead = strmap[0].split(",");
				String[] lineNo = strmap[1].split(";");
				if (lineHead != null && lineHead.length == lineNo.length) {
					for (int i = 0; i < lineHead.length; i++) {
						String[] lineNoArr = lineNo[i].split(",");
						this.lineMap.put(lineHead[i], lineNoArr);
					}
				} else {
					LOGGER.error("线体号Line配置文件有错误：也许线体头号与线体号分号不匹配");
				}
			} else {
				LOGGER.error("线体号Line配置文件有错误：也许线体头号与线体号之间无冒号");
			}
			if (strdevice.length > 0) {
				for (int i = 0; i < strdevice.length; i++) {
					String[] device = strdevice[i].split(",");
					if (device.length == 2) {
						this.deviceMap.put(device[1], device[0]);
					} else {
						LOGGER.error("线体号deviceNo配置文件有错误：也许线体名称与线体序号配置有问题");
					}
				}
			}
		}
	}

	/**
	 * 加载报文地图信息
	 */
	public void loadMsgToMap()
	{
		List<MsgToMap> msgToMapList=this.msgToMapService.selectMsgToMapList();
		if (msgToMapList!=null)
		{
			for (int i=0;i<msgToMapList.size();i++)
			{
				int lineNo=msgToMapList.get(i).getLineNo();
				int msgNo=msgToMapList.get(i).getMsgNo();
				String mapNo=msgToMapList.get(i).getMapNo();
				if (msgToMap.containsKey(lineNo))
				{
					msgToMap.get(lineNo).put(msgNo,mapNo);
				}else
				{
					HashMap<Integer,String> mapValue=new HashMap<>();
					mapValue.put(msgNo,mapNo);
					msgToMap.put(lineNo,mapValue);
				}
			}
		}else
		{

		}
	}

	/**
	 * 异常信息描述
	 * @return
	 */
	public HashMap<Integer, HashMap<Integer, String>> getErrinfo()
	{
		List<ErrDTO> faultDTOList=new ArrayList<>();
		HashMap<Integer,HashMap<Integer,String>> errDescrib=new HashMap<>();

		List<DeviceType> deviceTypeList=deviceTypeService.selectDeviceTypeList();
		if (deviceTypeList.size()>0)
		{
			for(DeviceType deviceType:deviceTypeList)
			{
				HashMap<Integer,String> errCodeDesc=new HashMap<>();
				errDescrib.put(deviceType.ginFonDeviceType,errCodeDesc);
				faultDTOList=errInfoDescribService.selectErrInfo(deviceType.ginFonDeviceType);
				if (faultDTOList.size()>0)
				{
					for (ErrDTO faultDTO:faultDTOList)
					{
						errCodeDesc.put(faultDTO.errCode,faultDTO.errDescrib);
					}
				}
			}
		}
		return errDescrib;
	}

	/**
	 * 获取设备类型名称
	 * @return
	 */
	public HashMap<Integer, String> getDeviceType()
	{
		HashMap<Integer,String> typeDescrip=new HashMap<>();
		try{
			List<DeviceType> deviceTypeDTOList=deviceTypeService.selectDeviceTypeList();
			if (deviceTypeDTOList.size()>0)
			{
				for (DeviceType deviceTypeDTO:deviceTypeDTOList)
				{
					if (!typeDescrip.containsKey(deviceTypeDTO.ginFonDeviceType))
					{
						typeDescrip.put(deviceTypeDTO.ginFonDeviceType,deviceTypeDTO.deviceDesc);
					}
				}
			}
		}catch (Exception e)
		{

		}
		return typeDescrip;
	}



	public Map<Integer,Map<Integer,String>> getMsgToMap()
	{
		return this.msgToMap;
	}
	
	public Map<String, String[]> getLineMap(){
		return this.lineMap;
	}
	
	public Map<String, String> getDeviceMap(){
		return this.deviceMap;
	}

	public Map<String, String> getMapDevice(){
		return this.mapDevice;
	}

	public Map<String,String> getdeviceToMap(){
		return this.deviceToMap;
	}



	/**
	 * 加载线体信息与ip
	 */
	private void loadlineip()
	{
		if (config!=null)
		{
			String[] iparry=config.split(";");
			if (iparry.length>0)
			{
				for (int i=0;i<iparry.length;i++)
				{
					String[] ipe=iparry[i].split(",");
					if (ipe.length==2)
					{
						lineip.put(ipe[0],ipe[1]);
					}
				}
			}
		}
	}

	/**
	 *
	 * @return
	 */
	public Map<String,String> getLineip()
	{
		return lineip;
	}
	
	/**
	 * 	获取异步执行器。
	 * @return
	 */
	public Executor getExecutor() {
		return this.executor;
	}
	
	@Override
	public void destroy() throws Exception {
		
	}
}
