package com.xmutca.nio.c05multithreading;

import static com.xmutca.nio.c01bytebuffer.ByteBufferUtil.debugAll;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.22
 */
@Slf4j
public class MultiThreadServer3 {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        final Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        //1. 创建固定数量的 Worker, 并初始化
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }

        AtomicInteger index = new AtomicInteger();
        while (true) {
            selector.select();
            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                final SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    final SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connected...{}", sc.getRemoteAddress());
                    //2. 关联 selector
                    log.debug("before register...{}", sc.getRemoteAddress());
                    // round robin 轮训
                    workers[index.getAndIncrement() % workers.length].register(sc);//boss 线程调用 初始化 selector， 启动 worker-0
                    log.debug("after register...{}", sc.getRemoteAddress());

                }

            }
        }

    }

    static class Worker implements Runnable {

        private Thread thread;
        private Selector selector;
        private String name;
        private boolean start = false;//还未初始化
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        /**
         * 初始化线程和 selector
         */
        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                selector = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                start = true;
            }
            //向队列添加了任务，单这个任务并没有立刻执行
            queue.add(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup();//唤醒selector

        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    selector.select();// worker-0 阻塞，wakeup
                    final Runnable task = queue.poll();
                    if (task != null) {
                        task.run();//这个位置执行了  sc.register(selector, SelectionKey.OP_READ, null);
                    }

                    final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        final SelectionKey key = iterator.next();
                        iterator.remove();

                        if (key.isReadable()) {
                            final ByteBuffer buffer = ByteBuffer.allocate(16);
                            final SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("read...{}", channel.getRemoteAddress());
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

}
