package com.ginfon.sfclient.service;

import com.ginfon.sfclient.entity.DeviceStatus;
import com.ginfon.sfclient.entity.DeviceType;

import java.util.List;

public interface IDeviceStatusService {
	/**
	 * 查询m_devicestatus表中所有数据
	 * @return
	 */
	List<DeviceStatus> selectDeviceStatusList();
}
