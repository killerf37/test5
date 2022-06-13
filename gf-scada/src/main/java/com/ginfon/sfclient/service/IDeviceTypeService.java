package com.ginfon.sfclient.service;

import com.ginfon.sfclient.entity.DeviceType;

import java.util.List;

public interface IDeviceTypeService {
	/**
	 * 查询m_devicetype表中所有数据
	 * @return
	 */
	List<DeviceType> selectDeviceTypeList();
}
