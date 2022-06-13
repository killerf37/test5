package com.ginfon.core.web.sys;

import com.ginfon.core.utils.AjaxResult;
import com.ginfon.core.utils.DateUtils;
import com.ginfon.core.utils.GetClientIP;
import com.ginfon.core.utils.MessageUtils;
import com.ginfon.core.utils.ServletUtils;
import com.ginfon.core.web.BaseController;
import com.ginfon.core.web.entity.SysLog;
import com.ginfon.core.web.service.ISysLogInfoService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.UnknownHostException;

/**
 * @author James
 */
@Controller
public class LoginController extends BaseController {
	@Autowired
	private ISysLogInfoService logInfoService;

	@GetMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response) {
		// 如果是Ajax请求，返回Json字符串。
		if (ServletUtils.isAjaxRequest(request)) {
			return ServletUtils.renderString(response, "{\"code\":\"1\",\"msg\":\"未登录或登录超时。请重新登录\"}");
		}
		return "login";
	}

	@PostMapping("/login")
	@ResponseBody
	public AjaxResult ajaxLogin(String username, String password) throws UnknownHostException {
		UsernamePasswordToken token = new UsernamePasswordToken(username, password, false);
		Subject subject = SecurityUtils.getSubject();
		/**
		 * 	系统日志
		 */
		SysLog sysLog = new SysLog();
		sysLog.setLogType("1");
		sysLog.setLogTime(DateUtils.getTime());
		sysLog.setUserId(username);
		String ip = GetClientIP.getClientIpAddress();
		sysLog.setClientIp(ip);
		sysLog.setLogInfo("登陆系统");
		try {
			subject.login(token);
			logInfoService.insertSysLog(sysLog);
			return success();
		} catch (Exception ex) {
			ex.printStackTrace();
			String msg = MessageUtils.message("user.password.not.match");
			if (ex instanceof UnknownAccountException) {
				msg = MessageUtils.message("user.not.exists");
			} else if (ex instanceof IncorrectCredentialsException) {
				msg = MessageUtils.message("user.password.error");
			}

			return error(msg);
		}
	}

	@GetMapping("/system/logout")
	public String logOut(HttpSession session) throws UnknownHostException {
		Subject subject = SecurityUtils.getSubject();
//        SysLog sysLog = new SysLog();
//        sysLog.setLogType("2");
//        sysLog.setLogTime(DateUtils.getTime());
//        sysLog.setUserId(ShiroUtils.getLoginName());
//        InetAddress addr = InetAddress.getLocalHost();
//        sysLog.setClientIp(addr.getHostAddress());
//        sysLog.setLogInfo("退出系统");
//        logInfoService.insertSysLog(sysLog);
		subject.logout();
		return "login";
	}
}
