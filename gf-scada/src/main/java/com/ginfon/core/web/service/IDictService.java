package com.ginfon.core.web.service;

import java.util.List;

import com.ginfon.core.web.entity.DictData;

/**
 * @Author: James
 * @Date: 2019/8/16 09:05
 * @Description:
 */
public interface IDictService {
    List<DictData> getType(String dictType);

    String getLabel(String dictType, String dictValue);
}
