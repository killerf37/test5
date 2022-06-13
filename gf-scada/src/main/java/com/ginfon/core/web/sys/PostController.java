package com.ginfon.core.web.sys;

import com.ginfon.core.utils.AjaxResult;
import com.ginfon.core.utils.ExcelUtil;
import com.ginfon.core.web.BaseController;
import com.ginfon.core.web.entity.Post;
import com.ginfon.core.web.page.TableDataInfo;
import com.ginfon.core.web.service.IPostService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位信息操作处理
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/post")
public class PostController extends BaseController {
	private String prefix = "post";

	@Autowired
	private IPostService postService;

	@RequiresPermissions("system:post:view")
	@GetMapping()
	public String operlog() {
		return prefix + "/post";
	}

	@RequiresPermissions("system:post:list")
	@PostMapping("/list")
	@ResponseBody
	public TableDataInfo list(Post post) {
		startPage();
		List<Post> list = postService.selectPostList(post);
		return getDataTable(list);
	}

	@RequiresPermissions("system:post:export")
	@PostMapping("/export")
	@ResponseBody
	public AjaxResult export(Post post) throws Exception {
		try {
			List<Post> list = postService.selectPostList(post);
			ExcelUtil<Post> util = new ExcelUtil<Post>(Post.class);
			return util.exportExcel(list, "post");
		} catch (Exception e) {
			return error("导出Excel失败，请联系网站管理员！");
		}
	}

	@RequiresPermissions("system:post:remove")
	@PostMapping("/remove")
	@ResponseBody
	public AjaxResult remove(String ids) {
		try {
			return toAjax(postService.deletePostByIds(ids));
		} catch (Exception e) {
			return error(e.getMessage());
		}
	}

	/**
	 * 新增岗位
	 */
	@GetMapping("/add")
	public String add() {
		return prefix + "/add";
	}

	/**
	 * 新增保存岗位
	 */
	@RequiresPermissions("system:post:add")
	@PostMapping("/add")
	@ResponseBody
	public AjaxResult addSave(Post post) {
		return toAjax(postService.insertPost(post));
	}

	/**
	 * 修改岗位
	 */
	@GetMapping("/edit/{postId}")
	public String edit(@PathVariable("postId") Long postId, ModelMap mmap) {
		mmap.put("post", postService.selectPostById(postId));
		return prefix + "/edit";
	}

	/**
	 * 修改保存岗位
	 */
	@RequiresPermissions("system:post:edit")
	@PostMapping("/edit")
	@ResponseBody
	public AjaxResult editSave(Post post) {
		return toAjax(postService.updatePost(post));
	}

	/**
	 * 校验岗位名称
	 */
	@PostMapping("/checkPostNameUnique")
	@ResponseBody
	public String checkPostNameUnique(Post post) {
		return postService.checkPostNameUnique(post);
	}

	/**
	 * 校验岗位编码
	 */
	@PostMapping("/checkPostCodeUnique")
	@ResponseBody
	public String checkPostCodeUnique(Post post) {
		return postService.checkPostCodeUnique(post);
	}
}
