package com.ginfon.core.web.service.impl.sys;

import com.ginfon.core.exception.UserPasswordNotMatchException;
import com.ginfon.core.web.entity.User;
import com.ginfon.core.web.service.IPasswordService;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Service;

/**
 * 登录密码方法
 *
 * @author peter
 */
@Service("passwordService")
public class PasswordServiceImpl implements IPasswordService {
	public void validate(User user, String password) {
		if (!matches(user, password)) {
			throw new UserPasswordNotMatchException();
		}
	}

	public boolean matches(User user, String password) {
		String encrypt = encryptPassword(user.getLoginName(), password, user.getSalt());
		return user.getPassword().equals(encrypt);
	}

	public String encryptPassword(String username, String password, String salt) {
		return new Md5Hash(username + password + salt).toHex().toString();
	}
}
