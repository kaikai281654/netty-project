package cn.dakaizi.nettyReal.c1;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLOutput;
import java.util.concurrent.TimeUnit;


@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        //创建事件循环组
        NioEventLoopGroup group = new NioEventLoopGroup(2);//io 事件，普通任务 定时任务
//        DefaultEventLoopGroup group1 = new DefaultEventLoopGroup();//普通任务 定时任务
//        System.out.println(NettyRuntime.availableProcessors());
        //获取下一个事件循环对象 group.next()
        System.out.println(group.next());
        //执行普通任务
        group.next().submit(()->{
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("ok");
        });
        group.next().submit(()->{
            log.debug("打印当前事件循环的线程");
        });
        //执行定时任务
        group.next().scheduleAtFixedRate(()->{
            log.debug("ok");
        },0,1, TimeUnit.SECONDS);


        log.debug("main");

    }
}
