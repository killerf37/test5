package com.ginfon.scada.event.mapper;

import com.ginfon.scada.entity.ErrDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/12/23/13:10
 * @Description:
 */
public interface ErrInfoDescribMapper {
    List<ErrDTO> selectErrInfo(Integer type);
}
