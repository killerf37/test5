package com.ginfon.scada.service.impl;

import com.ginfon.scada.entity.CurrentVoltage;
import com.ginfon.scada.event.mapper.CurrentVoltageMapper;
import com.ginfon.scada.service.ICurrentVoltageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: James
 * @Date: 2020/3/27 13:11
 * @Description:
 */
@Service
public class CurrentVoltageServiceImpl implements ICurrentVoltageService {
	
    @Autowired
    CurrentVoltageMapper currentVoltageMapper;

    @Override
    public int saveCurrentVoltage(CurrentVoltage currentVoltage) {
        return currentVoltageMapper.saveCurrentVoltage(currentVoltage);
    }

    @Override
    public List<CurrentVoltage> selectCurrentVoltage(CurrentVoltage currentVoltage) {
        return currentVoltageMapper.selectCurrentVoltage(currentVoltage);
    }

    @Override
    public List<CurrentVoltage> selectCurrentVoltageIn24Hours(CurrentVoltage currentVoltage) {
        return currentVoltageMapper.selectCurrentVoltageIn24Hours(currentVoltage);
    }
}
