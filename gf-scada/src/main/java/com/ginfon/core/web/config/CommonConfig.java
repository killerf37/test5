package com.ginfon.core.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 */
@Component
@ConfigurationProperties(prefix = "goldpeak")
public class CommonConfig {
	/**
	 * 	项目名称
	 */
	private String name;
	/**
	 * 	版本
	 */
	private String version;
	/**
	 * 	版权年份
	 */
	private String copyrightYear;
	/**
	 * 	上传路径
	 */
	private static String upload;
	/**
	 * 	下载地址
	 */
	private static String downloadPath;
	/**
	 *	获取地址开关
	 */
	private static boolean addressEnabled;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCopyrightYear() {
		return copyrightYear;
	}

	public void setCopyrightYear(String copyrightYear) {
		this.copyrightYear = copyrightYear;
	}

	public static String getUpload() {
		return upload;
	}

	public void setUpload(String upload) {
		CommonConfig.upload = upload;
	}

	public static String getDownloadPath() {
		return downloadPath;
	}

	public static void setDownloadPath(String downloadPath) {
		CommonConfig.downloadPath = downloadPath;
	}

	public static boolean isAddressEnabled() {
		return addressEnabled;
	}

	public void setAddressEnabled(boolean addressEnabled) {
		CommonConfig.addressEnabled = addressEnabled;
	}

}
