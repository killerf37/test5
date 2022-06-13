package com.ginfon.scada.event.mapper;

import java.util.List;

import com.ginfon.scada.entity.ScadaEvent;
import com.ginfon.scada.entity.ScadaLog;

public interface ScadaLogMapper {
    List<ScadaEvent> getScadaLogFifth(ScadaLog log);
    List<ScadaEvent> getScadaLogInfo(ScadaLog log);
}
