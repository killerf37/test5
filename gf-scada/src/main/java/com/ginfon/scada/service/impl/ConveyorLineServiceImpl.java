package com.ginfon.scada.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginfon.scada.entity.ConveyorLine;
import com.ginfon.scada.event.mapper.ConveyorLineMapper;
import com.ginfon.scada.service.IConveyorLineService;


@Service
public class ConveyorLineServiceImpl implements IConveyorLineService {

	@Autowired
	private ConveyorLineMapper conveyorLineMapper;
	
	@Override
	public List<ConveyorLine> selectConveyorLine() {
		return this.conveyorLineMapper.selectConveyorLine();
	}

	@Override
	public List<ConveyorLine> selectBaiLunLine()
	{
		return this.conveyorLineMapper.selectBaiLunLine();
	}

}
