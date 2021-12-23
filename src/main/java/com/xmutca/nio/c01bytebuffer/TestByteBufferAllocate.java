package com.xmutca.nio.c01bytebuffer;

import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2021.12.15
 */
@Slf4j
public class TestByteBufferAllocate {

    public static void main(String[] args) {
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
//        class java.nio.HeapByteBuffer  - java 堆内存，读写效率较低，受到GC影响
//        class java.nio.DirectByteBuffer - 直接内存，读写效率高（少一次拷贝），不会受GC影响，分配内存的效率低，使用不当可能造成内存泄露
    }

}
