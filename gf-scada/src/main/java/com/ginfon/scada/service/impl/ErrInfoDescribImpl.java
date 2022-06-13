package com.ginfon.scada.service.impl;

import com.ginfon.scada.entity.ErrDTO;
import com.ginfon.scada.event.mapper.ErrInfoDescribMapper;
import com.ginfon.scada.service.IErrInfoDescribService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/12/23/13:33
 * @Description:
 */
@Service
public class ErrInfoDescribImpl implements IErrInfoDescribService {

    @Autowired
    private ErrInfoDescribMapper errInfoDescribMapper;

    @Override
    public List<ErrDTO> selectErrInfo(Integer type) {
        return errInfoDescribMapper.selectErrInfo(type);
    }
}
