
package com.ginfon.core.web.sys;

import com.ginfon.core.utils.AjaxResult;
import com.ginfon.core.utils.ExcelUtil;
import com.ginfon.core.utils.StringUtils;
import com.ginfon.core.web.BaseController;
import com.ginfon.core.web.entity.User;
import com.ginfon.core.web.page.TableDataInfo;
import com.ginfon.core.web.service.IPostService;
import com.ginfon.core.web.service.IRoleService;
import com.ginfon.core.web.service.IUserService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: James
 * @Date: 2019/8/15 23:37
 * @Description:
 */
@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController {
	
	private String prefix = "user";

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IPostService postService;

	@RequiresPermissions("system:user:view")
	@GetMapping()
	public String user() {
		return this.prefix + "/user";
	}

	@RequiresPermissions("system:user:list")
	@PostMapping("/list")
	@ResponseBody
	public TableDataInfo list(User user) {
		startPage();
		List<User> list = this.userService.selectUserList(user);
		return super.getDataTable(list);
	}

	@RequiresPermissions("system:user:export")
	@PostMapping("/export")
	@ResponseBody
	public AjaxResult export(User user) throws Exception {
		try {
			List<User> list = this.userService.selectUserList(user);
			ExcelUtil<User> util = new ExcelUtil<User>(User.class);
			return util.exportExcel(list, "user");
		} catch (Exception e) {
			return error("导出Excel失败，请联系网站管理员！");
		}
	}

	/**
	 * 新增用户，返回一个html的编辑页面。
	 */
	@GetMapping("/add")
	public String add(ModelMap mmap) {
		mmap.put("roles", this.roleService.selectRoleAll());
		mmap.put("posts", this.postService.selectPostAll());
		return this.prefix + "/add";
	}

	/**
	 * 	新增保存用户，上面那个函数返回的页面上，点击提交会转到这里。<br>
	 * 	多数据源的情况下，@Transactional这个注解需要指明一个数据源。
	 */
	@RequiresPermissions("system:user:add")
	@PostMapping("/add")
	@Transactional(rollbackFor = Exception.class, transactionManager = "sysTransactionManager")
	@ResponseBody
	public AjaxResult addSave(User user) {
		if (StringUtils.isNotNull(user.getUserId()) && User.isAdmin(user.getUserId()))
			return error("不允许修改超级管理员用户");
		return super.toAjax(this.userService.insertUser(user));
	}

	/**
	 * 修改用户
	 */
	@GetMapping("/edit/{userId}")
	public String edit(@PathVariable("userId") Long userId, ModelMap mmap) {
		mmap.put("user", this.userService.selectUserById(userId));
		mmap.put("roles", this.roleService.selectRolesByUserId(userId));
		mmap.put("posts", this.postService.selectPostsByUserId(userId));
		return this.prefix + "/edit";
	}

	/**
	 * 修改保存用户
	 */
	@RequiresPermissions("system:user:edit")
	@PostMapping("/edit")
	@Transactional(rollbackFor = Exception.class, transactionManager = "sysTransactionManager")
	@ResponseBody
	public AjaxResult editSave(User user) {
		if (StringUtils.isNotNull(user.getUserId()) && User.isAdmin(user.getUserId()))
			return error("不允许修改超级管理员用户");
		return super.toAjax(this.userService.updateUser(user));
	}

	@RequiresPermissions("system:user:resetPwd")
	@GetMapping("/resetPwd/{userId}")
	public String resetPwd(@PathVariable("userId") Long userId, ModelMap mmap) {
		mmap.put("user", this.userService.selectUserById(userId));
		return this.prefix + "/resetpwd";
	}

	@RequiresPermissions("system:user:resetPwd")
	@PostMapping("/resetPwd")
	@ResponseBody
	public AjaxResult resetPwd(User user) {
		return super.toAjax(this.userService.resetUserPwd(user));
	}

	@RequiresPermissions("system:user:remove")
	@PostMapping("/remove")
	@ResponseBody
	public AjaxResult remove(String ids) {
		try {
			return super.toAjax(this.userService.deleteUserByIds(ids));
		} catch (Exception e) {
			return super.error(e.getMessage());
		}
	}

	/**
	 * 	校验用户名
	 */
	@PostMapping("/checkLoginNameUnique")
	@ResponseBody
	public String checkLoginNameUnique(User user) {
		return this.userService.checkLoginNameUnique(user.getLoginName());
	}

	/**
	 * 	校验手机号码
	 */
	@PostMapping("/checkPhoneUnique")
	@ResponseBody
	public String checkPhoneUnique(User user) {
		return this.userService.checkPhoneUnique(user);
	}

	/**
	 * 	校验email邮箱
	 */
	@PostMapping("/checkEmailUnique")
	@ResponseBody
	public String checkEmailUnique(User user) {
		return this.userService.checkEmailUnique(user);
	}
}
