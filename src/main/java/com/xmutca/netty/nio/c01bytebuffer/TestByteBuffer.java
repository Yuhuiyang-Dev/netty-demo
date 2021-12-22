package com.xmutca.netty.nio.c01bytebuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * nio的例子，ByteBuffer的基本使用
 *
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2021.12.14
 */
@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        //FileChannel
        //1.输入输出流 2. RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            while (true) {
                int len = channel.read(byteBuffer);
                log.debug("读到的字节数 {}", len);
                if (len == -1) {
                    break;
                }
                byteBuffer.flip();//切换至读模式(limit = position; position = 0; mark = -1;)
                while (byteBuffer.hasRemaining()) {
                    byte b = byteBuffer.get();
                    log.debug("读到的字符 {}", (char) b);
                }
                byteBuffer.clear();//切换至写模式(position = 0; limit = capacity; mark = -1;)
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
