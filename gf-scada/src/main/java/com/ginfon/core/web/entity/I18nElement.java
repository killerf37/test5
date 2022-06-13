package com.ginfon.core.web.entity;

/**
 * 	
 * @author Mark
 *
 */
public class I18nElement {
	
	private String localKey;
	
	private String localZh;
	
	private String localEn;
	
	private String localTh;
	
	public I18nElement() {}

	public String getLocalKey() {
		return localKey;
	}

	public void setLocalKey(String localKey) {
		this.localKey = localKey;
	}

	public String getLocalZh() {
		return localZh;
	}

	public void setLocalZh(String localZh) {
		this.localZh = localZh;
	}

	public String getLocalEn() {
		return localEn;
	}

	public void setLocalEn(String localEn) {
		this.localEn = localEn;
	}

	public String getLocalTh() {
		return localTh;
	}

	public void setLocalTh(String localTh) {
		this.localTh = localTh;
	}
}
