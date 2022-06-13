package com.ginfon.scada.event.mapper;

import com.ginfon.scada.entity.CurrentStatus;

import java.util.List;

public interface SelectStatusMapper {
    List<CurrentStatus> selectcurrentsta();
    List<CurrentStatus> selectallsta(CurrentStatus cs);
}
