package com.ginfon.scada.gateway.socket.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

/**
 * 通过 Marker将不同类型的日志写入不同的文件。
 * 
 * @author zhuxinkun
 *
 */
public class KsLogUtil {
	private static String INFO_MARKER = "INFOMARKER";
	private static String ERROR_MARKER = "ERRORMARKER";
	private static Logger log;

	private static final Map<Class<?>, Logger> loggers = new HashMap<>();

	public static void error(Throwable t) {
		outError("", t);
	}

	public static void error(Object message) {
		outError(KsLogParase.parseMessages(message));
	}

	public static void error(String message) {
		outError(message);
	}

	public static void error(String message, Throwable t) {
		outError(message, t);
	}

	public static void error(String message, Object... params) {
		outError(message, params);
	}

	public static void info(String message, Object... params) {
		outInfo(message, params);
	}

	public static void info(Object message) {
		outInfo(KsLogParase.parseMessages(message));
	}

	public static void info(String message) {
		outInfo(message);
	}

	public static void debug(String message) {
		outDebug(message);
	}

	public static void debug(String message, Object... params) {
		outDebug(message, params);
	}

	private static void outDebug(String message, Object... params) {
		Object[] caller = getCallerInfo();
		if (caller == null) {
			return;
		}
		Class<?> cls = (Class<?>) caller[0];
		log = loggers.get(cls);
		if (log == null) {
			log = LogManager.getLogger(cls);
			loggers.put(cls, log);
		}
		try {
			log.info(MarkerManager.getMarker(INFO_MARKER), getMessage(message, caller[2]), params);
		} catch (Exception e) {
			loggers.put(cls, null);
		}
	}

	private static void outInfo(String message, Object... params) {
		Object[] caller = getCallerInfo();
		if (caller == null) {
			return;
		}
		Class<?> cls = (Class<?>) caller[0];
		log = loggers.get(cls);
		if (log == null) {
			log = LogManager.getLogger(cls);
			loggers.put(cls, log);
		}
		try {
			log.info(MarkerManager.getMarker(INFO_MARKER), getMessage(message, caller[2]), params);
		} catch (Exception e) {
			loggers.put(cls, null);
		}
	}

	private static void outError(String message, Object... params) {
		Object[] caller = getCallerInfo();
		if (caller == null) {
			return;
		}
		Class<?> cls = (Class<?>) caller[0];
		log = loggers.get(cls);
		if (log == null) {
			log = LogManager.getLogger(cls);
			loggers.put(cls, log);
		}
		try {
			log.error(MarkerManager.getMarker(ERROR_MARKER), getMessage(message, caller[2]), params);
		} catch (Exception e) {
			loggers.put(cls, null);
		}
	}

	private static void outError(String message, Throwable t) {
		Object[] caller = getCallerInfo();
		if (caller == null) {
			return;
		}
		Class<?> cls = (Class<?>) caller[0];
		log = loggers.get(cls);
		if (log == null) {
			log = LogManager.getLogger(cls);
			loggers.put(cls, log);
		}
		try {
			log.error(MarkerManager.getMarker(ERROR_MARKER), getMessage(message, caller[2]), t);
		} catch (Exception e) {
			loggers.put(cls, null);
		}
	}

	private static String getMessage(String message, Object line) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(line);
		sb.append(") ");
		sb.append(message);
		return sb.toString();
	}

	protected static Object[] getCallerInfo() {
		Object[] ret = null;
		try {
			ret = new Object[3];
			StackTraceElement[] stack = new Throwable().getStackTrace();
			StackTraceElement ste = stack[3];
			ret[0] = Class.forName(ste.getClassName());
			ret[1] = ste.getMethodName();
			ret[2] = ste.getLineNumber();
		} catch (Exception localException) {
			return null;
		}
		return ret;
	}
}
