package com.xmutca.netty.bytebuffer;

import static com.xmutca.netty.bytebuffer.ByteBufferUtil.debugAll;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.17
 */
public class TestByteBufferString {

    public static void main(String[] args) {
        //1. 字符串转为 ByteBuffer
        final byte[] bytes = "hello".getBytes();

        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put(bytes);
        debugAll(buffer1);

        //2. Charset 这种方式会自动设置为读模式，即 position=0
        final ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);

        //3.wrap
        final ByteBuffer buffer3 = ByteBuffer.wrap(bytes);
        debugAll(buffer3);

        //4. 转为字符串
        final String str1 = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(str1);


        buffer1.flip();
        final String str2 = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(str2);


    }
}
