package cn.dakaizi.netty.c1;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static cn.dakaizi.netty.c1.ByteBufferUtil.debugAll;

public class TestBufferReadW {
    public static void main(String[] args) {
        ByteBuffer allocate = ByteBuffer.allocate(10);

        allocate.put((byte) 0x97);

        debugAll(allocate);




    }
}
