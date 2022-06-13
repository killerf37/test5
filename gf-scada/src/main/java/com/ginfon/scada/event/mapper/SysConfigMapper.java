package com.ginfon.scada.event.mapper;

import com.ginfon.scada.entity.SysConfig;

public interface SysConfigMapper {
    SysConfig selectValueByParamId(String paramId);
}
