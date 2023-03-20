package cn.dakaizi.netty.c2;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

@Slf4j
public class Server1 {
    public static void main(String[] args) throws IOException {

        //创建selector,管理多个channal
        Selector selector = Selector.open();
        ServerSocketChannel ssc= ServerSocketChannel.open();
        ssc.configureBlocking(false);//非阻塞模式


        //建立selector和channel的联系,SelectionKey是事件发生后，通过它可以知道事件和哪个channel的事件
        SelectionKey ssckey = ssc.register(selector, 0, null);
        ssckey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register ket:{}",ssckey);

        //绑定监听端口
        ssc.bind(new InetSocketAddress(8082));
        while(true){
            //调用selector 的select方法,没有事件发生，线程阻塞，有事件发生，线程运行
            selector.select();
            //处理事件，集合内部包含所有发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey next = iterator.next();
                log.debug("key:{}",next);
                ServerSocketChannel channel = (ServerSocketChannel) next.channel();
                SocketChannel sc = channel.accept();
                log.debug("{}",sc);
            }

        }

    }
}
