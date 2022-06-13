package com.ginfon.scada.event.mapper;

import java.util.List;

import com.ginfon.scada.entity.ConveyorLine;

/**
 * 	
 * @author Mark
 *
 */
public interface ConveyorLineMapper {
	
	List<ConveyorLine> selectConveyorLine();

	List<ConveyorLine> selectBaiLunLine();
	
}
