package com.ginfon.core.web.sys;

import com.ginfon.core.utils.AjaxResult;
import com.ginfon.core.utils.MessageUtils;
import com.ginfon.core.web.BaseController;
import com.ginfon.core.web.entity.Menu;
import com.ginfon.core.web.entity.Role;
import com.ginfon.core.web.service.IMenuService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜单信息
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/menu")
public class MenuController extends BaseController {

	private String prefix = "menu";

	@Autowired
	private IMenuService menuService;

	@RequiresPermissions("system:menu:view")
	@GetMapping()
	public String menu() {
		return prefix + "/menu";
	}

	@RequiresPermissions("system:menu:list")
	@GetMapping("/list")
	@ResponseBody
	public List<Menu> list(Menu menu) {
		List<Menu> menuList = menuService.selectMenuList(menu);
		return menuList;
	}

	/**
	 * 删除菜单
	 */
	@RequiresPermissions("system:menu:remove")
	@PostMapping("/remove/{menuId}")
	@ResponseBody
	public AjaxResult remove(@PathVariable("menuId") Long menuId) {
		if (menuService.selectCountMenuByParentId(menuId) > 0) {
			return error(1, "存在子菜单,不允许删除");
		}
		if (menuService.selectCountRoleMenuByMenuId(menuId) > 0) {
			return error(1, "菜单已分配,不允许删除");
		}
		return toAjax(menuService.deleteMenuById(menuId));
	}

	/**
	 * 新增
	 */
	@GetMapping("/add/{parentId}")
	public String add(@PathVariable("parentId") Long parentId, ModelMap mmap) {
		Menu menu = null;
		if (0L != parentId) {
			menu = menuService.selectMenuById(parentId);
		} else {
			menu = new Menu();
			menu.setMenuId(0L);
			menu.setMenuName(MessageUtils.message("menu.main.directory"));
			menu.setI18nKey("menu.main.directory");
		}
		mmap.put("menu", menu);
		return prefix + "/add";
	}

	/**
	 * 新增保存菜单
	 */
	@RequiresPermissions("system:menu:add")
	@PostMapping("/add")
	@ResponseBody
	public AjaxResult addSave(Menu menu) {
		return toAjax(menuService.insertMenu(menu));
	}

	/**
	 * 修改菜单
	 */
	@GetMapping("/edit/{menuId}")
	public String edit(@PathVariable("menuId") Long menuId, ModelMap mmap) {
		mmap.put("menu", menuService.selectMenuById(menuId));
		return prefix + "/edit";
	}

	/**
	 * 修改保存菜单
	 */
	@RequiresPermissions("system:menu:edit")
	@PostMapping("/edit")
	@ResponseBody
	public AjaxResult editSave(Menu menu) {
		return toAjax(menuService.updateMenu(menu));
	}

	/**
	 * 选择菜单图标
	 */
	@GetMapping("/icon")
	public String icon() {
		return prefix + "/icon";
	}

	/**
	 * 校验菜单名称
	 */
	@PostMapping("/checkMenuNameUnique")
	@ResponseBody
	public String checkMenuNameUnique(Menu menu) {
		return menuService.checkMenuNameUnique(menu);
	}

	/**
	 * 加载角色菜单列表树
	 */
	@GetMapping("/roleMenuTreeData")
	@ResponseBody
	public List<Map<String, Object>> roleMenuTreeData(Role role) {
		List<Map<String, Object>> tree = menuService.roleMenuTreeData(role);
		return tree;
	}

	/**
	 * 加载所有菜单列表树
	 */
	@GetMapping("/menuTreeData")
	@ResponseBody
	public List<Map<String, Object>> menuTreeData(Role role) {
		List<Map<String, Object>> tree = menuService.menuTreeData();
		return tree;
	}

	/**
	 * 选择菜单树
	 */
	@GetMapping("/selectMenuTree/{menuId}")
	public String selectMenuTree(@PathVariable("menuId") Long menuId, ModelMap mmap) {
		mmap.put("menu", menuService.selectMenuById(menuId));
		return prefix + "/tree";
	}
}