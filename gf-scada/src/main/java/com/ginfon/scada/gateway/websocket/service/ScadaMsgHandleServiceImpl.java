package com.ginfon.scada.gateway.websocket.service;

import java.text.SimpleDateFormat;
import java.util.*;

import com.ginfon.main.ScadaClientContext;
import com.ginfon.scada.entity.CurrentStatus;
import com.ginfon.scada.entity.CurrentStatusEvent;
import com.ginfon.scada.service.ICurrentStatusServce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ginfon.scada.config.Constants;
import com.ginfon.scada.gateway.socket.channel.SocketChannelHandler;
import com.ginfon.scada.gateway.socket.util.StringUtil;
import com.ginfon.scada.gateway.websocket.config.dto.DeviceStatus;
import com.ginfon.scada.gateway.websocket.config.dto.LineCloggedStatus;
import com.ginfon.scada.util.ByteUtil;
import com.ks.util.KsConvert;
import com.xiaoleilu.hutool.date.DateUtil;


/**
 * 	用于处理各种报文的服务。比如WCS发来的设备状态报文。
 * @author Mark
 *
 */
@Component
public class ScadaMsgHandleServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScadaMsgHandleServiceImpl.class);
	
	
	@Value("${scada.socket.cloud.enable}")
	private boolean enable;

	@Value("${scada.socket.server.alias}")
	private String aliasName;
	
	
	@Autowired
	private WebsocketPushServiceImpl websocketPushService;

	/**
	 * 保存状态
	 */
	@Autowired
	private ICurrentStatusServce currentStatusServce;

	/**
	 * 	SCADA应用上下文对象。
	 */
	@Autowired
	private ScadaClientContext scadaClientContext;

	/**
	 * 语音报警信息
	 */
	public StringBuilder errMsgAudio=new StringBuilder();
	
	public ScadaMsgHandleServiceImpl() {

	}
	
	/**
	 * 	处理收到的报文。
	 * @param channelHandler	接收到报文的通道处理器
	 * @param srcMsg			报文源码
	 * @param msgBody			去掉6位报文头后的消息体
	 * @param index				该报文的序列号
	 * @param functionCode		功能码
	 * @param time				接收到报文的时间
	 */
	public void handle(SocketChannelHandler channelHandler, byte[] srcMsg, byte[] msgBody, int index, int functionCode, long time) {
		//	将数组转换为字符串
		String msg = ByteUtil.bytesToString16(srcMsg);
		
		LOGGER.info("收到报文:[功能码:{},序列号:{}]-[{}]", functionCode, index, msg);
		//	去掉所有的空格
		msg = msg.replace(" ", "");
		Object result = null;
		//	判断要如何处理
		switch (functionCode) {
		case 301:
			result = this.handleLineStatisMsg(msg.substring(20));

			this.websocketPushService.pushMessage("/topic/linestatus/receive",result);
			LOGGER.info("状态已经推送给layout");
			this.websocketPushService.pushMessage(Constants.TOPIC_URL_FOR_LINE_STATUS, result);

			break;
		case 204:
			result = this.handleBlockingMsg(msg.substring(20));
			this.websocketPushService.pushMessage(Constants.TOPIC_URL_FOR_BLOKCING_STATUS, result);
			break;
		case 209:
			result = this.handleSleepStartMsg(msg.substring(20), 209);
			this.websocketPushService.pushMessage(Constants.TOPIC_URL_FOR_SLEEP_TIME, result);
			break;
		case 210:
			result = this.handleSleepStartMsg(msg.substring(20), 210);
			this.websocketPushService.pushMessage(Constants.TOPIC_URL_FOR_START_TIME, result);
			break;
			
		default:
			break;
		}
	}
	
	/**
	 * 	处理堵包状态查询功能返回的报文。对应功能码：204。千灯
	 * @param msg	报文字符串
	 * @return	处理结果
	 */
	private List<LineCloggedStatus> handleBlockingMsg(String msg) {
		List<LineCloggedStatus> list = new ArrayList<>();
		try {
			int msgleng = msg.length() / 12;
			if (msg.length() % 12 == 0)
				for (int i = 0; i < msgleng; i++) {
					if ((i + 1) * 12 <= msg.length()) {
						LineCloggedStatus lcs = new LineCloggedStatus();
						String lineNoinfo = msg.substring(i * 12, i * 12 + 4);
						String strTime = msg.substring(i * 12 + 4, i * 12 + 8);
						String strhx = msg.substring(i * 12 + 8, i * 12 + 12);
						Integer sta = KsConvert.hexToInt(strhx);
						String lineNo = StringUtil.padLeft(Integer.toString(KsConvert.hexToInt(lineNoinfo)), 2);
						lcs.setSn(lineNo);
						lcs.setCloggedtime(KsConvert.hexToInt(strTime));
						lcs.setStatus(sta);
						list.add(lcs);
						LOGGER.info("WCS-堵包状态答复-线体号:{},堵包报警时长:{},堵包状态:{}", lineNoinfo, strTime, strhx);
					}
				}
			else
				LOGGER.info("WCS-堵包状态答复报文异常-报文不完整无法解析,收到的报文为：{}", msg);
		}catch (Exception e) {
			LOGGER.error("发生异常：", e);
		}
		return list;
	}
	
	/**
	 * 推送休眠状态（休眠时长）、启动时长（即每条线体启动的间隔）。
	 *
	 * @param msg
	 * @param type
	 * @return 
	 */
	public Object[] handleSleepStartMsg(String msg, int type) {
		Object[] oc = new Object[2];
		List<LineCloggedStatus> list = new ArrayList<>();

		int msgleng = msg.length() / 12;
		if (msg.length() % 12 == 0) {
			for (int i = 0; i < msgleng; i++) {
				if ((i + 1) * 12 <= msg.length()) {
					LineCloggedStatus lcs = new LineCloggedStatus();

					String lineNoinfo = msg.substring(i * 12, i * 12 + 4);

					String strTime = msg.substring(i * 12 + 4, i * 12 + 8);
					String status=msg.substring(i * 12 + 8, i * 12 + 12);
					String lineNo = StringUtil.padLeft(Integer.toString(KsConvert.hexToInt(lineNoinfo)), 2);
					lcs.setSn(lineNo);
					lcs.setCloggedtime(KsConvert.hexToInt(strTime));
					lcs.setStatus(KsConvert.hexToInt(status));
					list.add(lcs);
					String infostr = String.format("任务类型为【%s】,线体号为【%s】,时间为【{%s}】", type, lineNo, strTime);
					LOGGER.info(infostr);
				}
			}

			oc[0] = list;
			oc[1] = type;
		} else {
			String infostr = String.format("任务【%s】报文错误【%s】", type, msg);
			LOGGER.info(infostr);
		}
		return oc;
	}
	
	/**
	 * 	解析线体运行状态的报文。对应功能码：301。
	 * @param msg	报文字符串
	 * @return	处理结果
	 */
	private List<DeviceStatus> handleLineStatisMsg(String msg) {
		List<DeviceStatus> list = new ArrayList<>();
		List<String> listln = new ArrayList<>();
		errMsgAudio=new StringBuilder();
		int msgleng = msg.length() / 12;
		if (msg.length() % 12 != 0) {
			LOGGER.info("WCS-报文解析异常-报文不完整:收到的报文为：{}", msg);
		}
		for (int i = 0; i < msgleng; i++) {
			DeviceStatus deviceStatus = new DeviceStatus();
			//	这个是优先级列表
			List<Integer> sts = new LinkedList<Integer>();
			// logger.info("解析到第组");
			// 消息类型
			deviceStatus.setType(301);
			if ((i + 1) * 12 <= msg.length()) {
				String ln = msg.substring(i * 12, i * 12 + 4);
				if (!listln.contains(ln)) {
					listln.add(ln);
				}
				// 线体号
				String lineNoinfo = msg.substring(i * 12, i * 12 + 4);

				// 线体号
				deviceStatus.setSn(StringUtil.padLeft(String.valueOf(KsConvert.hexToInt(lineNoinfo)), 2));
				// 位置皮带马达编号，若位置为0则表示整个线体
				String motoNoinfo = msg.substring(i * 12 + 4, i * 12 + 8);
				// 位置皮带马达编号，若位置为0则表示整个线体
				deviceStatus.setTrayArea(KsConvert.hexToInt(motoNoinfo));
				// 设备状态
				String strhx = msg.substring(i * 12 + 8, i * 12 + 12);
				byte[] bts = parseHexStr2Byte(strhx);
				String stateStr = ByteUtil.toBinaryString(bts);
				String svtr = new StringBuilder(stateStr).reverse().toString();

					StringBuilder strb = new StringBuilder();
					//
					for (int j = 0; j < stateStr.length(); j++) {
						String v = svtr.substring(j, j + 1);
						if (j == 0) {
							if (v.equals("1")) {
								if (strb.indexOf("运行") == -1) {
									strb.append("运行;");
									sts.add(7);
								}
							} else {
								if (strb.indexOf("停止") == -1) {
									strb.append("停止;");
									sts.add(6);
								}
							}
						} else if (j == 1) {
							if (v.equals("1")) {
								if (strb.indexOf("待机") == -1) {
									strb.append("待机;");
									sts.add(9);
								}
							} else {
								if (strb.indexOf("复位") == -1) {
									strb.append("复位;");
									sts.add(10);
								}
							}
						} else if (j == 2) {
							if (v.equals("1")) {
								if (strb.indexOf("休眠") == -1) {
									strb.append("休眠;");
									sts.add(5);
								}
							} else {
								if (strb.indexOf("复位") == -1) {
									strb.append("复位;");
									sts.add(11);
								}
							}
						} else if (j == 3) {
							if (v.equals("1")) {
								if (strb.indexOf("暂停") == -1) {
									strb.append("暂停;");
									sts.add(12);
								}

							} else {
								if (strb.indexOf("复位") == -1) {
									strb.append("复位;");
									sts.add(13);
								}
							}
						} else if (j == 4) {
							if (v.equals("1")) {
								if (strb.indexOf("堵包") == -1) {
									strb.append("堵包;");
									sts.add(3);
								}
							} else {
								if (strb.indexOf("恢复") == -1) {
									strb.append("恢复;");
									sts.add(14);
								}
							}
						} else if (j == 5) {
							if (v.equals("1")) {
								if (strb.indexOf("急停") == -1) {
									strb.append("急停;");
									sts.add(1);
								}
							} else {
								if (strb.indexOf("释放") == -1) {
									strb.append("释放;");
									sts.add(16);
								}
							}
						} else if (j == 6) {
							if (v.equals("1")) {
								if (strb.indexOf("远程") == -1) {
									strb.append("远程;");
									sts.add(8);
								}
							} else {
								if (strb.indexOf("本地") == -1) {
									strb.append("本地;");
									sts.add(4);
								}
							}
						} else if (j == 7) {
							if (v.equals("1")) {
								if (strb.indexOf("过载") == -1) {
									strb.append("过载;");
									sts.add(2);
								}
							} else {
								if (strb.indexOf("恢复") == -1) {
									strb.append("恢复;");
									sts.add(15);
								}
							}
						} else if (j == 8) {
							if (v.equals("1")) {
								if (strb.indexOf("连接") == -1) {
									strb.append("连接;");
									sts.add(17);
								}
							} else {
								if (strb.indexOf("断开") == -1) {
									strb.append("断开;");
									sts.add(20);
								}
							}
						} else if (j == 9) {
							if (v.equals("1")) {
								if (strb.indexOf("光电触发") == -1) {
									strb.append("光电触发;");
									sts.add(18);
								}
							} else {
								if (strb.indexOf("恢复") == -1) {
									strb.append("恢复;");
									sts.add(19);
								}
							}
						} else if (j == 15) {
							if (v.equals("1")) {
								if (strb.indexOf("故障") == -1) {
									strb.append("故障;");
									sts.add(2);
								}
							} else {
								if (strb.indexOf("恢复") == -1) {
									strb.append("恢复;");
									sts.add(15);
								}
							}
						}
					}
					String infostr = String.format("线体号为【%s】马达编号为【%s】状态报文为【%s】转换为二进制【%s】,反转为【%s】集成状态为【%s】", lineNoinfo,
							motoNoinfo, strhx, stateStr, svtr, strb);
					LOGGER.info(infostr);
					deviceStatus.setStatusNew(sts);
					deviceStatus.setTriggerTime(DateUtil.now());

					list.add(deviceStatus);
					handlestatus(true,lineNoinfo,motoNoinfo,sts);
			} else {
				LOGGER.info("WCS-报文解析异常-报文不完整:收到的报文为：{}", msg);
			}
		}
		return list;
	}

	/**
	 * 操作状态
	 * @param check 是否检查
	 * @param lineNoinfo 父线体号
	 * @param motoinfo 皮带号
	 * @param staturArr 状态数组
	 */
	@Async
	public void handlestatus(boolean check,String lineNoinfo,String motoinfo,List<Integer> staturArr)
	{
		if (check)
		{
			boolean isregister=Isregister(lineNoinfo,motoinfo);
			if (!isregister)
			{
				return;
			}
		}
		CurrentStatusEvent cse=new CurrentStatusEvent();
		cse.setLimt(4);
		int msgLineNo=KsConvert.hexToInt(lineNoinfo);
		int msgdeviceNo=KsConvert.hexToInt(motoinfo);
		String mapdeviceNo;//实际设备号
		if (scadaClientContext.getMsgToMap().containsKey(msgLineNo))
		{
			Map<Integer,String> msgmap= scadaClientContext.getMsgToMap().get(msgLineNo);
			if (msgmap!=null&&msgmap.containsKey(msgdeviceNo))
			{
				mapdeviceNo=msgmap.get(msgdeviceNo);
			}else
			{
				mapdeviceNo=String.valueOf(msgdeviceNo);
			}

		}else
		{
			mapdeviceNo=String.valueOf(msgdeviceNo);
		}
		cse.setParentlineno(msgLineNo);
		cse.setLineno(mapdeviceNo);
		cse.setStatus(staturArr);
		List<CurrentStatus> currentStatuses=selectstatus(cse);//数据库搜索出来的状态,条件需要线体号,Limt,
		int statuslevel=cse.getLimt();

		List<Integer> insertList=new ArrayList<Integer>();//需插入的List
		List<Integer> updatendtime=new ArrayList<Integer>();//需要修改结束时间的
		List<Integer> alreadyStatus=new ArrayList<Integer>();//出现过的状态
		HashMap<Integer,Integer> typendmap=new HashMap<Integer, Integer>();//键为状态值为ID
		long timeMillis =System.currentTimeMillis();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String date = df.format(timeMillis) ;// new Date()为获取当前系统时间，也可使用当前时间戳
		for (int sta:staturArr)
		{
			if (currentStatuses!=null&&currentStatuses.size()>0) {//数据库搜索出来的状态

				for (int i = 0; i < currentStatuses.size(); i++) {
					int state = currentStatuses.get(i).getStateId();//获取搜索出来的状态
					int tableId= currentStatuses.get(i).getId();//取出ID

					if (state==sta)//如果存在该状态
					{
						if (!updatendtime.contains(tableId)&&!alreadyStatus.contains(sta))
						{
							updatendtime.add(tableId);
							alreadyStatus.add(sta);
						}

						if (insertList.contains(state))
						{
							int index=insertList.indexOf(state);
							insertList.remove(index);
						}
						if (typendmap.containsKey(sta))
						{
							typendmap.remove(sta);
						}
					}
					else//不存在该状态，则加入插入列表和结束列表
					{
						if (!insertList.contains(sta)&&!alreadyStatus.contains(sta))
						{
							insertList.add(sta);
						}
						if (!typendmap.containsKey(state)&&!updatendtime.contains(tableId))
						{
							typendmap.put(state,tableId);
						}
					}
				}
			}
			else //查不到该线体的信息，直接插入状态吧
			{
				insertList.add(sta);
			}
			if (typendmap.containsKey(sta))
			{
				typendmap.remove(sta);
			}
		}
		if (insertList.size()>0)
		{
			Map<String,String> clientContextDeviceMap= scadaClientContext.getDeviceMap();
			for (Integer stainsert: insertList)//循环插入的状态
			{
				if (stainsert<=statuslevel)
				{
					String lineName=clientContextDeviceMap.get(String.valueOf(cse.getParentlineno()));//获取线体名称
					String errinfo=getErrMsgByCode(stainsert);//异常信息
					String mapdeviceNo1=mapdeviceNo;//实际设备号
					errMsgAudio.append("输送线");
					errMsgAudio.append(lineName);
					errMsgAudio.append(mapdeviceNo1);
					errMsgAudio.append("号皮带发生异常");
					errMsgAudio.append(errinfo);
					CurrentStatus cs=new CurrentStatus();
					cs.setLineNo(cse.getLineno());
					cs.setParentlineNo(cse.getParentlineno());
					cs.setStateId(stainsert);
					cs.setType(1);
					cs.setStartime(date);
					cs.setEndtime(date);
					insertstatus(cs);
				}
			}
			//Audioflag=true;
			//websocketPushService.pushMessage("/audio/line",1);
		}
		if (updatendtime.size()>0)
		{
			for (int id:updatendtime)
			{
				CurrentStatus cs=new CurrentStatus(); //当前收到的状态与上一次状态一致,则修改结束时间继续活动需要id值
				cs.setId(id);
				cs.setEndtime(date);
				updateendtime(cs);
			}
		}
		if (typendmap.size()>0)
		{
			for (Integer id: typendmap.values())
			{
				CurrentStatus cs=new CurrentStatus();
				cs.setType(2);
				cs.setEndtime(date);
				cs.setId(id);
				updatestatus(cs);
			}
		}
	}

	/**
	 * 根据Code获取异常信息
	 * @param errCode 异常码
	 * @return
	 */
	public String getErrMsgByCode(Integer errCode)
	{
		String errInfo="";
		switch (errCode)
		{
			case 1:
				errInfo="急停";
				break;
			case 2:
				errInfo="故障";
				break;
			case 3:
				errInfo="堵包";
				break;
			case 4:
				errInfo="手动";
				break;
			case 5:
				errInfo="休眠";
				break;
			case 6:
				errInfo="停止";
				break;
			case 7:
				errInfo="运行";
				break;
		}
		return errInfo;
	}

	/**
	 * 	插入状态。
	 * @param log
	 * @return
	 */
	public void insertstatus(CurrentStatus log) {
		this.currentStatusServce.insertstatus(log);
	}


	/**
	 * 	修改状态。
	 * @param log
	 * @return
	 */
	public void updatestatus(CurrentStatus log) {
		this.currentStatusServce.updatestatus(log);
	}

	/**
	 * 修改结束时间
	 * @param log
	 */
	public void updateendtime(CurrentStatus log) {
		this.currentStatusServce.updateendtime(log);
	}


	/**
	 * 	查询状态。
	 * @param log
	 * @return
	 */
	public List<CurrentStatus> selectstatus(CurrentStatusEvent log) {
		return this.currentStatusServce.selectstatus(log);
	}

	/**
	 * 判断线体是否在配置文件中
	 * @param lineNo
	 * @param motoInfo
	 * @return
	 */
	public boolean Isregister(String lineNo,String motoInfo)
	{
		boolean isregister=true;
		try {
			int line =KsConvert.hexToInt(lineNo);
			int moto=KsConvert.hexToInt(motoInfo);
			String strline;
			String strmoto;
			if (line<10)
			{
				strline="0"+line;
			}
			else
			{
				strline=String.valueOf(line);
			}
			if (moto<10)
			{
				strmoto="0"+moto;
			}
			else
			{
				strmoto=String.valueOf(moto);
			}

			if (scadaClientContext.getDeviceMap().containsKey(strline))
			{
				String linename=scadaClientContext.getDeviceMap().get(strline);
				if (scadaClientContext.getLineMap().containsKey(linename))
				{
					String[] motoarr=scadaClientContext.getLineMap().get(linename);
					if (!Arrays.asList(motoarr).contains(strmoto))
					{
						isregister=false;
					}
				}
			}
			else
			{
				isregister=false;
			}
			return isregister;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	private byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}


}
