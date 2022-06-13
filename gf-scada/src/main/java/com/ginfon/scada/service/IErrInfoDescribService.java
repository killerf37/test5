package com.ginfon.scada.service;

import com.ginfon.scada.entity.ErrDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/12/23/13:32
 * @Description:
 */
public interface IErrInfoDescribService {
    List<ErrDTO> selectErrInfo(Integer type);
}
