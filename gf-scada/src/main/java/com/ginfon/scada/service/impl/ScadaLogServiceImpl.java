package com.ginfon.scada.service.impl;

import com.ginfon.scada.entity.ScadaEvent;
import com.ginfon.scada.entity.ScadaLog;
import com.ginfon.scada.event.mapper.ScadaLogMapper;
import com.ginfon.scada.service.IScadaLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScadaLogServiceImpl implements IScadaLogService {
    @Autowired
    private ScadaLogMapper scadaLogMapper;

    @Override
    public List<ScadaEvent> getScadaLogFifth(ScadaLog log) {
        return this.scadaLogMapper.getScadaLogFifth(log);
    }

    @Override
    public List<ScadaEvent> getScadaLogInfo(ScadaLog log) {
        return this.scadaLogMapper.getScadaLogInfo(log);
    }
}
