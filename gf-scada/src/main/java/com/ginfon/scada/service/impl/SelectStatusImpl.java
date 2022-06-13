package com.ginfon.scada.service.impl;

import com.ginfon.scada.entity.CurrentStatus;
import com.ginfon.scada.event.mapper.SelectStatusMapper;
import com.ginfon.scada.service.ISelectCurrentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelectStatusImpl implements ISelectCurrentStatus {
    @Autowired
    private SelectStatusMapper selectStatusMapper;

    @Override
    public List<CurrentStatus> selectcurrentsta() {
        return selectStatusMapper.selectcurrentsta();
    }

    @Override
    public List<CurrentStatus> selectallsta(CurrentStatus cs) {
        return selectStatusMapper.selectallsta(cs);
    }
}
