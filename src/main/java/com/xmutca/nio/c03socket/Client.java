package com.xmutca.nio.c03socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
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


        log.debug("waiting...");

    }

}
