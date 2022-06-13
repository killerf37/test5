package com.ginfon.scada.jikong;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ginfon.main.ScadaClientContext;
import com.ginfon.manage.container.GfScadaContainer;
import com.ginfon.scada.entity.DeviceFaultDTO;
import com.ginfon.scada.entity.ScadaStatusDTO;
import com.ginfon.scada.entity.StatusAndErrResult;
import com.ginfon.scada.gateway.websocket.service.ScadaMsgHandleServiceImpl;
import com.ginfon.scada.gateway.websocket.service.WebsocketPushServiceImpl;
import com.ginfon.scada.service.IFaultLogService;
import com.ginfon.scada.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/12/16/11:51
 * @Description:
 */
public class jikongThread  implements Runnable{

    private static final Logger LOGGER= LoggerFactory.getLogger(jikongThread.class);

    private GfScadaContainer gfScadaContainer;

    private Map<String, ConcurrentHashMap<String, Integer>> deviceStatusMapLast;

    private Map<String, ConcurrentHashMap<String, Integer>> deviceFaultMapLast;

    private ConcurrentHashMap<String,Integer> lastStatusMap;

    private ConcurrentHashMap<String,Integer> lastFaultMap;

    private IFaultLogService faultLogService;

    private WebsocketPushServiceImpl websocketPushService;

    private ScadaMsgHandleServiceImpl scadaMsgHandleService;

    private Map<String,ConcurrentHashMap<Integer,Integer>> errInfoMapLast;//当前异常信息集合，

    //插入异常的标志
    private Boolean insertTable=false;

    public jikongThread(GfScadaContainer gfScadaContainer1, IFaultLogService faultLogService1, Map<String,ConcurrentHashMap<String,Integer>> deviceStatusMap1, Map<String,ConcurrentHashMap<String,Integer>> deviceFaultMap1, WebsocketPushServiceImpl websocketPushService1,ScadaMsgHandleServiceImpl scadaMsgHandleService1)
    {
        gfScadaContainer=gfScadaContainer1;
        faultLogService=faultLogService1;
        deviceStatusMapLast=deviceStatusMap1;
        deviceFaultMapLast=deviceFaultMap1;
        websocketPushService=websocketPushService1;
        lastStatusMap=new ConcurrentHashMap<>();
        lastFaultMap=new ConcurrentHashMap<>();
        scadaMsgHandleService=scadaMsgHandleService1;
    }

