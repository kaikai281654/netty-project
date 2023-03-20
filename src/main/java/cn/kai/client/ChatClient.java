package cn.kai.client;

import cn.kai.message.*;
import cn.kai.protocol.MessageCodecSharable;
import cn.kai.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER =new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());//不能共享
//                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);

                    //用来判断是否读写时间过长
                    //3s内如果没有向服务器写入channel 的数据，会触发一个事件:
                    //{@link IdleState#WRITER_IDLE}
                    ch.pipeline().addLast(new IdleStateHandler(0,3,0));


                    //ChannelDuplexHandler既可以作为出站处理器也可以当作入站处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        //用来出发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event=(IdleStateEvent) evt;
                            //出发了读空闲事件
                            if (event.state()== IdleState.WRITER_IDLE) {
//                                log.debug("超过三秒未向服务器写入数据,发送心跳包");
                                ctx.writeAndFlush(new PingMessage());
                            }
                            super.userEventTriggered(ctx, evt);
                        }
                    });


                    ch.pipeline().addLast("clientHandler",new ChannelInboundHandlerAdapter(){


                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            System.out.println(msg);
                            log.debug("msg{}",msg);
                           if ((msg instanceof  LoginResponseMessage)){
                               LoginResponseMessage responseMessage = (LoginResponseMessage) msg;
                               if (responseMessage.isSuccess()) {
                                   LOGIN.set(true);
                               }
                               WAIT_FOR_LOGIN.countDown();
                           }
                        }

                        //连接建立后触发
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //创建线程接受用户输入 负责向服务器发送各种消息
                            new Thread(()->{
                                Scanner scanner=new Scanner(System.in);
                                System.out.println("请输入用户名");
                                String userName=scanner.next();
                                System.out.println("请输入密码");
                                String password= scanner.next();

                                //构造消息对象
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(userName, password);
                                //发送消息
                                ctx.writeAndFlush(loginRequestMessage);


                                System.out.println("等待下一步输入");
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //判断登陆状态
                                if (!LOGIN.get()) {
                                    ctx.channel().close();
                                    return;
                                }

                                while(true){

                                    System.out.println("============ 功能菜单 ============");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = scanner.nextLine();
                                    String[] arry = command.split(" ");
                                    switch (arry[0]){
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(userName,arry[1],arry[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(userName,arry[1],arry[2]));
                                            break;
                                        case "gcreate":
                                            Set<String> set=new HashSet<>(Arrays.asList(arry[2].split(",")));
                                            set.add(userName);//加入当前用户
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(arry[1],set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(arry[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(userName,arry[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(userName,arry[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
                                    }
                                }

                            },"客户端线程").start();
                        }









                    });
                }
            });
            Channel localhost = bootstrap.connect("localhost", 8080).sync().channel();
            //此处连接已经建立


            localhost.closeFuture().sync();
        } catch (Exception e) {
            log.debug("client erro{}",e);
        } finally {
            group.shutdownGracefully();
        }


    }
}
