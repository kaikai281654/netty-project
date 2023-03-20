package cn.dakaizi.nettyReal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {
    public static void main(String[] args) {
                //服务器端启动器，负责组装netty组件
                new ServerBootstrap()
                        //
                        .group(new NioEventLoopGroup())
                        //选择服务器的serversocketChannel
                        .channel(NioServerSocketChannel.class)
                        //boss处理链接 ，worker负责处理读写worker（chiled） ，childHander决定了worker能执行那些操作
                        .childHandler(
                                //
                                new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel channel) throws Exception {
                                //添加具体的handler
                                channel.pipeline().addLast(new StringDecoder());//将传过来的bytebuff类型转化为字符串
                                channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {//自定义的handler
                                    @Override//读事件
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        //打印上一步处理好的字符串
                                        System.out.println(msg);
                                    }
                                });
                            }
                        })
                        //绑定监听端口
                        .bind(8080);
    }
}
