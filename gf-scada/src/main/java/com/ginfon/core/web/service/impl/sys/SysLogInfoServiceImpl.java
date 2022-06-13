package com.ginfon.core.web.service.impl.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginfon.core.web.entity.SysLog;
import com.ginfon.core.web.mapper.SysLogInfoMapper;
import com.ginfon.core.web.service.ISysLogInfoService;

import java.util.List;

@Service("logInfoService")
public class SysLogInfoServiceImpl implements ISysLogInfoService {
	@Autowired
	private SysLogInfoMapper sysLogInfoMapper;

	@Override
	public int insertSysLog(SysLog sysLog) {
		return sysLogInfoMapper.insertSysLog(sysLog);
	}

	@Override
	public List<SysLog> sysLogQuery(SysLog sysLog) {
		return sysLogInfoMapper.sysLogQuery(sysLog);
	}

}
