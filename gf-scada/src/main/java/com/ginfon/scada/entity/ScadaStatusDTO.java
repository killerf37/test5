package com.ginfon.scada.entity;

import com.ginfon.scada.util.ByteUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/12/16/19:18
 * @Description:
 */
public class ScadaStatusDTO {
    private String lineNo;

    private Integer deviceType;

    private Integer deviceNo;

    private Integer statusIndex;

    private Integer status;

    private Integer wheelForward;

    private Integer fault;

    private Integer faultIndex;

    private List<Integer> statusList;

    private Timestamp faultTriggerTime;
    private Timestamp faultReleaseTime;

    public Timestamp getFaultTriggerTime() {
        return faultTriggerTime;
    }

    public void setFaultTriggerTime(Timestamp faultTriggerTime) {
        this.faultTriggerTime = faultTriggerTime;
    }

    public Timestamp getFaultReleaseTime() {
        return faultReleaseTime;
    }

    public void setFaultReleaseTime(Timestamp faultReleaseTime) {
        this.faultReleaseTime = faultReleaseTime;
    }

    public ScadaStatusDTO()
    {
        statusList=new ArrayList<>();
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getDeviceNo() {
        return deviceNo;
    }

    public void setDevieNo(Integer deviceNo) {
        this.deviceNo = deviceNo;
    }

    public Integer getStatusIndex() {
        return statusIndex;
    }

    public void setStatusIndex(Integer statusIndex) {
        this.statusIndex = statusIndex;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getFault() {
        return fault;
    }

    public void setFault(Integer fault) {
        this.fault = fault;
    }

    public Integer getFaultIndex() {
        return faultIndex;
    }

    public void setFaultIndex(Integer faultIndex) {
        this.faultIndex = faultIndex;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getWheelForward() {
        return wheelForward;
    }

    public void setWheelForward(Integer wheelForward) {
        this.wheelForward = wheelForward;
    }

    public void addStatusList(Integer sta){statusList.add(sta);}

    public void removeStatusListEle(Integer sta)
    {
        if(statusList.contains(sta))
        {
            statusList.remove(sta);
        }
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    /**
     * 状态解析,将偏转轮方向赋值给WheelForward(10右01左00中),将状态赋值给list[0],手自动[1]0本地1远程,堵塞状态[2]0正常1堵塞
     *
     * @param lowsta
     * @param highsta
     * 1禁用2急停3暂停4临时停机5故障6堵塞7手动8休眠9停止10运行11自动
     */
    public void opearStatus(int devieNo,int deviceType,byte lowsta, byte highsta,int fault)
    {
        int st1=lowsta&0xFF;
        if (deviceType==0)
        {
            statusList.add(scopeSwitch(st1));
            String st2 = ByteUtil.toBinaryString(highsta);
            if (st2.length()==8)
            {
                for (int i=4;i<st2.length();i++)
                {
                    if (st2.substring(i,i+1).equals("1"))
                    {
                        statusList.add(1);
                    }else
                    {
                        statusList.add(0);
                    }
                }
            }
        }
        else if (deviceType==16)//急停按钮
        {
            statusList.add(buttonJT(st1));
            if (fault>0)
            {
                statusList.add(5);//添加故障信息
            }
        }
        else if (deviceType==6||deviceType==7||deviceType==9)
        {
            if (st1==0)
            {
                statusList.add(9);
            }
            else
            {
                statusList.add(10);
            }
            if (fault>0)
            {
                statusList.add(5);
            }
        }
        else//单机状态
        {
            statusList.add(scopeSwitch(st1));
            if (fault>0)
            {
                statusList.add(5);//添加故障信息
            }
            String st2 = ByteUtil.toBinaryString(highsta);
            if (st2.length()==8)
            {
                Integer foward=Integer.parseInt(st2.substring(0,2));
                if (foward==10)//右
                {
                    wheelForward=10;
                }else if(foward==1)//左
                {
                    wheelForward=1;
                }else if(foward==0)//中间
                {
                    wheelForward=0;
                }else
                {
                    System.out.println("摆轮方向状态异常:"+foward);
                }
                String autoStatus=st2.substring(7);
                String normalStatus=st2.substring(6,7);
                if (autoStatus.equalsIgnoreCase("1"))
                {
                    statusList.add(11);
                }
                else
                {
                    statusList.add(7);
                }
                if (normalStatus.equalsIgnoreCase("1"))
                {
                    statusList.add(6);
                }
                else
                {
                    statusList.add(11);
                }
            }
        }
    }

    /**
     * 单机优先级换算
     * @param status
     * @return
     */
    public int scopeSwitch(int status)
    {
        switch (status)
        {
            case 1://停止
                return 9;

            case 2://运行
                return 10;

            case 3://休眠
                return 8;

            case 4://禁用
                return 1;

            case 5://急停
                return 2;

            case 6://暂停
                return 3;

            case 7://临时停机
                return 4;

            case 8://启动中
                return 10;

            case 9://停止中
                return 9;

            default:
                return 13;

        }
    }

    /**
     * 按钮和光电
     * @param status
     * @return
     */
    public int buttonJT(int status)
    {
        int st=0;
        if (status==0)
        {
            st=9;
        }else if (status==1)
        {
            st=9;
        }else if (status==2)//按下
        {
            st=2;
        }else if (status==3){//松开未复位
            st=3;
        }
        else
        {
            st=11;
        }
        return st;
    }
}
