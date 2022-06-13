package com.ginfon.scada.service.impl;

import com.ginfon.scada.entity.CurrentStatus;
import com.ginfon.scada.entity.CurrentStatusEvent;
import com.ginfon.scada.event.mapper.ConveyorLineCurrentStatusMapper;
import com.ginfon.scada.service.ICurrentStatusServce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrentStatusImpl implements ICurrentStatusServce {
    @Autowired
    private ConveyorLineCurrentStatusMapper conveyorLineCurrentStatusMapper;

    @Override
    public void insertstatus(CurrentStatus log) {
        this.conveyorLineCurrentStatusMapper.insertstatus(log);
    }

    @Override
    public void updatestatus(CurrentStatus log) {
        this.conveyorLineCurrentStatusMapper.updatestatus(log);
    }

    @Override
    public void updateendtime(CurrentStatus cs) {
        this.conveyorLineCurrentStatusMapper.updateendtime(cs);
    }

    @Override
    public List<CurrentStatus> selectstatus(CurrentStatusEvent cse) {
        return this.conveyorLineCurrentStatusMapper.selectstatus(cse);
    }
}