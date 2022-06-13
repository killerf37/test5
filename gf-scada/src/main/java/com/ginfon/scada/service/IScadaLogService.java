package com.ginfon.scada.service;

import java.util.List;

import com.ginfon.scada.entity.ScadaEvent;
import com.ginfon.scada.entity.ScadaLog;

public interface IScadaLogService {
    List<ScadaEvent> getScadaLogFifth(ScadaLog log);
    List<ScadaEvent> getScadaLogInfo(ScadaLog log);
}
