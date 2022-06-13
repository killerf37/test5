package com.ginfon.scada.service;

import com.ginfon.scada.entity.CurrentVoltage;

import java.util.List;

/**
 * @Author: James
 * @Date: 2020/3/27 13:06
 * @Description:
 */
public interface ICurrentVoltageService {
	
    int saveCurrentVoltage(CurrentVoltage currentVoltage);

    List<CurrentVoltage> selectCurrentVoltage(CurrentVoltage currentVoltage);

    //获取最近24小时数据
    List<CurrentVoltage> selectCurrentVoltageIn24Hours(CurrentVoltage currentVoltage);
}
