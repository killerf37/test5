package com.ginfon.scada.event.mapper;

import com.ginfon.scada.entity.CurrentStatus;
import com.ginfon.scada.entity.CurrentStatusEvent;

import java.util.List;

public interface ConveyorLineCurrentStatusMapper {
    void insertstatus(CurrentStatus cs);
    void updatestatus(CurrentStatus cs);
    void updateendtime(CurrentStatus cs);
    List<CurrentStatus> selectstatus(CurrentStatusEvent cse);
}
