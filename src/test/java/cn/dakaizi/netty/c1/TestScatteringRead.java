package cn.dakaizi.netty.c1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.RandomAccess;

import static cn.dakaizi.netty.c1.ByteBufferUtil.debugAll;

//分散读
public class TestScatteringRead {
    public static void main(String[] args) {
        try (FileChannel r = new RandomAccessFile("d:\\ab.txt", "r").getChannel()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(2);
            ByteBuffer byteBuffer1 = ByteBuffer.allocate(2);
            ByteBuffer byteBuffer2 = ByteBuffer.allocate(3);

            r.read(new ByteBuffer[]{byteBuffer,byteBuffer1,byteBuffer2});
            //切换为读模式
            byteBuffer.flip();
            byteBuffer1.flip();
            byteBuffer2.flip();
            //利用工具类进行读写
            debugAll(byteBuffer);
            debugAll(byteBuffer1);
            debugAll(byteBuffer2);


        } catch (IOException e) {
        }


    }
}
