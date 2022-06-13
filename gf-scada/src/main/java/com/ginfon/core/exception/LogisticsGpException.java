package com.ginfon.core.exception;

public class LogisticsGpException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Integer code;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public LogisticsGpException() {
		super();
	}

	public LogisticsGpException(String message, Integer code) {
		super(message);
		this.code = code;
	}
}