    @Override
    public void run() {
        try {
            //List<DeviceFaultDTO> deviceFaultDTOList1=faultLogService.selectLineFault(30);
            //状态列表
            List<ScadaStatusDTO> scadaStatusDTOSList = new ArrayList<>();
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long nowTimeSpan=System.currentTimeMillis();
            //设备状态map
            Map<String, ConcurrentHashMap<String, Integer>> deviceStatusMap=gfScadaContainer.getDeviceStatusMap();
            //设备异常map
            Map<String,ConcurrentHashMap<String,Integer>> deviceFaultMap=gfScadaContainer.getDeviceFaultMap();
            //获取异常状态
            Map<String,ConcurrentHashMap<String,String>> deviceErrInfoMap=gfScadaContainer.getDeviceErrInfoMap();

            //Iterator<Map.Entry<String,ConcurrentHashMap<String, Integer>>> iterator = deviceStatusMap.entrySet().iterator();
            if(deviceStatusMap!=null&&deviceStatusMap.size()>0)
            {
                Iterator<String> iter=deviceStatusMap.keySet().iterator();
                while (iter.hasNext())
                {
                    String linekey=iter.next();//取出键，线号为key,键为ip
                    LOGGER.info("设备状态键为{}",linekey);
                    if (gfScadaContainer.getOnline().containsKey(linekey))
                    {
                        Long lastTimeSpan=gfScadaContainer.getOnline().get(linekey);
                        String now=dateFormat.format(nowTimeSpan);
                        String last=dateFormat.format(lastTimeSpan);
                        Long span=nowTimeSpan-lastTimeSpan;
                        if (span>1500)
                        {
                            return;
                        }
                    }
                    ConcurrentHashMap<String,Integer> statusHasMap=deviceStatusMap.get(linekey);//取出值,线号---状态值
                    if (deviceFaultMap.containsKey(linekey))
                    {
                        LOGGER.info("异常map<<yml><config,fault>>里包含yml键{}",linekey);
                        ConcurrentHashMap<String,Integer> faultHasMap=deviceFaultMap.get(linekey);//取出值,线号----异常值
                        //取出各条具体状态
                        Iterator<String> statusiter=statusHasMap.keySet().iterator();
                        while (statusiter.hasNext())
                        {
                            String stauskey=statusiter.next();//取出状态值的key
                            Integer statusInt=statusHasMap.get(stauskey);

                            if (faultHasMap.containsKey(stauskey))
                            {
                                LOGGER.info("异常map<config,fault>里包含键{}",stauskey);
                                //取出key中的线号，类型，设备号
                                String[] infos=stauskey.split("_");

                                if (infos!=null&&infos.length==5)
                                {
                                    String lineNo=infos[1];//线号
                                    Integer deviceType=Integer.parseInt(infos[2]);//设备类型
                                    Integer deviceNo=Integer.parseInt(infos[3]);//设备号

                                    Integer faultInt=faultHasMap.get(stauskey);//取出异常码值
                                    byte[] stbytes=ByteUtil.intToByte(statusInt,2);
                                    ScadaStatusDTO scadaStatusDTO=new ScadaStatusDTO();
                                    scadaStatusDTO.setLineNo(lineNo);
                                    scadaStatusDTO.setDeviceType(deviceType);
                                    scadaStatusDTO.setDevieNo(deviceNo);
                                    scadaStatusDTO.setStatus(statusInt);
                                    scadaStatusDTO.setStatusIndex(0);
                                    scadaStatusDTO.setFault(faultInt);
                                    scadaStatusDTO.opearStatus(deviceNo,deviceType,stbytes[1],stbytes[0],faultInt);
                                    scadaStatusDTOSList.add(scadaStatusDTO);
                                    //CheckStatusAndFault(linekey,stauskey,faultInt,statusInt,scadaStatusDTO);//检查是否插入

                                    lastStatusMap.put(stauskey,statusInt);
                                    lastFaultMap.put(stauskey,faultInt);

                                    deviceStatusMapLast.put(linekey,lastStatusMap);
                                    deviceFaultMapLast.put(linekey,lastFaultMap);
                                }else
                                {
                                    LOGGER.info("key值异常,长度为{}",infos.length);
                                }
                            }else
                            {
                                LOGGER.info("!异常map<config,fault>里不包含键{}",stauskey);
                            }
                        }
                    }else
                    {
                        LOGGER.info("!异常map<<yml><config,fault>>里不包含yml键{}",linekey);
                    }
                }
            }
            LOGGER.info("推送状态对象{}个",scadaStatusDTOSList.size());
            websocketPushService.pushMessage("/jk/status",scadaStatusDTOSList);

            if (deviceErrInfoMap.size()>0)
            {
                websocketPushService.pushMessage("/audio/baojing",deviceErrInfoMap);
            }
            if (!"".equalsIgnoreCase(scadaMsgHandleService.errMsgAudio.toString()))
            {
                websocketPushService.pushMessage("/audio/line",scadaMsgHandleService.errMsgAudio.toString());
            }

            if (deviceErrInfoMap.size()>0)//从分控收到的异常
            {
                List<String> errInfoList=new ArrayList<>();
                Iterator<String> iterErr=deviceErrInfoMap.keySet().iterator();
                boolean isupdate=false;
                while (iterErr.hasNext())
                {
                    String keyLine=iterErr.next();
                    ConcurrentHashMap<String,String> errInfomap=deviceErrInfoMap.get(keyLine);//获取分控异常键值对
                    Iterator<String> lineconfigKey=errInfomap.keySet().iterator();//获取<line1,<line2,errinfo>>中的line2
                    while (lineconfigKey.hasNext())
                    {
                        String lineConfig=lineconfigKey.next();
                        String errInfo=errInfomap.get(lineConfig);//获取到异常信息
                        JSONObject jsonObject=JSONObject.parseObject(errInfo);//获取到异常信息
                        Integer lineNo=(Integer) jsonObject.get("lineNo");
                        Integer count=(Integer) jsonObject.get("count");
                        BigInteger maxId=new BigInteger (jsonObject.get("maxId").toString());

                        List<DeviceFaultDTO> deviceFaultDTOList=faultLogService.selectLineFault(lineNo);
                        if (deviceFaultDTOList.size()>0)
                        {
                            JSONArray oj=(JSONArray) jsonObject.get("errinfo");
                            BigInteger clientId1=deviceFaultDTOList.get(deviceFaultDTOList.size()-1).getClientId();
                            int errcount=deviceFaultDTOList.size();
                            if (!clientId1.equals(maxId)||errcount!=count)
                            {
                                HashMap<BigInteger,BigInteger> idmap=new HashMap<>();
                                List<BigInteger> clientId=new ArrayList<>();//分控异常Id的集合
                                List<BigInteger> updateIdList=new ArrayList<>();//更新掉的Id集合

                                for (int j=0; j<deviceFaultDTOList.size();j++)
                                {
                                    idmap.put(deviceFaultDTOList.get(j).getClientId(),deviceFaultDTOList.get(j).getID());
                                }

                                for(int j=0;j<oj.size();j++)
                                {
                                    JSONObject errinfoTable= (JSONObject) oj.get(j);
                                    BigInteger id=(BigInteger) errinfoTable.get("ID");
                                    clientId.add(id);
                                    if (!idmap.containsKey(id))//异常不存在
                                    {
                                        DeviceFaultDTO deviceFaultDTO=new DeviceFaultDTO();
                                        deviceFaultDTO.setlineNo(errinfoTable.get("lineNo").toString());
                                        deviceFaultDTO.setdeviceNo((Integer)errinfoTable.get("deviceNo"));
                                        deviceFaultDTO.setdeviceType((Integer)errinfoTable.get("deviceType"));
                                        deviceFaultDTO.setfault((Integer)errinfoTable.get("fault"));

                                        deviceFaultDTO.setClientId((BigInteger)errinfoTable.get("clientId"));
                                        deviceFaultDTO.setLineName(errinfoTable.get("lineName").toString());
                                        deviceFaultDTO.setDevicePhysicalNo(errinfoTable.get("devicePhysicalNo").toString());
                                        deviceFaultDTO.setFaultDescrib(errinfoTable.get("faultDescrib").toString());
                                        deviceFaultDTO.setTypeDescrib(errinfoTable.get("typeDescrib").toString());

                                        deviceFaultDTO.setfaultIndex(1);
                                        Timestamp ts=Timestamp.valueOf(errinfoTable.get("faultTriggerTime").toString());
                                        deviceFaultDTO.setfaultTriggerTime(ts);

                                        faultLogService.insertFault(deviceFaultDTO);
                                        isupdate=true;
                                    }
                                }
                                Iterator ite=idmap.entrySet().iterator();
                                while (ite.hasNext())
                                {
                                    BigInteger cid=(BigInteger) ite.next();
                                    if (!clientId.contains(cid))
                                    {
                                        BigInteger zId=idmap.get(cid);
                                        updateIdList.add(zId);
                                        faultLogService.updateFault(zId);
                                        isupdate=true;
                                    }
                                }
                            }
                        }else
                        {
                            JSONArray oj=(JSONArray) jsonObject.get("errinfo");
                            for(int j=0;j<oj.size();j++)
                            {
                                JSONObject errinfoTable= (JSONObject) oj.get(j);
                                BigInteger id=new BigInteger(errinfoTable.get("ID").toString());

                                DeviceFaultDTO deviceFaultDTO=new DeviceFaultDTO();
                                deviceFaultDTO.setlineNo(errinfoTable.get("lineNo").toString());
                                deviceFaultDTO.setdeviceNo((Integer)errinfoTable.get("deviceNo"));
                                deviceFaultDTO.setdeviceType((Integer)errinfoTable.get("deviceType"));
                                deviceFaultDTO.setfault((Integer)errinfoTable.get("fault"));

                                deviceFaultDTO.setClientId(id);
                                deviceFaultDTO.setLineName(errinfoTable.get("lineName").toString());
                                deviceFaultDTO.setDevicePhysicalNo(errinfoTable.get("devicePhysicalNo").toString());
                                deviceFaultDTO.setFaultDescrib(errinfoTable.get("faultDescrib").toString());
                                deviceFaultDTO.setTypeDescrib(errinfoTable.get("typeDescrib").toString());

                                deviceFaultDTO.setfaultIndex(1);
                                Timestamp ts=Timestamp.valueOf(errinfoTable.get("faultTriggerTime").toString());
                                deviceFaultDTO.setfaultTriggerTime(ts);

                                faultLogService.insertFault(deviceFaultDTO);
                                isupdate=true;
                            }
                        }
                    }
                }
                if (isupdate)
                {
                    List<DeviceFaultDTO> faultDTOList=faultLogService.selctTopFault();
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("list",faultDTOList);
                    //websocketPushService.pushMessage("/jk/errinfo",JSONObject.toJSONStringWithDateFormat(jsonObject,"yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat));
                    String jsonmsg=JSONObject.toJSONStringWithDateFormat(jsonObject,"yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat);
                    websocketPushService.pushMessage("/jk/errinfo",jsonmsg);
                    if (insertTable)
                    {
                        //websocketPushService.pushMessage("/audio/baojing",1);
                        insertTable=false;
                    }
                    isupdate=false;
                }
            }

        }catch (Exception err)
        {
            LOGGER.info("插入数据库失败");
        }
    }


    /**
     *检查该状态与异常是否与容器中的状态一致
     * @author fanhan
     * @createby 2021/5/10
     * @return 第一位表示状态第二位表示异常。1：无变化，2有变化，3容器里无该设备状态，4传入的值为空
     */
    public void CheckStatusAndFault(String linekey,String lineMsgkey,Integer fault,Integer status,ScadaStatusDTO statusDTO)
    {
        StatusAndErrResult statusAndErrResult=new StatusAndErrResult();
        boolean isupdate=false;
        if (deviceStatusMapLast.containsKey(linekey)&&deviceFaultMapLast.containsKey(linekey))
        {
            ConcurrentHashMap<String,Integer> statusHasMap=deviceStatusMapLast.get(linekey);
            ConcurrentHashMap<String,Integer> faultHasMap=deviceFaultMapLast.get(linekey);

            if (statusHasMap.containsKey(lineMsgkey)&&faultHasMap.containsKey(lineMsgkey))
            {
                Integer state=statusHasMap.get(lineMsgkey);
                Integer faultCode=faultHasMap.get(lineMsgkey);
                if (fault.equals(faultCode)&&state.equals(status))
                {

                }else //LastMap键值对值与上一次不一致，开始数据库检查取值，并将异常信息更新
                {
                    statusAndErrResult=operateFault(lineMsgkey,statusDTO,fault);
                }
            }else //LastMap键值对不存在该线体信息，开始数据库检查取值，并将异常信息更新
            {
                statusAndErrResult=operateFault(lineMsgkey,statusDTO,fault);
            }
        }else//LastMap键值对不存在该线体信息，开始数据库检查取值，并将异常信息更新
        {
            statusAndErrResult=operateFault(lineMsgkey,statusDTO,fault);
        }

        if (statusAndErrResult.getInsertList().size()>0)
        {
            for (ScadaStatusDTO scadaStatusDTO:statusAndErrResult.getInsertList())
            {
                for (Integer faultCode:scadaStatusDTO.getStatusList())
                {
                    DeviceFaultDTO deviceFaultDTO=new DeviceFaultDTO(scadaStatusDTO,faultCode);
                    faultLogService.insertFault(deviceFaultDTO);
                }
            }
            isupdate=true;
        }

        if (statusAndErrResult.getUpdateList().size()>0)
        {
            for (BigInteger id:statusAndErrResult.getUpdateList().keySet())
            {

                faultLogService.updateFault(id);
            }
            isupdate=true;
        }
        //更新过map。更新页面
        if (isupdate)
        {
            List<DeviceFaultDTO> faultDTOList=faultLogService.selctTopFault();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("list",faultDTOList);
            String jsonmsg=JSONObject.toJSONStringWithDateFormat(jsonObject,"yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat);
            websocketPushService.pushMessage("/jk/errinfo",jsonmsg);
        }
    }

    /**
     * 处理线体异常信息
     * @param lineMsgkey
     * @param statusDTO
     * @param fault
     * @return
     */
    public StatusAndErrResult operateFault(String lineMsgkey,ScadaStatusDTO statusDTO,Integer fault)
    {
        List<Integer> listerr=new ArrayList<Integer>();//当前异常的集合
        StatusAndErrResult statusAndErrResult=new StatusAndErrResult();
        List<DeviceFaultDTO> deviceFaultDTOList=faultLogService.selctFault(statusDTO);

        ScadaStatusDTO scadaStatusDTO=new ScadaStatusDTO();
        scadaStatusDTO.setLineNo(statusDTO.getLineNo());
        scadaStatusDTO.setDeviceType(statusDTO.getDeviceType());
        scadaStatusDTO.setDevieNo(statusDTO.getDeviceNo());

        List<Integer> statusErr=getStatusErr(statusDTO.getStatusList(),statusDTO.getDeviceType());

        String svtr=Integer.toBinaryString(fault);//转换异常码

        for(int i=0;i<statusErr.size();i++)
        {
            if (!listerr.contains(statusErr.get(i)))
            {
                listerr.add(statusErr.get(i));
            }
        }
        String errstr = new StringBuilder(svtr).reverse().toString();

        for (int i=0;i<errstr.length();i++)
        {
            String errSub=errstr.substring(i,i+1);
            if (errSub=="1"||errSub.equals("1"))
            {
                if (!listerr.contains(i+1))
                {
                    listerr.add(i+1);
                }
            }
        }

        if (deviceFaultDTOList!=null&&deviceFaultDTOList.size()>0)//如果数据库有该设备数据
        {
            for (int j=0;j<deviceFaultDTOList.size();j++)
            {
                if (listerr.contains(deviceFaultDTOList.get(j).getfault()))
                {
                    listerr.remove(deviceFaultDTOList.get(j).getfault());
                }
                else
                {
                    statusAndErrResult.addUpdateFaultDTO(deviceFaultDTOList.get(j).getID(),deviceFaultDTOList.get(j).getfault());
                }
            }
            if (listerr.size()>0)//如果遍历数据库之后还有不包含的状态，则继续插入数据库
            {
                for(int j=0;j<listerr.size();j++)
                {
                    scadaStatusDTO.addStatusList(listerr.get(j));
                }
                scadaStatusDTO.setFaultTriggerTime(new Timestamp(System.currentTimeMillis()));
                statusAndErrResult.addInsertFaultDTO(scadaStatusDTO);
            }
        }
        else //数据库不存在该状态，则加入插入Hashmap
        {
            //System.out.println("开始插入Hashmap");
            if (listerr.size()>0)
            {
                for (int i=0;i<listerr.size();i++)
                {
                    scadaStatusDTO.addStatusList(listerr.get(i));
                }
                scadaStatusDTO.setFaultTriggerTime(new Timestamp(System.currentTimeMillis()));
                statusAndErrResult.addInsertFaultDTO(scadaStatusDTO);
            }
        }
        return statusAndErrResult;
    }
    //获取插入标志
    public Boolean getInsertTable()
    {
        return insertTable;
    }
    //设置插入标志
    public void setInsertTable(Boolean insertflag)
    {
        insertTable=insertflag;
    }

    /**
     * 获取状态中存在的异常
     * @param errlist
     * @param devicetype
     * @return
     */
    public List<Integer> getStatusErr(List<Integer> errlist,int devicetype)
    {
        List<Integer> integerList=new ArrayList<>();
        if (devicetype==0)//整线
        {

        }else if (devicetype==16)//急停按钮
        {
            if (errlist.contains(2))//急停按下
            {
                integerList.add(1);
            }
            if (errlist.contains(3))//松开未复位
            {
                integerList.add(2);
            }
        }else if (devicetype==6||devicetype==7||devicetype==9)//按钮和光电和远程IO站
        {

        }else
        {
//            if (errlist.contains(2))//如果存在急停
//            {
//                integerList.add(10);
//            }
            if (errlist.contains(7))//如果存在手动
            {
                integerList.add(11);
            }
            if (errlist.contains(6))//如果存在堵塞
            {
                integerList.add(12);
            }
        }
        return integerList;
    }
}
