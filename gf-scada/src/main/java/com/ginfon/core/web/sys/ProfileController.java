package com.ginfon.core.web.sys;

import com.ginfon.core.utils.AjaxResult;
import com.ginfon.core.utils.FileUploadUtils;
import com.ginfon.core.utils.ShiroUtils;
import com.ginfon.core.web.BaseController;
import com.ginfon.core.web.entity.User;
import com.ginfon.core.web.service.IDictService;
import com.ginfon.core.web.service.IUserService;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 个人信息 业务处理
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/user/profile")
public class ProfileController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

	private String prefix = "user/profile";

	@Autowired
	private IUserService userService;

	@Autowired
	private IDictService dictService;

	/**
	 * 个人信息
	 */
	@GetMapping()
	public String profile(ModelMap mmap) {
		User user = ShiroUtils.getUser();
		user.setSex(dictService.getLabel("sys_user_sex", user.getSex()));
		mmap.put("user", user);
		mmap.put("roleGroup", userService.selectUserRoleGroup(user.getUserId()));
		mmap.put("postGroup", userService.selectUserPostGroup(user.getUserId()));
		return prefix + "/profile";
	}

	@GetMapping("/checkPassword")
	@ResponseBody
	public boolean checkPassword(String password) {
		User user = ShiroUtils.getUser();
		String encrypt = new Md5Hash(user.getLoginName() + password + user.getSalt()).toHex().toString();
		if (user.getPassword().equals(encrypt)) {
			return true;
		}
		return false;
	}

	@GetMapping("/resetPwd/{userId}")
	public String resetPwd(@PathVariable("userId") Long userId, ModelMap mmap) {
		mmap.put("user", userService.selectUserById(userId));
		return prefix + "/setpwd";
	}

	@PostMapping("/resetPwd")
	@ResponseBody
	public AjaxResult resetPwd(User user) {
		int rows = userService.resetUserPwd(user);
		if (rows > 0) {
			setUser(userService.selectUserById(user.getUserId()));
			return success();
		}
		return error();
	}

	/**
	 * 修改用户
	 */
	@GetMapping("/edit/{userId}")
	public String edit(@PathVariable("userId") Long userId, ModelMap mmap) {
		mmap.put("user", userService.selectUserById(userId));
		return prefix + "/edit";
	}

	/**
	 * 修改头像
	 */
	@GetMapping("/avatar/{userId}")
	public String avatar(@PathVariable("userId") Long userId, ModelMap mmap) {
		mmap.put("user", userService.selectUserById(userId));
		return prefix + "/avatar";
	}

	/**
	 * 修改用户
	 */
	@PostMapping("/update")
	@ResponseBody
	public AjaxResult update(User user) {
		if (userService.updateUserInfo(user) > 0) {
			setUser(userService.selectUserById(user.getUserId()));
			return success();
		}
		return error();
	}

	/**
	 * 保存头像
	 */
	@PostMapping("/updateAvatar")
	@ResponseBody
	public AjaxResult updateAvatar(User user, @RequestParam("avatarfile") MultipartFile file) {
		try {
			if (!file.isEmpty()) {
				String avatar = FileUploadUtils.upload(file);
				user.setAvatar(avatar);
				if (userService.updateUserInfo(user) > 0) {
					setUser(userService.selectUserById(user.getUserId()));
					return success();
				}
			}
			return error();
		} catch (Exception e) {
			log.error("修改头像失败！", e);
			return error(e.getMessage());
		}
	}

	public void setUser(User user) {
		ShiroUtils.setUser(user);
	}

}
