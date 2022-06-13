/**
 * 异常信息转换
 * @param r
 */
function convertFault(r,lv) {
    console.log(r);
    if (lv==1)//线号
    {
        if (r.lineName==null||r.lineName==undefined)
        {
            return r.lineNo;
        }else
        {
            return  r.lineName;
        }
    }else if (lv==2)//设备类型
    {
        if (r.typeDescrib==null||r.typeDescrib==undefined)
        {
            return r.deviceType;
        }else
        {
            return r.typeDescrib;
        }

    }else if (lv==3)//异常信息
    {
        if (r.faultDescrib==null||r.faultDescrib==undefined)
        {
            return r.fault;
        }else
        {
            return r.faultDescrib;
        }

    }else if (lv==4)
    {
        if(r.devicePhysicalNo==null||r.devicePhysicalNo==undefined)
        {
            return r.deviceNo;
        }else
        {
            return r.devicePhysicalNo;
        }
    }else
    {

    }
}