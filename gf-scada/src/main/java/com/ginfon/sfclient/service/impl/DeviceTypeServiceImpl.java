package com.ginfon.sfclient.service.impl;

import com.ginfon.sfclient.entity.DeviceType;
import com.ginfon.sfclient.mapper.DeviceTypeMapper;
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
public class DeviceTypeServiceImpl implements IDeviceTypeService {

	@Autowired
	private DeviceTypeMapper deviceTypeMapper;

	@Override
	public List<DeviceType> selectDeviceTypeList() {
		return deviceTypeMapper.selectDeviceTypeList();
	}
}
