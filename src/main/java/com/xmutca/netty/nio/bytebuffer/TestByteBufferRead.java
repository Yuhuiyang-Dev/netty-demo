package com.xmutca.netty.nio.bytebuffer;

import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2021.12.15
 */
public class TestByteBufferRead {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        //rewind 从头开始读
        buffer.get(new byte[4]);
        ByteBufferUtil.debugAll(buffer);
        buffer.rewind();//从头开始读 rewind(position=0; mark = -1;)
        System.out.println((char) buffer.get());

        System.out.println("-----------------------------");

        //mark & reset
        //mark 做一个标记，reset 是将 position 重置到 mark 的位置
        buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.mark();//加标记，索引2 的位置
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.reset();//将 position 重置到索引 2
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());

        //get(i) 不会改变 position 位置
        System.out.println((char) buffer.get(2));
        ByteBufferUtil.debugAll(buffer);
    }
}
