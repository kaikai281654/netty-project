package cn.RPC.client;

import cn.RPC.Message.RpcRequestMessage;
import cn.RPC.Message.RpcResponseMessage;
import cn.RPC.handler.RpcResponseMessageHandler;
import cn.RPC.protocol.SequenceIdGenerator;
import cn.RPC.server.service.helloService;
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
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

@Slf4j
public class SuperRpcClient {

    public static void main(String[] args) {

        helloService service=getProxyService(helloService.class);
        System.out.println(service.sayHello("zhangsan"));
//        System.out.println(service.sayHello("lisi"));

    }


    //创建代理来屏蔽发送消息的封装过程
    public static <T> T getProxyService(Class<T> serviceClass){
        ClassLoader loader=serviceClass.getClassLoader();
        Class[] interfaces=new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            //将方法的调用转换为消息对象
            int SequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(
                    SequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            //将消息对象发送出去
            getChannel().writeAndFlush(rpcRequestMessage);
            //准备空的promise对象来接受结果    指定promise对象接收结果线程
            DefaultPromise<Object> defaultPromise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISES.put(SequenceId,defaultPromise);

            //如果想异步调用拿到结果的话可以用promis的addlsitenler方法


            //等待promise的结果
            defaultPromise.await();
            if (defaultPromise.isSuccess()){
                //调用正常
                return defaultPromise.getNow();
            }else{
                //调用失败
                throw  new RuntimeException(defaultPromise.cause());
            }
        });
            return (T)o;

    }

    public static Channel channel=null;
    public static final Object sycObject=new Object();

    public static  Channel getChannel(){
        if(channel!=null){
            return channel;
        }
        synchronized (sycObject){
            if(channel!=null){
                return channel;
            }
            initChannel();
            return channel;
        }
    }


    private static void initChannel() {
        NioEventLoopGroup group= new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler();
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();

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
        try {
            channel = bootstrap.connect("localhost",8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.debug("server erro",e);
        }
    }
}
