package com.xmutca.netty.nio.c01bytebuffer;

import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2021.12.14
 */
public class TestByteBufferReadWrite {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        buffer.put((byte) 0x61);
        ByteBufferUtil.debugAll(buffer);

        buffer.put(new byte[]{0x62, 0x63, 0x64});
        ByteBufferUtil.debugAll(buffer);

//        System.out.println(buffer.get());
        buffer.flip();
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);

        buffer.compact();//compact切换模式，此时position及其后面的数据被压缩到ByteBuffer前面去了,( (新)position = limit - position; limit = capacity)
        ByteBufferUtil.debugAll(buffer);


        buffer.put(new byte[]{0x65, 0x66});
        ByteBufferUtil.debugAll(buffer);

    }

}
