package com.ginfon.sfclient.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ByteUtil {

    private ByteUtil() {

    }

    /**
     * 将byte数组转换成int。2位byte或者4位byte。
     *
     * @param bytes byte数组，长度为2或者4。
     * @return int
     */
    public static int byteToInt(byte... bytes) {
        if (bytes == null)
            throw new NullPointerException("byte数组不能为空值！");
        int intValue = 0;
        //	如果长度是2，就补到4位。
        if (bytes.length == 2) {
            byte[] newBytes = new byte[]{0, 0, bytes[0], bytes[1]};
            for (int i = 0; i < newBytes.length; i++)
                intValue += (newBytes[i] & 0xFF) << (8 * (3 - i));
        } else
            for (int i = 0; i < bytes.length; i++)
                intValue += (bytes[i] & 0xFF) << (8 * (3 - i));
        return intValue;
    }

    /**
     * 将8位byte转换成ling类型。
     *
     * @param bytes 8位byte数组
     * @return long
     */
    public static long byteToLong(byte... bytes) {
        long longValue = 0;
        if (bytes.length == 8) {
            for (int i = 0; i < 8; ++i) {
                int shift = (7 - i) << 3;
                longValue |= ((long) 0xff << shift) & ((long) bytes[i] << shift);
            }
        }
        return longValue;
    }

    /**
     * 将int转换为byte数组，默认为长度4的byte数组。
     *
     * @param value int
     * @return byte数组
     */
    public static byte[] intToByte(int value) {
        return intToByte(value, 4);
    }

    /**
     * @param value
     * @param src
     * @param srcPos
     * @param len
     */
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

    /**
     * @param value
     * @param src
     * @param srcPos
     * @param len
     */
    public static void longToByte(long value, byte[] src, int srcPos, int len) {
        if (len == 8) {
            src[srcPos] = (byte) ((value >> 56) & 0xFF);
            src[srcPos + 1] = (byte) ((value >> 48) & 0xFF);
            src[srcPos + 2] = (byte) ((value >> 40) & 0xFF);
            src[srcPos + 3] = (byte) ((value >> 32) & 0xFF);
            src[srcPos + 4] = (byte) ((value >> 24) & 0xFF);
            src[srcPos + 5] = (byte) ((value >> 16) & 0xFF);
            src[srcPos + 6] = (byte) ((value >> 8) & 0xFF);
            src[srcPos + 7] = (byte) ((value) & 0xFF);
        }
    }


    public static byte[] longToByte(long value, int len) {
        if (len == 8) {
            byte[] result = new byte[8];
            result[0] = (byte) ((value >> 56) & 0xFF);
            result[1] = (byte) ((value >> 48) & 0xFF);
            result[2] = (byte) ((value >> 40) & 0xFF);
            result[3] = (byte) ((value >> 32) & 0xFF);
            result[4] = (byte) ((value >> 24) & 0xFF);
            result[5] = (byte) ((value >> 16) & 0xFF);
            result[6] = (byte) ((value >> 8) & 0xFF);
            result[7] = (byte) ((value) & 0xFF);
            return result;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Integer.toBinaryString(65535));
        System.out.println(258 & 0xFF);
    }

    /**
     * 将byte转换成二进制数，以字符串形式返回，比如输入0x01，则返回00000001……这太蠢了。
     *
     * @param b byte
     * @return 二进制字符串
     */
    public static String toBinaryString(byte b) {
        return Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
    }

    /**
     * 将复数的byte转换成二进制数，以字符串形式返回。比如输入{0x00, 0x01}，则返回0000000000000001，这太蠢了。
     *
     * @param bytes byte数组
     * @return 二进制字符串
     */
    public static String toBinaryString(byte... bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
            sb.append(toBinaryString(bytes[i]));
        return sb.toString();
    }

    /**
     * byte[] 转16进制字符串
     *
     * @param b
     * @return
     */
    public static String bytesToString16(byte[] b) {
        char[] _16 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            sb.append(_16[b[i] >> 4 & 0xf]).append(_16[b[i] & 0xf]).append(' ');
        }
        return sb.toString();
    }


    /**
     * byte[] 转16进制字符串
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
     * 十六进制字符串转byte数组。
     *
     * @param hex
     * @return
     */
    public static byte[] string16ToBytes(String hex) {
        hex = hex.replace(" ", "");
        ByteBuffer bf = ByteBuffer.allocate(hex.length() / 2);
        for (int i = 0; i < hex.length(); i++) {
            String hexStr = hex.charAt(i) + "";
            i++;
            hexStr += hex.charAt(i);
            byte b = (byte) Integer.parseInt(hexStr, 16);
            bf.put(b);
        }
        return bf.array();
    }

    /**
     * 进行LRC校验。
     *
     * @param data
     * @return
     */
    public static byte getLRC(byte[] data) {
        int tmp = 0;
        for (int i = 0; i < data.length; i++) {
            tmp = tmp + (byte) data[i];
        }
        tmp = ~tmp;
        tmp = (tmp & (0xff));
        tmp += 1;
        return (byte) tmp;
    }

    /**
     * @Description: 根据十进制数值，获取对应二进制中1所在位置索引集合
     * @Param:状态值
     * @return: 状态位置索引集合
     * @Author: swenson
     * @Date: 2021/5/10
     */
    public static List<Integer> getIndexFromNum(int state) {
        if (state > 65535) {
            throw new IllegalArgumentException();
        }
        List<Integer> indexList = new ArrayList<>();
        StringBuilder sb = new StringBuilder(Integer.toBinaryString(state)).reverse();
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == 49) {
                indexList.add(i);
            }
        }
        return indexList;
    }

    public static String byteToAscii(byte[] b) {
        return new String(b, StandardCharsets.US_ASCII);
    }


    public static byte getXor(byte[] data) {
        byte xor = 0;
        for (int i = 1; i < data.length; i++) {
            if (i == 8) {
                continue;
            }
            xor ^= data[i];
        }
        return xor;
    }

    public static byte getXor2(byte[] data) {
        byte xor = 0;
        for (int i = 1; i < data.length; i++) {
            xor ^= data[i];
        }
        return xor;
    }
}
