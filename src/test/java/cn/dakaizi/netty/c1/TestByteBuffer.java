package cn.dakaizi.netty.c1;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {

        try (FileChannel channel = new FileInputStream("d:\\a.txt").getChannel()) {
            //准备缓冲区
            ByteBuffer allocate = ByteBuffer.allocate(10);

            while(true){
                int read = channel.read(allocate);
                log.debug("读取到的字节{}" ,read);

                if(read==-1){
                    break;
                }
                //打印独到的流
                allocate.flip();//切换至读模式
                while(allocate.hasRemaining()){
                    byte b = allocate.get();
                    log.debug("实际字节{}" ,(char)b);

                }
                allocate.clear();//切换为写模式 将position指针指向队列开头

            }


        } catch (IOException e) {
        }







    }


}
