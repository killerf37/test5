package com.ginfon.scada.service;

import com.ginfon.scada.entity.MsgToMap;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/11/08/16:08
 * @Description:
 */
public interface IMsgToMapService {
    List<MsgToMap> selectMsgToMapList();
}
