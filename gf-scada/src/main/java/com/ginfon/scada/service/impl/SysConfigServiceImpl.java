package com.ginfon.scada.service.impl;

import com.ginfon.scada.entity.SysConfig;
import com.ginfon.scada.event.mapper.SysConfigMapper;
import com.ginfon.scada.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("sysConfigService")
public class SysConfigServiceImpl implements ISysConfigService {
    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public String getValueByParmId(String paramId) {
        SysConfig sysConfig = sysConfigMapper.selectValueByParamId(paramId);

        if (sysConfig != null) {
            return sysConfig.getParamValue();
        } else {
            return "";
        }
    }
}
