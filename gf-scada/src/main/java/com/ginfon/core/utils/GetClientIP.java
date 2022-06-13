package com.ginfon.core.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Description: 获取客户端的IP地址
 * @date 2020年5月25日
 */
public class GetClientIP {
	public static String getClientIpAddress() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
		String headerName = "x-forwarded-for";
		String ip = request.getHeader(headerName);
		if (null != ip && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个IP值，第一个IP才是真实IP,它们按照英文逗号','分割
			if (ip.indexOf(",") != -1) {
				ip = ip.split(",")[0];
			}
		}
		if (checkIp(ip)) {
			headerName = "Proxy-Client-IP";
			ip = request.getHeader(headerName);
		}
		if (checkIp(ip)) {
			headerName = "WL-Proxy-Client-IP";
			ip = request.getHeader(headerName);
		}
		if (checkIp(ip)) {
			headerName = "HTTP_CLIENT_IP";
			ip = request.getHeader(headerName);
		}
		if (checkIp(ip)) {
			headerName = "HTTP_X_FORWARDED_FOR";
			ip = request.getHeader(headerName);
		}
		if (checkIp(ip)) {
			headerName = "X-Real-IP";
			ip = request.getHeader(headerName);
		}
		if (checkIp(ip)) {
			headerName = "remote addr";
			ip = request.getRemoteAddr();
			if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ip = inet.getHostAddress();
			}
		}
		return ip;
	}

	private static boolean checkIp(String ip) {
		if (null == ip || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			return true;
		}
		return false;
	}
}
