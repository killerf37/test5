package com.ginfon.scada.service;

import java.util.List;

import com.ginfon.scada.entity.ConveyorLine;

public interface IConveyorLineService {

	List<ConveyorLine> selectConveyorLine();

	List<ConveyorLine> selectBaiLunLine();
	
}
