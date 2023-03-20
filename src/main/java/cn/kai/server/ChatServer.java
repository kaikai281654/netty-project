package cn.kai.server;

import cn.kai.protocol.MessageCodecSharable;
import cn.kai.protocol.ProcotolFrameDecoder;
import cn.kai.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup  boss= new NioEventLoopGroup();
        NioEventLoopGroup  worker= new NioEventLoopGroup();
        
        //netty自带handler
        LoggingHandler LOGGING_HANDLER =new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        
        //自定义消息handler
        LoginRequestMessageHandler LOGGING_REQUESTMESSAGE_HANDLER = new LoginRequestMessageHandler();

        ChatRequestMessageHandler CHAT_REQUESTMESSAGE_HANDLER = new ChatRequestMessageHandler();

        GroupCreatRequestMessageHandler GROUP_CREAT_REQUESTMESSAGE_HANDLER = new GroupCreatRequestMessageHandler();

        GroupJoinRequestMessageHandler GROUPJOIN_REQUESTMESSAGE_HANDLER = new GroupJoinRequestMessageHandler();

        GroupMenbersRequestMessageHandler GROUPMENBERS_REQUESTMESSAGE_HANDLER = new GroupMenbersRequestMessageHandler();

        GroupQuitRequestMessageHandler GROUPQUIT_REQUESTMESSAGE_HANDLER = new GroupQuitRequestMessageHandler();

        GroupChatRequestMessageHandler GROUP_CHAT_REQUESTMESSAGE_HANDLER = new GroupChatRequestMessageHandler();

        QuitHandler QUIT_HANDLER = new QuitHandler();


        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss,worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {

                    ch.pipeline().addLast(new ProcotolFrameDecoder());//不能共享
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);

                    //用来判断是否读写时间过长
                    //5s内如果没有收到channel 的数据，会触发一个事件:
                    //{@link IdleState#READER_IDLE}
                    ch.pipeline().addLast(new IdleStateHandler(5,0,0));


                    //ChannelDuplexHandler既可以作为出站处理器也可以当作入站处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        //用来出发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event=(IdleStateEvent) evt;
                            //出发了读空闲事件
                            if (event.state()== IdleState.READER_IDLE) {
                                log.debug("读空闲超过5s");
                                ctx.channel().close();
                            }
                            super.userEventTriggered(ctx, evt);
                        }
                    });



                    //登陆消息的处理器
                    ch.pipeline().addLast(LOGGING_REQUESTMESSAGE_HANDLER);
                    ch.pipeline().addLast(CHAT_REQUESTMESSAGE_HANDLER);
                    ch.pipeline().addLast(GROUP_CREAT_REQUESTMESSAGE_HANDLER);
                    ch.pipeline().addLast(GROUPJOIN_REQUESTMESSAGE_HANDLER);
                    ch.pipeline().addLast(GROUPMENBERS_REQUESTMESSAGE_HANDLER);
                    ch.pipeline().addLast(GROUPQUIT_REQUESTMESSAGE_HANDLER);
                    ch.pipeline().addLast(GROUP_CHAT_REQUESTMESSAGE_HANDLER);
                    ch.pipeline().addLast(QUIT_HANDLER);
                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.debug("server erro{}",e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }


    }

}
