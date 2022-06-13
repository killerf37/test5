package com.ginfon.scada.gateway.socket.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.ks.util.KsStringUtils;

public class KsLogParase {

	public static String logParase(String msg, String separate, HashMap<String, Integer> fieldList) {

		String[] msgs = msg.split(separate);
		if (msgs.length != fieldList.size()) {
			return msg;
		}
		StringBuilder sbMsg = new StringBuilder();
		for (int i = 0; i < msgs.length; i++) {
			if (fieldList.containsValue(i)) {
				if (KsStringUtils.isEmpty(sbMsg.toString())) {
					sbMsg.append(msgs[i]);
				} else {
					sbMsg.append(separate + msgs[i]);
				}
			}
		}
		return sbMsg.toString();
	}

	public static String logParase(String msg, String separate, String filter) {
		String[] msgs = msg.split(separate);
		if (msgs.length != filter.length()) {
			return msg;
		}

		StringBuilder sbMsg = new StringBuilder();
		for (int i = 0; i < msgs.length; i++) {
			if ("1".equals(filter.substring(i, i + 1))) {
				if (KsStringUtils.isEmpty(sbMsg.toString())) {
					sbMsg.append(msgs[i]);
				} else {
					sbMsg.append(separate + msgs[i]);
				}
			}
		}
		return sbMsg.toString();
	}

	public static String parseMessages(String[] messages) {
		// TODO \r\n 要定义到ksfw
		StringBuilder sbMsg = new StringBuilder("String[]: \r\n");
		for (String str : messages) {
			sbMsg.append("\t" + str + "\r\n");
		}
		return sbMsg.toString();
	}

	public static String parseMessages(Object message) {
		// TODO \r\n 要定义到ksfw
		StringBuilder sbMsg = new StringBuilder(message.getClass().getSimpleName());
		sbMsg.append(":\r\n");

		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(message.getClass());
			PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
			if (proDescrtptors != null && proDescrtptors.length > 0) {
				for (PropertyDescriptor propDesc : proDescrtptors) {

					Method methodGetUserName = propDesc.getReadMethod();
					if (methodGetUserName != null) {
						Object objUserName = methodGetUserName.invoke(message);

						sbMsg.append("\t" + propDesc.getName());
						sbMsg.append(":");
						sbMsg.append(objUserName == null ? null : objUserName.toString());
						sbMsg.append("\r\n");
					}
				}
			}
			Introspector.flushFromCaches(message.getClass());
		} catch (Exception e) {
			sbMsg.append("\t" + "Messages parse error.");
		}

		return sbMsg.toString();
	}
}
