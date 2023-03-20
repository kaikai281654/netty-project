package cn.RPC.client;

import cn.RPC.Message.RpcRequestMessage;
import cn.RPC.handler.RpcRequestMessageHandler;
import cn.RPC.handler.RpcResponseMessageHandler;
import cn.kai.protocol.MessageCodecSharable;
import cn.kai.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        NioEventLoopGroup group= new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler();
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {

                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(rpcResponseMessageHandler);

                }
            });
            Channel channel = bootstrap.connect("localhost",8080).sync().channel();
            ChannelFuture future = channel.writeAndFlush(new RpcRequestMessage(
                    1,
                    "cn.RPC.server.service.helloService",
                    "sayHello",
                    String.class,
                    new Class[]{String.class},
                    new Object[]{"zhangsan!"}
            ));
            future.addListener(promise->{
                if(!promise.isSuccess()){
                    Throwable cause = promise.cause();
                    log.error("error : ", cause);
                }
            });
            channel.closeFuture().sync();



        } catch (Exception e) {
            log.debug("server erro",e);
        } finally {
            group.shutdownGracefully();
        }

    }
}
