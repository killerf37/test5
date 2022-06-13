package com.ginfon.core.utils;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.ginfon.core.web.i18n.I18nMessageSource;

/**
 * 获取i18n资源文件
 *
 * @author peter
 */
@Component("messageUtils")
public class MessageUtils {

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return
     */
    public static String message(String code, Object... args) {
    	I18nMessageSource messageSource = SpringUtils.getBean(I18nMessageSource.class);
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
