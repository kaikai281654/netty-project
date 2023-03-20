package cn.dakaizi.nettyReal;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        //起动器
        new Bootstrap()
                //添加eventloop
                .group(new NioEventLoopGroup())
                //选择客户端channel实现
                .channel(NioSocketChannel.class)
                //添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    //建立连接后被调用
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new StringEncoder());
                    }
                })
                //连接到服务器 connect方法为异步非阻塞类型，调用线程为main 真正执行connect方法的线程是nio线程
                .connect(new InetSocketAddress("localhost",8080))

                .sync()//阻塞方法，直到连接建立
                .channel()//代表连接对象
                //向服务端发送数据
                .writeAndFlush("helloWord");//发送数据
    }
}
