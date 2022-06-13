package com.ginfon.core.web.sys;

import com.ginfon.core.utils.AjaxResult;
import com.ginfon.core.web.BaseController;
import com.ginfon.core.web.entity.Dept;
import com.ginfon.core.web.service.IDeptService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 	部门信息
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/dept")
public class DeptController extends BaseController {
	private String prefix = "dept";

	@Autowired
	private IDeptService deptService;

	@RequiresPermissions("system:dept:view")
	@GetMapping()
	public String dept() {
		return prefix + "/dept";
	}

	@RequiresPermissions("system:dept:list")
	@GetMapping("/list")
	@ResponseBody
	public List<Dept> list(Dept dept) {
		List<Dept> deptList = deptService.selectDeptList(dept);
		return deptList;
	}

	/**
	 * 新增部门
	 */
	@GetMapping("/add/{parentId}")
	public String add(@PathVariable("parentId") Long parentId, ModelMap mmap) {
		mmap.put("dept", deptService.selectDeptById(parentId));
		return prefix + "/add";
	}

	/**
	 * 新增保存部门
	 */
	@RequiresPermissions("system:dept:add")
	@PostMapping("/add")
	@ResponseBody
	public AjaxResult addSave(Dept dept) {
		return toAjax(deptService.insertDept(dept));
	}

	/**
	 * 修改
	 */
	@GetMapping("/edit/{deptId}")
	public String edit(@PathVariable("deptId") Long deptId, ModelMap mmap) {
		mmap.put("dept", deptService.selectDeptById(deptId));
		return prefix + "/edit";
	}

	/**
	 * 保存
	 */
	@RequiresPermissions("system:dept:edit")
	@PostMapping("/edit")
	@ResponseBody
	public AjaxResult editSave(Dept dept) {
		return toAjax(deptService.updateDept(dept));
	}

	/**
	 * 删除
	 */
	@RequiresPermissions("system:dept:remove")
	@PostMapping("/remove/{deptId}")
	@ResponseBody
	public AjaxResult remove(@PathVariable("deptId") Long deptId) {
		if (deptService.selectDeptCount(deptId) > 0) {
			return error(1, "存在下级部门,不允许删除");
		}
		if (deptService.checkDeptExistUser(deptId)) {
			return error(1, "部门存在用户,不允许删除");
		}
		return toAjax(deptService.deleteDeptById(deptId));
	}

	/**
	 * 校验部门名称
	 */
	@PostMapping("/checkDeptNameUnique")
	@ResponseBody
	public String checkDeptNameUnique(Dept dept) {
		return deptService.checkDeptNameUnique(dept);
	}

	/**
	 * 选择部门树
	 */
	@GetMapping("/selectDeptTree/{deptId}")
	public String selectDeptTree(@PathVariable("deptId") Long deptId, ModelMap mmap) {
		mmap.put("dept", deptService.selectDeptById(deptId));
		return prefix + "/tree";
	}

	/**
	 * 加载部门列表树
	 */
	@GetMapping("/treeData")
	@ResponseBody
	public List<Map<String, Object>> treeData() {
		List<Map<String, Object>> tree = deptService.selectDeptTree();
		return tree;
	}
}
