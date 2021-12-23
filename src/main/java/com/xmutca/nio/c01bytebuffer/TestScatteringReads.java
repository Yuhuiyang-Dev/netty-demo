package com.xmutca.nio.c01bytebuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.17
 */
@Slf4j
public class TestScatteringReads {

    public static void main(String[] args) {

        try (final FileChannel channel = new RandomAccessFile("words.txt", "r").getChannel()) {
            final ByteBuffer b1 = ByteBuffer.allocate(3);
            final ByteBuffer b2 = ByteBuffer.allocate(3);
            final ByteBuffer b3 = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{b1, b2, b3});
            b1.flip();
            b2.flip();
            b3.flip();
            ByteBufferUtil.debugAll(b1);
            ByteBufferUtil.debugAll(b2);
            ByteBufferUtil.debugAll(b3);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }
}
