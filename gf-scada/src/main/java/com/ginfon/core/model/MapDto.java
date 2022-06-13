package com.ginfon.core.model;

import org.jsoup.select.Elements;

/**
 * @description:
 * @author: curtain
 * @create: 2022-05-07 17:08
 **/
public class MapDto {

	private String lineName;

	private Elements svg;

	private String url;

	public MapDto() {
	}

	public MapDto(String lineName, Elements svg, String url) {
		this.lineName = lineName;
		this.svg = svg;
		this.url = url;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public Elements getSvg() {
		return svg;
	}

	public void setSvg(Elements svg) {
		this.svg = svg;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
