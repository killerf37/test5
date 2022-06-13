package com.ginfon.scada.event.mapper;

import com.ginfon.scada.entity.DeviceFaultDTO;
import com.ginfon.scada.entity.ScadaLog;
import com.ginfon.scada.entity.ScadaStatusDTO;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/12/17/14:18
 * @Description:
 */
public interface FaultLogMapper {
    List<DeviceFaultDTO> selctFault(ScadaStatusDTO deviceFaultDTO);
    List<DeviceFaultDTO> selectMoreFault(ScadaLog log);
    List<DeviceFaultDTO> selectLineFault(Integer lineNo);
    List<DeviceFaultDTO> selctTopFault();
    void delFault(ScadaStatusDTO deviceFaultDTO);
    void updateFault(BigInteger id);
    void insertFault(DeviceFaultDTO deviceFaultDTO);
}
