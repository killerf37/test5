package com.ginfon.sfclient.mapper;

import com.ginfon.sfclient.entity.DeviceType;

import java.util.List;

/**
 * @description:
 * @author: curtain
 * @create: 2021-07-13 14:52
 **/

public interface DeviceTypeMapper {
	/**
	 * 查询m_devicetype表中所有数据
	 * @return
	 */
	List<DeviceType> selectDeviceTypeList();
}
