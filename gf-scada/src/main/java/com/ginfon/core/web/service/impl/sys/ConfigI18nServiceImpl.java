package com.ginfon.core.web.service.impl.sys;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ginfon.core.web.entity.I18nElement;
import com.ginfon.core.web.mapper.ConfigI18nMapper;
import com.ginfon.core.web.service.IConfigI18nService;

/**
 * 	国际化配置加载
 * @author Mark
 *
 */
@Service
public class ConfigI18nServiceImpl implements IConfigI18nService {
	
	@Autowired
	private ConfigI18nMapper configI18nMapper;

	@Override
	public List<I18nElement> selectAll() {
		return this.configI18nMapper.selectAll();
	}
	
}
