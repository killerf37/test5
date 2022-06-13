package com.ginfon.core.model;

/**
 * @author James
 */
public class Query {

	/**
	 * 要排序的字段名
	 */
	protected String sort;
	/**
	 * 排序方式: desc \ asc
	 */
	protected String order = "";

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
}