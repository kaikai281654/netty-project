package cn.dakaizi.netty.c3;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.dakaizi.netty.c1.ByteBufferUtil.debugAll;

@Slf4j
public class MultiThreadServer {

    static class worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile boolean signal=false;
        private ConcurrentLinkedQueue<Runnable> queue=new ConcurrentLinkedQueue<>();

        public worker(String name){
            this.name=name;
        }

        public void register(SocketChannel sc) throws IOException {
            if(!signal){

                thread=new Thread(this,name);
                selector=Selector.open();
                thread.start();
                signal=true;
            }
            //向队列添加任务
            queue.add(()->{
                try {
                    sc.register(selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup();//唤醒selector
        }


        @Override
        public void run() {
            while(true){
                try {
                    selector.select();
                    Runnable poll = queue.poll();
                    if(poll!=null){
                        poll.run();//执行注册
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key= iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("readed...{}",channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }



    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("大凯子的boss线程");
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.configureBlocking(false);


        Selector boss=Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);


        ssc.bind(new InetSocketAddress(8080));
        //创建固定数量的worker
        worker[] workers = new worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i]=new worker("worker-"+i);
        }
//        worker worker = new worker("worker-0");
        AtomicInteger index =new AtomicInteger();
        while(true){
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while(iter.hasNext()){
                SelectionKey key= iter.next();
                iter.remove();
                if(key.isAcceptable()){
                    SocketChannel sc=ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connect。。{}",sc.getRemoteAddress());
                    //关联
                    log.debug("before Register..{}",sc.getRemoteAddress());
                    workers[index.getAndIncrement()% workers.length].register(sc);
                    log.debug("after Register..{}",sc.getRemoteAddress());

                }
            }
        }
    }



}
