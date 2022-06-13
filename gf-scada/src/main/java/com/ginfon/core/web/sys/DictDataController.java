package com.ginfon.core.web.sys;

import com.ginfon.core.utils.AjaxResult;
import com.ginfon.core.utils.ExcelUtil;
import com.ginfon.core.web.BaseController;
import com.ginfon.core.web.entity.DictData;
import com.ginfon.core.web.page.TableDataInfo;
import com.ginfon.core.web.service.IDictDataService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典信息
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/dict/data")
public class DictDataController extends BaseController {
	private String prefix = "dict/data";

	@Autowired
	private IDictDataService dictDataService;

	@RequiresPermissions("system:dict:view")
	@GetMapping()
	public String dictData() {
		return prefix + "/data";
	}

	@PostMapping("/list")
	@RequiresPermissions("system:dict:list")
	@ResponseBody
	public TableDataInfo list(DictData dictData) {
		startPage();
		List<DictData> list = dictDataService.selectDictDataList(dictData);
		return getDataTable(list);
	}

	@RequiresPermissions("system:dict:export")
	@PostMapping("/export")
	@ResponseBody
	public AjaxResult export(DictData dictData) throws Exception {
		try {
			List<DictData> list = dictDataService.selectDictDataList(dictData);
			ExcelUtil<DictData> util = new ExcelUtil<DictData>(DictData.class);
			return util.exportExcel(list, "dictData");
		} catch (Exception e) {
			return error("导出Excel失败，请联系网站管理员！");
		}
	}

	/**
	 * 新增字典类型
	 */
	@GetMapping("/add/{dictType}")
	public String add(@PathVariable("dictType") String dictType, ModelMap mmap) {
		mmap.put("dictType", dictType);
		return prefix + "/add";
	}

	/**
	 * 新增保存字典类型
	 */
	@RequiresPermissions("system:dict:add")
	@PostMapping("/add")
	@ResponseBody
	public AjaxResult addSave(DictData dict) {
		return toAjax(dictDataService.insertDictData(dict));
	}

	/**
	 * 修改字典类型
	 */
	@GetMapping("/edit/{dictCode}")
	public String edit(@PathVariable("dictCode") Long dictCode, ModelMap mmap) {
		mmap.put("dict", dictDataService.selectDictDataById(dictCode));
		return prefix + "/edit";
	}

	/**
	 * 修改保存字典类型
	 */
	@RequiresPermissions("system:dict:edit")
	@PostMapping("/edit")
	@ResponseBody
	public AjaxResult editSave(DictData dict) {
		return toAjax(dictDataService.updateDictData(dict));
	}

	@RequiresPermissions("system:dict:remove")
	@PostMapping("/remove")
	@ResponseBody
	public AjaxResult remove(String ids) {
		return toAjax(dictDataService.deleteDictDataByIds(ids));
	}
}
