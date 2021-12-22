package com.xmutca.netty.nio.c05multithreading;

import static com.xmutca.netty.nio.c01bytebuffer.ByteBufferUtil.debugAll;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.22
 */
@Slf4j
public class MultiThreadServer2 {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        final Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        //1. 创建固定数量的 Worker, 并初始化
        final Worker worker = new Worker("worker-0");

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
                    worker.register(sc);//boss 线程调用 初始化 selector， 启动 worker-0
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
            selector.wakeup();//唤醒selector
            try {
                //实事证明此方法行不通，会引起阻塞
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sc.register(selector, SelectionKey.OP_READ, null);

        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    selector.select();// worker-0 阻塞，wakeup

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
