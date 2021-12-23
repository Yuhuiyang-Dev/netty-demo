package com.xmutca.nio.c04selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.21
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws IOException {

        SocketChannel sc = SocketChannel.open();

        sc.connect(new InetSocketAddress("localhost", 8080));

//        sc.write(Charset.defaultCharset().encode("hello\nworld\n"));
        sc.write(Charset.defaultCharset().encode("0123456789abcdef\n"));
        sc.write(Charset.defaultCharset().encode("0123456789abcdef3333\n"));
        sc.write(Charset.defaultCharset().encode("0123456789abcdef3333\n"));
        sc.write(Charset.defaultCharset().encode("0123456789abcdef3333n0123456789abcdef3333n0123456789abcdef3333n0123456789abcdef3333\n"));

        System.in.read();
    }

}
