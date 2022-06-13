package com.ginfon.scada.service.impl;

import com.ginfon.scada.entity.DeviceFaultDTO;
import com.ginfon.scada.entity.ScadaLog;
import com.ginfon.scada.entity.ScadaStatusDTO;
import com.ginfon.scada.event.mapper.FaultLogMapper;
import com.ginfon.scada.service.IFaultLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/12/17/14:28
 * @Description:
 */
@Service
public class FaultLogServiceImpl implements IFaultLogService {

    @Autowired
    private FaultLogMapper faultLogMapper;

    @Override
    public List<DeviceFaultDTO> selctFault(ScadaStatusDTO deviceFaultDTO) {
        return faultLogMapper.selctFault(deviceFaultDTO);
    }

    @Override
    public void delFault(ScadaStatusDTO deviceFaultDTO) {
        faultLogMapper.delFault(deviceFaultDTO);
    }

    @Override
    public void updateFault(BigInteger id) {
        faultLogMapper.updateFault(id);
    }

    @Override
    public void insertFault(DeviceFaultDTO deviceFaultDTO) {
        faultLogMapper.insertFault(deviceFaultDTO);
    }

    @Override
    public List<DeviceFaultDTO> selctTopFault() {
        return faultLogMapper.selctTopFault();
    }

    @Override
    public List<DeviceFaultDTO> selectLineFault(Integer lineNo){
        return faultLogMapper.selectLineFault(lineNo);
    }

    @Override
    public List<DeviceFaultDTO> selectMoreFault(ScadaLog log) {
        return faultLogMapper.selectMoreFault(log);
    }
}
