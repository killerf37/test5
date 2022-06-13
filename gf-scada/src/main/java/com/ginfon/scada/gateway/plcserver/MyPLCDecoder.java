package com.ginfon.scada.gateway.plcserver;


import com.ginfon.scada.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2021/09/01/18:25
 * @Description:
 */
public class MyPLCDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger("PLCINFO");
    private int BASE_LENGTH=2;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        LOGGER.info("【PLC解码器】收到报文长度"+in.readableBytes());
        //基础长度不足，我们设定基础长度为4
        if (in.readableBytes() < BASE_LENGTH) {
            return;
        }
        int beginIdx; //记录包头位置
        int ableIndex;//能读取的
        int commentLength=0;//读取长度

        while (true) {
            // 获取包头开始的index
            beginIdx = in.readerIndex();
            ableIndex=in.readableBytes();
            LOGGER.info("【PLC解码器】包头开始索引{},可读长度{}",beginIdx,ableIndex);
            if (beginIdx==0)
            {
                if ((ableIndex-beginIdx)>=2)
                {
                    byte msgHead1=in.readByte();
                    LOGGER.info("【PLC解码器】读取一个字节{}", ByteUtil.byteToString16(msgHead1));
                    if (msgHead1==-1)
                    {
                        byte msgHead2=in.readByte();
                        LOGGER.info("【PLC解码器】上一个字节为FF,读取一个字节{}", ByteUtil.byteToString16(msgHead2));
                        if (msgHead2==-1)
                        {
                            beginIdx=in.readerIndex();
                            LOGGER.info("【PLC解码器】找到报文头FFFF现在索引为{}",beginIdx);
                            // 标记包头开始的index
                            in.markReaderIndex();
                            break;
                        }else
                        {
                            int exxx=in.readerIndex();
                            LOGGER.info("【PLC解码器】FF后不是FF,抛弃{}之前的字节",exxx);
                            in.discardReadBytes();
                        }
                    }else
                    {
                        int exxx=in.readerIndex();
                        LOGGER.info("【PLC解码器】无报文头且字节不是FF,抛弃{}之前的字节",exxx);
                        in.discardReadBytes();
                    }
                }else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }

        //剩余长度不足可读取数量[没有内容长度位]
        ableIndex = in.readableBytes();
        LOGGER.info("【PLC解码器】可读长度为{}",ableIndex);
        if (ableIndex <= 1) {
            return;
        }

        if(beginIdx==2)
        {
            LOGGER.info("【PLC解码器】有报文头,开始获取报文长度");
            if ((ableIndex-beginIdx)>=2)
            {
                short msgLength=in.getShort(2);
                LOGGER.info("【PLC解码器】报文内容定义长度为{}",msgLength);
                //int aaa=in.readerIndex();
                if (ableIndex>=(msgLength-2))
                {
                    commentLength=msgLength;
                    LOGGER.info("【PLC解码器】报文实际长度为{}满足",ableIndex);
                }
            }
            else
            {
                LOGGER.info("【PLC解码器】报文头后,长度字节小于2");
                return;
            }
        }
        else
        {
            return;
        }

        if (commentLength>0)
        {
            if (ableIndex==(commentLength-2))
            {
                LOGGER.info("【PLC解码器】报文内容与长度完全符合,开始获取");
                in.resetReaderIndex();
                in.readerIndex(in.readerIndex()-2);
                ByteBuf msgContent = in.readBytes(commentLength);
                LOGGER.info("【PLC解码器】报文获取成功且清空缓存");
                out.add(msgContent);

            }else if (ableIndex>(commentLength-2))
            {
                LOGGER.info("【PLC解码器】报文实际长度大于内容长度,开始截取");
                in.resetReaderIndex();
                in.readerIndex(in.readerIndex()-2);
                ByteBuf msgContent = in.readBytes(commentLength);
                in.discardReadBytes();
                LOGGER.info("【PLC解码器】已截取到需要的报文");
                out.add(msgContent);
            }else
            {
                LOGGER.info("【PLC解码器】报文实际长度小于需要长度");
                return;
            }
        }
    }
}
