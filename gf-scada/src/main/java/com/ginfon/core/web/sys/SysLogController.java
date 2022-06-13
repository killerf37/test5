package com.ginfon.core.web.sys;

import com.ginfon.core.web.BaseController;
import com.ginfon.core.web.entity.SysLog;
import com.ginfon.core.web.page.TableDataInfo;
import com.ginfon.core.web.service.ISysLogInfoService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/system/log")
public class SysLogController extends BaseController {

	@Autowired
	private ISysLogInfoService sysLogInfoService;

	@RequiresPermissions("system:log:query")
	@GetMapping()
	public String sysLog() {
		return "sysLog/sysLog";
	}

	@PostMapping("/list")
	@ResponseBody
	public TableDataInfo list(SysLog sysLog) {
		startPage();
		List<SysLog> list = sysLogInfoService.sysLogQuery(sysLog);
		return getDataTable(list);
	}
}
