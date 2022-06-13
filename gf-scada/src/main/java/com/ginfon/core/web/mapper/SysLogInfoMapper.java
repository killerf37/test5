package com.ginfon.core.web.mapper;

import java.util.List;
import com.ginfon.core.web.entity.SysLog;

public interface SysLogInfoMapper {
	
    int insertSysLog(SysLog sysLog);
    
    public List<SysLog> sysLogQuery(SysLog sysLog);
}
