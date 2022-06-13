package com.ginfon.core.exception;

/**
 * @author James
 */
public class LogisticsGpRepositoryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LogisticsGpRepositoryException(String message) {
		super(message);
	}

	public LogisticsGpRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}
}
