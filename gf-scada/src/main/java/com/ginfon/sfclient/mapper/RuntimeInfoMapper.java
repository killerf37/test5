package com.ginfon.sfclient.mapper;

import com.ginfon.sfclient.entity.RuntimeInfo;

import java.util.List;

public interface RuntimeInfoMapper {

	/**
	 * 查询m_runtimedata表中所有数据
	 * @return
	 */
	List<RuntimeInfo> selectRuntimeInfoList();
}
