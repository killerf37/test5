package com.ginfon.core.web.service.impl.sys;

import com.ginfon.core.web.entity.DictData;
import com.ginfon.core.web.service.IDictDataService;
import com.ginfon.core.web.service.IDictService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: James
 * @Date: 2019/8/16 09:07
 * @Description:
 */
@Service("dictService")
public class DictServiceImpl implements IDictService {
	@Autowired
	private IDictDataService dictDataService;

	/**
	 * 根据字典类型查询字典数据信息
	 *
	 * @param dictType 字典类型
	 * @return 参数键值
	 */
	public List<DictData> getType(String dictType) {
		return dictDataService.selectDictDataByType(dictType);
	}

	/**
	 * 根据字典类型和字典键值查询字典数据信息
	 *
	 * @param dictType  字典类型
	 * @param dictValue 字典键值
	 * @return 字典标签
	 */
	public String getLabel(String dictType, String dictValue) {
		return dictDataService.selectDictLabel(dictType, dictValue);
	}
}
