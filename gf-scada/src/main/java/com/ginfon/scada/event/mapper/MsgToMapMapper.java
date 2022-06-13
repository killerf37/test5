package com.ginfon.scada.event.mapper;

import com.ginfon.scada.entity.MsgToMap;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/11/08/16:24
 * @Description:
 */
public interface MsgToMapMapper {
    List<MsgToMap> selectMsgToMapList();
}
