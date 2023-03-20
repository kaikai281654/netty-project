package cn.kai.protocol;

import cn.kai.config.Config;
import cn.kai.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@ChannelHandler.Sharable
@Slf4j
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        //6字节的魔数
        out.writeBytes(new byte[]{1,2,3,4});
        //1字节的版本
        out.writeByte(0);
        //1字节的序列化方式 0,jdk 1,json
        out.writeByte(Config.getMySerializerAlgorithm().ordinal());
        //1字节的指令类型
        out.writeByte(msg.getMessageType());
        //4字节的请求序号
        out.writeInt(msg.getSequenceId());
        //1个字节的无意义凑数字节 对齐填充
        out.writeByte(0xff);


        //获取对象的字节数组

        byte[] serialize = Config.getMySerializerAlgorithm().serialize(msg);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream oos=new ObjectOutputStream(bos);
//        oos.writeObject(msg);
//        byte[] bytes = bos.toByteArray();

        //写入长度
        out.writeInt(serialize.length);
        //写入内容
        out.writeBytes(serialize);
        outList.add(out);
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();        // 大端4字节的 魔数

        byte version = in.readByte();       // 版本

        byte serializerType = in.readByte();

        byte messageType = in.readByte();

        int sequenceId = in.readInt();
        in.readByte();

        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        // 处理内容
        //找到序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerType];
        //确定具体消息类型
        Class<?> messageClass = Message.getMessageClass(messageType);
        Object message = algorithm.deserialize(messageClass, bytes);


//        final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
//        final ObjectInputStream ois = new ObjectInputStream(bis);

        // 转成 Message类型
//        Message message = (Message) ois.readObject();

        log.debug("{},{},{},{},{},{}",magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);


        // 将message给下一个handler使用
        out.add(message);
    }
}
