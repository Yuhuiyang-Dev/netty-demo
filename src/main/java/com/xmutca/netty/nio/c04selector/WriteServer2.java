package com.xmutca.netty.nio.c04selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.22
 */
@Slf4j
public class WriteServer2 {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        final Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            selector.select();
            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                final SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    final SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    final SelectionKey scKey = sc.register(selector, 0, null);

                    //1. 向客户端发送大量数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }
                    final ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());

                    //2. 返回值代表实际写入的字节数
                    final int write = sc.write(buffer);
                    log.debug("{}", write);

                    //3. 判断是否有剩余内容
                    if (buffer.hasRemaining()) {
                        //4. 关注可写事件
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        //5. 把未写完的数据挂载到 SelectionKey 上
                        scKey.attach(buffer);
                    }

                } else if (key.isWritable()) {
                    final ByteBuffer buffer = (ByteBuffer) key.attachment();
                    final SocketChannel sc = (SocketChannel) key.channel();
                    final int write = sc.write(buffer);
                    log.debug("{}", write);
                    //6. 清理 ByteBuffer
                    if (buffer.hasRemaining()) {
                        key.attach(null);
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);//不再关注可写事件
                    }

                }

            }
        }

    }


}
