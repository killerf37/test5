package com.ginfon.core.web.service;

import com.ginfon.core.web.entity.User;

/**
 * @Author: James
 * @Date: 2019/8/14 23:33
 * @Description:
 */
public interface IPasswordService {
	void validate(User user, String password);

	String encryptPassword(String username, String password, String salt);
}
