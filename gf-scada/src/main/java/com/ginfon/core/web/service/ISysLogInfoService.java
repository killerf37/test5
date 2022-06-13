package com.ginfon.core.web.service;


import java.util.List;

import com.ginfon.core.web.entity.SysLog;

public interface ISysLogInfoService {

	int insertSysLog(SysLog sysLog);

	public List<SysLog> sysLogQuery(SysLog sysLog);
}
