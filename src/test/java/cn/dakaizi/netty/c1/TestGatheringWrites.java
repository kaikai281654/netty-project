package cn.dakaizi.netty.c1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

//集中写
public class TestGatheringWrites {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer byteBuffer1 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer byteBuffer2 = StandardCharsets.UTF_8.encode("摸哈");


        try (FileChannel rw = new RandomAccessFile("word2.txt", "rw").getChannel()) {
            rw.write(new ByteBuffer[]{byteBuffer,byteBuffer1,byteBuffer2});
        } catch (IOException e) {
        }

    }
}
