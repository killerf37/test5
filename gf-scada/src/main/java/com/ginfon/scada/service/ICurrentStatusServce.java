package com.ginfon.scada.service;

import com.ginfon.scada.entity.CurrentStatus;
import com.ginfon.scada.entity.CurrentStatusEvent;

import java.util.List;

public interface ICurrentStatusServce {
    void insertstatus(CurrentStatus log);
    void updatestatus(CurrentStatus log);
    void updateendtime(CurrentStatus cs);
    List<CurrentStatus> selectstatus(CurrentStatusEvent cse);
}
