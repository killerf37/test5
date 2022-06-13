package com.ginfon.core.enums;

/**
 * @Author: James
 * @Date: 2018/6/20 09:58
 * @Description:
 */
public enum UserAndRloleType {
	ADMIN("系统管理员"), SYSUSER("系统用户");

	UserAndRloleType(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}
}