package com.ginfon.core.web.service.impl.sys;

import com.ginfon.core.constant.UserConstants;
import com.ginfon.core.exception.UserBlockedException;
import com.ginfon.core.exception.UserNotExistsException;
import com.ginfon.core.exception.UserPasswordNotMatchException;
import com.ginfon.core.web.entity.User;
import com.ginfon.core.web.enums.UserStatus;
import com.ginfon.core.web.service.ILoginService;
import com.ginfon.core.web.service.IPasswordService;
import com.ginfon.core.web.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 登录校验方法
 *
 * @author peter
 */
@Service("loginService")
public class LoginServiceImpl implements ILoginService {
	@Autowired
	private IPasswordService passwordService;

	@Autowired
	private IUserService userService;

	/**
	 * 登录
	 */
	public User login(String username, String password) {
		// 用户名或密码为空 错误
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			throw new UserNotExistsException();
		}
		// 密码如果不在指定范围内 错误
		if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
				|| password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
			throw new UserPasswordNotMatchException();
		}

		// 用户名不在指定范围内 错误
		if (username.length() < UserConstants.USERNAME_MIN_LENGTH
				|| username.length() > UserConstants.USERNAME_MAX_LENGTH) {
			throw new UserPasswordNotMatchException();
		}

		try {
			// 查询用户信息
			User user = userService.selectUserByLoginName(username);

			if (user == null && maybeMobilePhoneNumber(username)) {
				user = userService.selectUserByPhoneNumber(username);
			}

			if (user == null && maybeEmail(username)) {
				user = userService.selectUserByEmail(username);
			}

			if (user == null || UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
				throw new UserNotExistsException();
			}

			passwordService.validate(user, password);

			if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
				throw new UserBlockedException(user.getRemark());
			}
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserPasswordNotMatchException();
		}
	}

	private boolean maybeEmail(String username) {
		if (!username.matches(UserConstants.EMAIL_PATTERN)) {
			return false;
		}
		return true;
	}

	private boolean maybeMobilePhoneNumber(String username) {
		if (!username.matches(UserConstants.MOBILE_PHONE_NUMBER_PATTERN)) {
			return false;
		}
		return true;
	}
}
