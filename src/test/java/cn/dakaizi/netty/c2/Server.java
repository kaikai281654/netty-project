package cn.dakaizi.netty.c2;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static cn.dakaizi.netty.c1.ByteBufferUtil.debugRead;
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {

        //创建selector,管理多个channal
        Selector selector = Selector.open();

        //使用nio来理解阻塞模式 单线程
        ByteBuffer allocate = ByteBuffer.allocate(16);

        //创建服务器
        ServerSocketChannel ssc= ServerSocketChannel.open();
        ssc.configureBlocking(false);//非阻塞模式


        //建立selector和channel的联系,SelectionKey是事件发生后，通过它可以知道事件和哪个channel的事件
        SelectionKey ssckey = ssc.register(selector, 0, null);
        ssckey.interestOps(SelectionKey.OP_ACCEPT);


        //绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        //accept,建立与客户端的链接,SocketChannel用来与客户端通信
        List<SocketChannel> socketChannels=new ArrayList<>();
        while(true){
            SocketChannel sc = ssc.accept();//阻塞方法,线程停止
            if(sc!=null){
                log.debug("connected..{}",sc);
                sc.configureBlocking(false);
                socketChannels.add(sc);
            }

            //接收客户端发送的数据
            for (SocketChannel channel:socketChannels
                 ) {

                int read = channel.read(allocate);//阻塞方法,线程停止

                if(read>0){
                    allocate.flip();
                    debugRead(allocate);
//                allocate.compact();
                    allocate.clear();
                    log.debug("readed...{}",channel);
                }
            }

        }

    }
}
