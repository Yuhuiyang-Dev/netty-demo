package com.xmutca.netty.nio.bytebuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.17
 */
public class TestGatheringWrite {

    public static void main(String[] args) {
        final ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        final ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        final ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");

        try (final FileChannel channel = new RandomAccessFile("words2.txt", "rw").getChannel()) {
            channel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (IOException e) {
        }


    }

}
