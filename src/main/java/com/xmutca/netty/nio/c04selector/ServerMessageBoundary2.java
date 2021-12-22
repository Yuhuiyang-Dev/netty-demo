package com.xmutca.netty.nio.c04selector;

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
public class ServerMessageBoundary2 {

    public static void main(String[] args) throws IOException {

        //1. 创建 selector, 管理多个 channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        //2. 建立 selector 和 channel 的联系（注册）
        // SelectionKey 就是将来事件发生后，通过它可以知道事件和那个 channel 的事件
        final SelectionKey sscKey = ssc.register(selector, 0, null);
        // key 只关注 accept 事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key:{}", sscKey);

        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            //3. select 方法, 没有事件发生，线程阻塞，有事件，线程才会恢复运行
            // select 在事件未处理时，它不会阻塞, 事件发生后要么处理，要么取消，不能置之不理
            selector.select();
            //4. 处理事件, selectedKeys 内部包含了所有发生的事件
            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();//accept, read
            while (iterator.hasNext()) {
                final SelectionKey key = iterator.next();
                iterator.remove();
                log.debug("key: {}", key);
                //5. 区分事件类型
                if (key.isAcceptable()) {
                    final ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    final SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);//attachment
                    final SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
                    log.debug("scKey:{}", scKey);

                } else if (key.isReadable()) {
                    try {
                        final SocketChannel channel = (SocketChannel) key.channel();
                        //获取 SelectionKey 关联附件
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        final int read = channel.read(buffer);//如果是正常断开，read 方法返回的是 -1
                        if (read == -1) {
                            log.debug("客户端正常断开");
                            key.cancel();
                            continue;
                        }
                        split(buffer);
                        if (buffer.position() == buffer.limit()) {

                            buffer.flip();
                            final ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                            newBuffer.put(buffer);
                            key.attach(newBuffer);
                            log.debug("容量不足，进行扩容 {}", newBuffer.capacity());
                        } else {
                            //自动减容
                            log.debug("缓存区够用，减容至position ({}) 大小,最小为16", buffer.position());
                            buffer.flip();
                            ByteBuffer newBuffer = buffer.limit() > 16 ? ByteBuffer.allocate(buffer.limit()) : ByteBuffer.allocate(16);
                            newBuffer.put(buffer);
                            key.attach(newBuffer);
                            log.debug("实际减容至 {} ", newBuffer.capacity());
                        }

                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        key.cancel();//因为客户端断开了，因此需要将 key 取消（从 selector 的 keys 集合中真正删除 key）
                    }
                }


            }
        }
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            //找到一条完整消息
            if (source.get(i) == '\n') {
                //把这条完整消息存入新的ByteBuffer
                int length = i + 1 - source.position();
                final ByteBuffer target = ByteBuffer.allocate(length);
                //从 source 读，想 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();

    }

}
