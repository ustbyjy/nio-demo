package com.yan.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Buffer：负责数据的存取，根据不同的数据类型（除boolean）提供相应类型的缓冲区：ByteBuffer、FloatBuffer、CharBuffer...
 * <p>
 * Buffer通过allocate方法获取缓冲区
 * </p>
 * <p>
 * Buffer类的属性：mark <= position <= limit <= capacity，
 * 1、capacity：容量，缓冲区存放的最大字节数；
 * 2、limit：限制，缓冲区可以操作的最大容量，即limit后的数据不能读写；
 * 3、position：位置，缓冲区正在操作的数据的位置；
 * 4、mark：标记，可以标记当前position的位置，可以通过reset()恢复到mark的位置；
 * </p>
 */
public class TestBuffer {
    private static Logger logger = LoggerFactory.getLogger(TestBuffer.class);

    @Test
    public void test1() {
        String str = "abcde";

        // 1、分配一个指定大小的缓冲区，capacity：单位bytes
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        logger.info("=====================allocate=======================");
        logger.info("capacity={}，limit={}，position={}", byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position());

        // 2、存入数据到缓冲区
        byteBuffer.put(str.getBytes());
        logger.info("=====================put=======================");
        logger.info("capacity={}，limit={}，position={}", byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position());

        // 3、切换成读模式
        byteBuffer.flip();
        logger.info("=====================flip=======================");
        logger.info("capacity={}，limit={}，position={}", byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position());

        // 4、从缓冲区读取数据
        byte[] data = new byte[byteBuffer.limit()];
        byteBuffer.get(data);
        logger.info("=====================get=======================");
        logger.info("str={}", new String(data));
        logger.info("capacity={}，limit={}，position={}", byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position());

        // 5、倒回，可重复读取数据
        byteBuffer.rewind();
        logger.info("=====================rewind=======================");
        logger.info("capacity={}，limit={}，position={}", byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position());

        // 6、清空缓冲区，但是缓冲区的字节依然存在，处于“被遗忘”状态，因为limit已经复位
        byteBuffer.clear();
        logger.info("=====================clear=======================");
        logger.info("capacity={}，limit={}，position={}", byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position());

        // 7、测试清空缓冲区并没有清除数据
        logger.info("=====================get after clear=======================");
        logger.info("char={}", (char) byteBuffer.get());
        logger.info("capacity={}，limit={}，position={}", byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position());
    }

    @Test
    public void test2() {
        String str = "abcde";

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        byteBuffer.put(str.getBytes());

        byteBuffer.flip();

        byte[] data = new byte[byteBuffer.limit()];
        byteBuffer.get(data, 0, 2);
        logger.info("str={}", new String(data, 0, 2));
        logger.info("capacity={}，limit={}，position={}", byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position());

        // 标记position位置
        byteBuffer.mark();

        byteBuffer.get(data, 2, 2);
        logger.info("str={}", new String(data, 2, 2));
        logger.info("capacity={}，limit={}，position={}", byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position());

        // 恢复到mark标记的位置
        byteBuffer.reset();
        logger.info("capacity={}，limit={}，position={}", byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position());

        // 判断position后是否还有数据，若有则获得数量
        if (byteBuffer.hasRemaining()) {
            logger.info("remaining={}", byteBuffer.remaining());
        }
    }
}
