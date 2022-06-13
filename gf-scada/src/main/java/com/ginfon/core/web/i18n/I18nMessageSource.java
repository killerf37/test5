package com.ginfon.core.web.i18n;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.ginfon.core.web.entity.I18nElement;
import com.ginfon.core.web.service.impl.sys.ConfigI18nServiceImpl;

/**
 * 	I18n参数读取配置。
 * @author Mark
 *
 */
@Service("messageSource")
public class I18nMessageSource extends AbstractMessageSource implements ResourceLoaderAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(I18nMessageSource.class);
	
	// 这个是用来缓存数据库中获取到的配置的 数据库配置更改的时候可以调用reload方法重新加载
	private static final Map<String, Map<String, String>> LOCAL_CACHE = new ConcurrentHashMap<>(256);
	
	/**
	 * 	这个其实没啥用，但是得留着对吧。
	 */
	@SuppressWarnings("unused")
	private ResourceLoader resourceLoader;
	
	@Autowired
	private ConfigI18nServiceImpl configI18nServiceImpl;
	
	@Autowired
	private HttpServletRequest request;
	
	public I18nMessageSource() {
		
	}
	
	
	@PostConstruct
	public void init() {
		this.reload();
	}
	
    /**
     * 重新将数据库中的国际化配置加载
     */
	public void reload() {
		LOCAL_CACHE.clear();
		this.loadAllMessageResourcesFromDB();
	}
	
	/**
	 * 	从数据库加载所有的配置信息。
	 */
	private void loadAllMessageResourcesFromDB(){
		List<I18nElement> list = this.configI18nServiceImpl.selectAll();

		final Map<String, String> zhCnMessageResources = new HashMap<>(list.size());
		final Map<String, String> enUsMessageResources = new HashMap<>(list.size());
		final Map<String, String> thUsMessageResources = new HashMap<>(list.size());

		for (I18nElement e : list) {
			// 分配中文
			zhCnMessageResources.put(e.getLocalKey(), e.getLocalZh());
			// 分配英文
			enUsMessageResources.put(e.getLocalKey(), e.getLocalEn());
			// 分配泰文
			thUsMessageResources.put(e.getLocalKey(), e.getLocalTh());
		}

		// 加入缓存
		LOCAL_CACHE.put("zh", zhCnMessageResources);
		LOCAL_CACHE.put("en", enUsMessageResources);
		LOCAL_CACHE.put("th", thUsMessageResources);
	}
	
	/**
	 * 	按照键值查找对应的文本描述。
	 * @param code
	 * @param locale
	 * @return
	 */
	public String getSourceFromCache(String code, Locale locale) {
		String language = locale == null ? RequestContextUtils.getLocale(this.request).getLanguage() : locale.getLanguage();
		Map<String, String> props = LOCAL_CACHE.get(language);
		if (null != props && props.containsKey(code)) {
			return props.get(code);
		}else {
			// 如果对应语言中不能匹配到数据项，从上级获取返回
			try {
				if (null != this.getParentMessageSource()) {
					return this.getParentMessageSource().getMessage(code, null, locale);
				}
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			// 如果上级也没有找到，那么返回请求键值
			return code;
		}
	}
	
	
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		String msg = this.getSourceFromCache(code, locale);
		MessageFormat messageFormat = new MessageFormat(msg, locale);
		return messageFormat;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = (resourceLoader == null ? new DefaultResourceLoader() : resourceLoader);
	}
	
	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		return this.getSourceFromCache(code, locale);
	}
}
