package com.ginfon.scada.util;

public final class ByteUtil {
	
	private ByteUtil() {
		
	}
	
	
	public static int byteToInt(byte...bytes) {
		if(bytes == null)
			throw new NullPointerException("byte数组不能为空值！");
		int intValue = 0;
		//	如果长度是2，就补到4位。
		if(bytes.length == 2) {
			byte[] newBytes = new byte[] {0, 0, bytes[0], bytes[1]};
			for (int i = 0; i < newBytes.length; i++)
				intValue += (newBytes[i] & 0xFF) << (8 * (3 - i));
		}else
			for (int i = 0; i < bytes.length; i++)
				intValue += (bytes[i] & 0xFF) << (8 * (3 - i));
		return intValue;
	}
	
	public static byte[] intToByte(int value) {
		return intToByte(value, 4);
	}
	
	public static void intToByte(int value, byte[] src, int srcPos, int len) {
		if (len == 4) {
			src[srcPos] = (byte) ((value >> 24) & 0xFF);
			src[srcPos + 1] = (byte) ((value >> 16) & 0xFF);
			src[srcPos + 2] = (byte) ((value >> 8) & 0xFF);
			src[srcPos + 3] = (byte) ((value) & 0xFF);
		} else if (len == 2) {
			src[srcPos] = (byte) ((value >> 8) & 0xFF);
			src[srcPos + 1] = (byte) ((value) & 0xFF);
		}
	}

	public static byte[] intToByte(int value, int len) {
		if (len == 4) {
			byte[] result = new byte[4];
			result[0] = (byte) ((value >> 24) & 0xFF);
			result[1] = (byte) ((value >> 16) & 0xFF);
			result[2] = (byte) ((value >> 8) & 0xFF);
			result[3] = (byte) ((value) & 0xFF);
			return result;
		} else if (len == 2) {
			byte[] result = new byte[2];
			result[0] = (byte) ((value >> 8) & 0xFF);
			result[1] = (byte) ((value) & 0xFF);
			return result;
		}
		return null;
	}

	public static StringBuilder HexToStr(int length, String strHex) {
		StringBuilder sb = new StringBuilder();
		int lengc = length - strHex.length();
		if (lengc > 0) {
			for (int i = 0; i < lengc; i++) {
				if (i > 0 && i % 2 == 0) {
					sb.append(" ");
				}
				sb.append("0");
			}
		} else {
		}
		sb.append(strHex);
		return sb;
	}
	
	/**
	 * byte[] 转16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String bytesToString16(byte[] b) {
		char[] _16 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			sb.append(_16[b[i] >> 4 & 0xf]).append(_16[b[i] & 0xf]).append(' ');
		}
		return sb.toString();
	}

	/**
	 * byte 转16进制字符串
	 *
	 * @param b
	 * @return
	 */
	public static String byteToString16(byte b) {
		char[] _16 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		StringBuilder sb = new StringBuilder();
		sb.append(_16[b >> 4 & 0xf]).append(_16[b & 0xf]).append(' ');
		return sb.toString();
	}

	/**
	 *  将byte转换成二进制数，以字符串形式返回，比如输入0x01，则返回00000001……这太蠢了。
	 * @param b byte
	 * @return 二进制字符串
	 */
	public static String toBinaryString(byte b) {
		return Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
	}

	/**
	 *  将复数的byte转换成二进制数，以字符串形式返回。比如输入{0x00, 0x01}，则返回0000000000000001，这太蠢了。
	 * @param bytes byte数组
	 * @return  二进制字符串
	 */
	public static String toBinaryString(byte...bytes) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < bytes.length; i++)
			sb.append(toBinaryString(bytes[i]));
		return sb.toString();
	}
}
