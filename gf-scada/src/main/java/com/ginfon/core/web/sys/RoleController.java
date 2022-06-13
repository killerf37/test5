package com.ginfon.core.web.sys;

import com.ginfon.core.utils.AjaxResult;
import com.ginfon.core.utils.ExcelUtil;
import com.ginfon.core.web.BaseController;
import com.ginfon.core.web.entity.Role;
import com.ginfon.core.web.page.TableDataInfo;
import com.ginfon.core.web.service.IRoleService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/role")
public class RoleController extends BaseController {

	private String prefix = "role";

	@Autowired
	private IRoleService roleService;

	@RequiresPermissions("system:role:view")
	@GetMapping()
	public String role() {
		return prefix + "/role";
	}

	@RequiresPermissions("system:role:list")
	@PostMapping("/list")
	@ResponseBody
	public TableDataInfo list(Role role) {
		startPage();
		List<Role> list = roleService.selectRoleList(role);
		return getDataTable(list);
	}

	@RequiresPermissions("system:role:export")
	@PostMapping("/export")
	@ResponseBody
	public AjaxResult export(Role role) throws Exception {
		try {
			List<Role> list = roleService.selectRoleList(role);
			ExcelUtil<Role> util = new ExcelUtil<Role>(Role.class);
			return util.exportExcel(list, "role");
		} catch (Exception e) {
			return error("导出Excel失败，请联系网站管理员！");
		}
	}

	/**
	 * 新增角色
	 */
	@GetMapping("/add")
	public String add(ModelMap mmap) {
		mmap.put("roles", roleService.selectRoleAll());
		return prefix + "/add";
	}

	/**
	 * 新增保存角色
	 */
	@RequiresPermissions("system:role:add")
	@PostMapping("/add")
	@Transactional(rollbackFor = Exception.class, transactionManager = "sysTransactionManager")
	@ResponseBody
	public AjaxResult addSave(Role role) {
		return toAjax(roleService.insertRole(role));

	}

	/**
	 * 修改角色
	 */
	@GetMapping("/edit/{roleId}")
	public String edit(@PathVariable("roleId") Long roleId, ModelMap mmap) {
		mmap.put("role", roleService.selectRoleById(roleId));
		return prefix + "/edit";
	}

	/**
	 * 修改保存角色
	 */
	@RequiresPermissions("system:role:edit")
	@PostMapping("/edit")
	@Transactional(rollbackFor = Exception.class, transactionManager = "sysTransactionManager")
	@ResponseBody
	public AjaxResult editSave(Role role) {
		return toAjax(roleService.updateRole(role));
	}

	@RequiresPermissions("system:role:remove")
	@PostMapping("/remove")
	@ResponseBody
	public AjaxResult remove(String ids) {
		try {
			return toAjax(roleService.deleteRoleByIds(ids));
		} catch (Exception e) {
			return error(e.getMessage());
		}
	}

	/**
	 * 校验角色名称
	 */
	@PostMapping("/checkRoleNameUnique")
	@ResponseBody
	public String checkRoleNameUnique(Role role) {
		return roleService.checkRoleNameUnique(role);
	}

	/**
	 * 校验角色权限
	 */
	@PostMapping("/checkRoleKeyUnique")
	@ResponseBody
	public String checkRoleKeyUnique(Role role) {
		return roleService.checkRoleKeyUnique(role);
	}

	/**
	 * 选择菜单树
	 */
	@GetMapping("/selectMenuTree")
	public String selectMenuTree() {
		return prefix + "/tree";
	}
}