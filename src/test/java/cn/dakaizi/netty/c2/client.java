package cn.dakaizi.netty.c2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc=SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8082));
        System.out.println("waiting");
    }
}