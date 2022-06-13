package com.ginfon.sfclient.service.impl;

import com.ginfon.sfclient.entity.DeviceStatus;
import com.ginfon.sfclient.entity.DeviceType;
import com.ginfon.sfclient.mapper.DeviceStatusMapper;
import com.ginfon.sfclient.mapper.DeviceTypeMapper;
import com.ginfon.sfclient.service.IDeviceStatusService;
import com.ginfon.sfclient.service.IDeviceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: curtain
 * @create: 2021-07-13 14:55
 **/
@Service
public class DeviceStatusServiceImpl implements IDeviceStatusService {

	@Autowired
	private DeviceStatusMapper deviceStatusMapper;

	@Override
	public List<DeviceStatus> selectDeviceStatusList() {
		return deviceStatusMapper.selectDeviceStatusList();
	}
}
