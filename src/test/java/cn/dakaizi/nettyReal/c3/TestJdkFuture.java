package cn.dakaizi.nettyReal.c3;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
@Slf4j
public class TestJdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        //提交任务
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 50;
            }
        });
        //主线程通过future来与线程池中的线程进行通信获取结果
        log.debug("等待结果");
        log.debug("结果是{}" ,future.get());

    }
}
