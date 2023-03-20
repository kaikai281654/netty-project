package cn.dakaizi.netty.c1;

import java.nio.ByteBuffer;

import static cn.dakaizi.netty.c1.ByteBufferUtil.debugAll;

public class TestByteBufferExam {
    public static void main(String[] args) {

        ByteBuffer source =ByteBuffer.allocate(32);
        source.put("Hello,world\nI' m zhangsan\nHo".getBytes());
        split(source);
        source.put("w sre you?\n".getBytes());
        split(source);
    }



    private static void split(ByteBuffer source){
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) =='\n') {
                //找到完整消息,将消息传入新的byteBuffer
                int length= i+1- source.position() ;
                ByteBuffer target=ByteBuffer.allocate(length);
                //从source读，向target写入
                for (int j = 0; j < length; j++) {
                   target.put(source.get()) ;
                }
                debugAll(target);
            }
        }
        source.compact();
    }







}
