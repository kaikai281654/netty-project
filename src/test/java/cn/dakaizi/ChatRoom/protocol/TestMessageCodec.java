package cn.dakaizi.ChatRoom.protocol;

import cn.dakaizi.ChatRoom.entity.LoginRequestMessage;
import cn.dakaizi.ChatRoom.protocol.MessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

public class TestMessageCodec {
    public static void main(String[] args) throws Exception {

        LoggingHandler LOGGING_HANDLER = new LoggingHandler();

        EmbeddedChannel channel  =new EmbeddedChannel(

                LOGGING_HANDLER,

                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),

                new MessageCodec()

        );

        //encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
//        channel.writeOutbound(message);


        //decode
        ByteBuf buf= ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null,message,buf);


        ByteBuf s1= buf.slice(0, 100);
        ByteBuf s2= buf.slice(100, buf.readableBytes()-100);
        s1.retain();

        //入站
        channel.writeInbound(s1); //relase=0

        channel.writeInbound(s2);


    }



}
