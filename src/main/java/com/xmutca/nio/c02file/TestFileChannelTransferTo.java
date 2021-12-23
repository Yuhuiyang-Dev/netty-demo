package com.xmutca.nio.c02file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 传输效率高(0拷贝)，上线一次2G
 *
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.21
 */
@Slf4j
public class TestFileChannelTransferTo {

    public static void main(String[] args) throws IOException {

        try (final FileChannel from = new FileInputStream("data.txt").getChannel();
            final FileChannel to = new FileOutputStream("to.txt").getChannel()) {

            final long size = from.size();
            long left = size;
            while (left > 0) {
                log.debug("position: {} left: {}", (size - left), left);
                left -= from.transferTo(size - left, left, to);
            }

        }

    }

}
