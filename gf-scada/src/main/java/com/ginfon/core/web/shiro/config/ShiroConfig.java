package com.ginfon.core.web.shiro.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ginfon.core.web.shiro.relam.LogisticsGpShiroRealm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author James
 */
@Configuration
public class ShiroConfig {
	public static final String PREMISSION_STRING = "perms[\"{0}\"]";

	// Session超时时间，单位为毫秒（默认30分钟）
	@Value("${shiro.session.expireTime}")
	private int expireTime;

	// 相隔多久检查一次session的有效性，单位毫秒，默认就是10分钟
	@Value("${shiro.session.validationInterval}")
	private int validationInterval;

	// 验证码开关
	@Value("${shiro.user.captchaEnabled}")
	private boolean captchaEnabled;

	// 验证码类型
	@Value("${shiro.user.captchaType}")
	private String captchaType;

	// 设置Cookie的域名
	@Value("${shiro.cookie.domain}")
	private String domain;

	// 设置cookie的有效访问路径
	@Value("${shiro.cookie.path}")
	private String path;

	// 设置HttpOnly属性
	@Value("${shiro.cookie.httpOnly}")
	private boolean httpOnly;

	// 设置Cookie的过期时间，秒为单位
	@Value("${shiro.cookie.maxAge}")
	private int maxAge;

	// 登录地址
	@Value("${shiro.user.loginUrl}")
	private String loginUrl;

	// 权限认证失败地址
	@Value("${shiro.user.unauthorizedUrl}")
	private String unauthorizedUrl;

	/**
	 * 	自定义Realm
	 */
	@Bean
	public LogisticsGpShiroRealm logisticsGpShiroRealm() {
		LogisticsGpShiroRealm logisticsGpShiroRealm = new LogisticsGpShiroRealm();
		return logisticsGpShiroRealm;
	}

	/**
	 * 	安全管理器
	 */
	@Bean
	public SecurityManager securityManager(LogisticsGpShiroRealm logisticsGpShiroRealm) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置realm.
		securityManager.setRealm(logisticsGpShiroRealm);
		return securityManager;
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
		filterChainDefinitionMap.put("/static/**", "anon");
		filterChainDefinitionMap.put("/webjars/**", "anon");
		filterChainDefinitionMap.put("/js/**", "anon");
		filterChainDefinitionMap.put("/i18n/**", "anon");
		filterChainDefinitionMap.put("/css/**", "anon");
		filterChainDefinitionMap.put("/fonts/**", "anon");
		filterChainDefinitionMap.put("/ruoyi/**", "anon");
		filterChainDefinitionMap.put("/ajax/**", "anon");
		filterChainDefinitionMap.put("/images/**", "anon");
		filterChainDefinitionMap.put("/login", "anon");
		filterChainDefinitionMap.put("/services", "anon");
		filterChainDefinitionMap.put("/logout", "logout");
		filterChainDefinitionMap.put("/**", "authc");
		shiroFilterFactoryBean.setLoginUrl("/login");
		shiroFilterFactoryBean.setSuccessUrl("/index");
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	/**
	 * thymeleaf模板引擎和shiro框架的整合
	 */
	@Bean
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}

	/**
	 * 开启Shiro注解通知器
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
			@Qualifier("securityManager") SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}
}