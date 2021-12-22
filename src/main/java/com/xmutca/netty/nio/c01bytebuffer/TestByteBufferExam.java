package com.xmutca.netty.nio.c01bytebuffer;

import static com.xmutca.netty.nio.c01bytebuffer.ByteBufferUtil.debugAll;

import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.17
 */
public class TestByteBufferExam {

    public static void main(String[] args) {
       /*
        网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
        但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
            Hello,world\n
            I’m Nyima\n
            How are you?\n
        变成了下面的两个 byteBuffer (粘包，半包)
            Hello,world\nI’m Nyima\nHo
            w are you?\n
        现在要求你编写程序，将错乱的数据恢复成原始的按 \n 分隔的数据
        */

        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm zhansan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);

    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            //找到一条完整消息
            if (source.get(i) == '\n') {
                //把这条完整消息存入新的ByteBuffer
                int length = i + 1 - source.position();
                final ByteBuffer target = ByteBuffer.allocate(length);
                //从 source 读，想 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();

    }

}
