package cn.dakaizi.netty.c1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
//传输大文件
public class TestFileChannelTransferTo {
    public static void main(String[] args) throws FileNotFoundException {
        try (
                FileChannel channel = new FileInputStream("word2.txt").getChannel();
                FileChannel channel1= new FileOutputStream("to.txt").getChannel();

        ) {
            long size=channel.size();
            for(long left =size;left>0;){
                left-=channel.transferTo(size-left, size, channel1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
