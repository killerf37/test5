package com.ginfon.scada.event.mapper;


import java.util.List;

import com.ginfon.scada.entity.CurrentVoltage;


/**
 * @Author: James
 * @Date: 2020/3/27 13:12
 * @Description:
 */
public interface CurrentVoltageMapper {
	
    int saveCurrentVoltage(CurrentVoltage currentVoltage);

    List<CurrentVoltage> selectCurrentVoltage(CurrentVoltage currentVoltage);

    //获取最近24小时数据
    List<CurrentVoltage> selectCurrentVoltageIn24Hours(CurrentVoltage currentVoltage);
}
