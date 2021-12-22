package com.xmutca.netty.nio.c04selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.21
 */
@Slf4j
public class WriteClient {

    public static void main(String[] args) throws IOException {

        SocketChannel sc = SocketChannel.open();

        sc.connect(new InetSocketAddress("localhost", 8080));

        //3. 接收数据
        int count = 0;
        while (true) {
            final ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            final int read = sc.read(buffer);
            count += read;
            log.debug("{}", count);
            buffer.clear();
        }

    }

}
