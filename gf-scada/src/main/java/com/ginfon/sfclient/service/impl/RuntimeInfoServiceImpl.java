package com.ginfon.sfclient.service.impl;

import com.ginfon.sfclient.entity.RuntimeInfo;
import com.ginfon.sfclient.mapper.DeviceStatusMapper;
import com.ginfon.sfclient.mapper.RuntimeInfoMapper;
import com.ginfon.sfclient.service.IRuntimeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: curtain
 * @create: 2021-07-23 11:20
 **/
@Service
public class RuntimeInfoServiceImpl implements IRuntimeInfoService {

	@Autowired
	private RuntimeInfoMapper runtimeInfoMapper;
	@Override
	public List<RuntimeInfo> selectRuntimeInfoList() {
		return runtimeInfoMapper.selectRuntimeInfoList();
	}
}
