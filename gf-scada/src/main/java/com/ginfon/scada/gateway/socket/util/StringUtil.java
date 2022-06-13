package com.ginfon.scada.gateway.socket.util;

import java.util.Arrays;

/**
 * @Author: James
 * @Date: 2020/4/2 14:41
 * @Description:
 */
public class StringUtil {
	/**
	 * 字符串位数不足时左边补0
	 *
	 * @param s
	 * @param length
	 * @return
	 */
	public static String padLeft(String s, int length) {
		byte[] bs = new byte[length];
		byte[] ss = s.getBytes();
		Arrays.fill(bs, (byte) (48 & 0xff));
		System.arraycopy(ss, 0, bs, length - ss.length, ss.length);
		return new String(bs);
	}
}
