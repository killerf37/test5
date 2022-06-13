package com.ginfon.scada.entity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/12/17/16:53
 * @Description:
 */
public class StatusAndErrResult {

    private List<ScadaStatusDTO> insertList;
    private HashMap<BigInteger,Integer> updateList;
    public StatusAndErrResult()
    {
        insertList=new ArrayList<>();
        updateList=new HashMap<>();
    }

    public void addInsertFaultDTO(ScadaStatusDTO deviceFaultDTO)
    {
        insertList.add(deviceFaultDTO);
    }

    public void removeInsertFaultDTO(DeviceFaultDTO deviceFaultDTO)
    {
        if (insertList.contains(deviceFaultDTO))
        {
            insertList.remove(deviceFaultDTO);
        }
    }

    public void addUpdateFaultDTO(BigInteger key,Integer value)
    {
        updateList.put(key,value);
    }

    public void removeUpdateFaultDTO(BigInteger key)
    {
        if (updateList.containsKey(key))
        {
            updateList.remove(key);
        }
    }

    public List<ScadaStatusDTO> getInsertList() {
        return insertList;
    }

    public HashMap<BigInteger, Integer> getUpdateList() {
        return updateList;
    }

}
