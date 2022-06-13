package com.ginfon.core.utils;

import java.util.regex.Pattern;

/**
 * @author sqp123
 * @date 2018/6/27 10:29 账户相关属性验证工具
 */
public class UserUtil {
	public static final String REGEX_ACCOUNT = "^[a-zA-Z]{1}([a-zA-Z0-9]|[._]){5,17}$";

	public static final String REGEX_PASSWORD = "^([\\w\\.\\_]|[\\u4e00-\\u9fa5]){6,15}$";

	public static final String REGEX_USERNAME = "^(\\w){6,18}$";

	public static final String REGEX_MOBILE = "^1[3|4|5|8][0-9]\\d{4,8}$";

	/**
	 * 	校验登录账号
	 * 
	 * @param account
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isAccount(String account) {
		return Pattern.matches(REGEX_ACCOUNT, account);
	}

	/**
	 * 	校验密码
	 * 
	 * @param password
	 * @return
	 */
	public static boolean isPassword(String password) {
		return Pattern.matches(REGEX_PASSWORD, password);
	}

	/**
	 * 	校验用户名称
	 * 
	 * @param username
	 * @return
	 */
	public static boolean isUsername(String username) {
		return Pattern.matches(REGEX_USERNAME, username);
	}

	/**
	 * 	校验手机号码
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile) {
		return Pattern.matches(REGEX_MOBILE, mobile);
	}
}
