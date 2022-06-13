package com.ginfon.sfclient.mapper;

import com.ginfon.sfclient.entity.DeviceStatus;
import com.ginfon.sfclient.entity.DeviceType;

import java.util.List;

/**
 * @description:
 * @author: curtain
 * @create: 2021-07-13 14:52
 **/

public interface DeviceStatusMapper {
	/**
	 * 查询m_devicestatus表中所有数据
	 * @return
	 */
	List<DeviceStatus> selectDeviceStatusList();
}
