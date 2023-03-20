package cn.dakaizi.nettyReal.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        //起动器
        Channel localhost = new Bootstrap()
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
                //连接到服务器
                .connect(new InetSocketAddress("localhost", 8080))

                .sync()//阻塞方法，直到连接建立
                .channel();//代表连接对象
//向服务端发送数据
//                .writeAndFlush("helloWord");//发送数据
        System.out.println(localhost);
        System.out.println("");
    }
}
