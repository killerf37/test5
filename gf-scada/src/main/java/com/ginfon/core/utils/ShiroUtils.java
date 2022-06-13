package com.ginfon.core.utils;

import com.ginfon.core.web.entity.User;
import com.ginfon.core.web.shiro.relam.LogisticsGpShiroRealm;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;

import java.lang.reflect.InvocationTargetException;

/**
 * shiro 工具类
 *
 * @author ruoyi
 */
public class ShiroUtils {

	public static Subject getSubjct() {
		return SecurityUtils.getSubject();
	}

	public static Session getSession() {
		return SecurityUtils.getSubject().getSession();
	}

	public static void logout() {
		getSubjct().logout();
	}

	public static User getUser() {
		User user = null;
		Object obj = getSubjct().getPrincipal();
		if (StringUtils.isNotNull(obj)) {
			user = new User();
			try {
				BeanUtils.copyProperties(user, obj);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	public static void setUser(User user) {
		Subject subject = getSubjct();
		PrincipalCollection principalCollection = subject.getPrincipals();
		String realmName = principalCollection.getRealmNames().iterator().next();
		PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(user, realmName);
		// 重新加载Principal
		subject.runAs(newPrincipalCollection);
	}

	public static void clearCachedAuthorizationInfo() {
		RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
		LogisticsGpShiroRealm realm = (LogisticsGpShiroRealm) rsm.getRealms().iterator().next();
		realm.clearCachedAuthorizationInfo();
	}

	public static Long getUserId() {
		return getUser().getUserId().longValue();
	}

	public static String getLoginName() {
		return getUser().getLoginName();
	}

	public static String getIp() {
		return getSubjct().getSession().getHost();
	}

	public static String getSessionId() {
		return String.valueOf(getSubjct().getSession().getId());
	}
}
