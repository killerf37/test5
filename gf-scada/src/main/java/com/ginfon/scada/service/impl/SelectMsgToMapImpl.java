package com.ginfon.scada.service.impl;

import com.ginfon.scada.entity.MsgToMap;
import com.ginfon.scada.event.mapper.MsgToMapMapper;
import com.ginfon.scada.service.IMsgToMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/11/08/16:18
 * @Description:
 */
@Service
public class SelectMsgToMapImpl implements IMsgToMapService {

    @Autowired
    MsgToMapMapper msgToMapMapper;

    @Override
    public List<MsgToMap> selectMsgToMapList() {
        return this.msgToMapMapper.selectMsgToMapList();
    }
}
