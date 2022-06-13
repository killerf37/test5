package com.ginfon.scada.gateway.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: James
 * @Date: 2019/7/15 11:16
 * @Description:spring工具类 方便在非spring管理环境中获取bean
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
	/**
	 * spring容器上下文对象
	 */
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtils.applicationContext = applicationContext;
	}

	/**
	 * 获取上下文对象实例
	 *
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 通过bean名称获取实例
	 * <p>
	 * 该bean一般添加@Component注解就行了
	 *
	 * @param name bean名称
	 * @return 实例对象
	 */
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}

	/**
	 * 通过类class获取实例
	 * <p>
	 * 该bean一般添加@Component注解就行了
	 *
	 * @param clazz
	 * @return 实例对象
	 */
	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}

	/**
	 * 通过类name,class获取指定的实例
	 * <p>
	 * 该bean一般添加@Component注解就行了
	 *
	 * @param name
	 * @param clazz
	 * @return 实例对象
	 */
	public static <T> T getBean(String name, Class<T> clazz) {
		return getApplicationContext().getBean(name, clazz);
	}
}