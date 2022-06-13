package com.ginfon.sfclient.channel;


import com.ginfon.sfclient.util.ByteUtil;

/**
 * 顺丰WCS发来的数据。
 *
 * @author Mark
 */
public final class SFScadaMessageFrame extends BaseSocketMessageFrame {

    /**
     * 当前协议的版本编号。
     */
    private int xor;

    private String lineNo;

    private long receiveTime;


    public SFScadaMessageFrame(byte[] srcMsg, byte[] msgBody, int sn, int functionCode, long time, int xor, String lineNo, long receiveTime) {
        super(srcMsg, msgBody, sn, functionCode, time);
        this.xor = xor;
        this.lineNo = lineNo;
        this.receiveTime = receiveTime;
    }

    public int getXor() {
        return this.xor;
    }

    public String getLineNo() {
        return this.lineNo;
    }

    public long getReceiveTime() {
        return this.receiveTime;
    }

    public static SFScadaMessageFrame create(byte[] sourceMsg) {
        //	序列号
        int sn = ByteUtil.byteToInt(sourceMsg[4], sourceMsg[5], sourceMsg[6], sourceMsg[7]);
        // 异或值
        int xor = ByteUtil.byteToInt(sourceMsg[8]);
        // 顺丰时间戳
        long receiveTime = ByteUtil.byteToLong(sourceMsg[9], sourceMsg[10], sourceMsg[11], sourceMsg[12], sourceMsg[13], sourceMsg[14], sourceMsg[15], sourceMsg[16]);
        byte[] lineMsg = new byte[10];
        System.arraycopy(sourceMsg, 17, lineMsg, 0, 10);
        //	创建报文体部分
        byte[] msgBody = new byte[sourceMsg.length - 27];
        System.arraycopy(sourceMsg, 27, msgBody, 0, msgBody.length);
        //	功能代码
        int functionCode = ByteUtil.byteToInt(msgBody[2], msgBody[3]);
        // 线体编码
        String lineNo = ByteUtil.byteToAscii(lineMsg);
        //	请求时间
        long time = System.currentTimeMillis();
        SFScadaMessageFrame frame = new SFScadaMessageFrame(sourceMsg, msgBody, sn, functionCode, time, xor, lineNo, receiveTime);
        return frame;
    }
}
