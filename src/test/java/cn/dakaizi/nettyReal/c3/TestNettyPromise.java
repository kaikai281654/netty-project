package cn.dakaizi.nettyReal.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoop group = new NioEventLoopGroup().next();
        //主动创建promis对象,结果的容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(group);
        new Thread(()->{
            //任意线程执行计算完毕后向promis填充结果
            log.debug("开始计算");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            promise.setSuccess(80);
        },"dakaizi").start();

        //主线程获取计算结果
        log.debug("等待结果");
        log.debug("计算结果是{}",promise.get());
    }
}
