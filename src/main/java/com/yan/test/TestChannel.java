package com.yan.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 通道：用于源节点与目标节点的连接，负责数据的传输，与缓冲区配合。
 */
public class TestChannel {
    private static Logger logger = LoggerFactory.getLogger(TestChannel.class);

    /**
     * 利用非直接缓冲区进行文件复制
     */
    @Test
    public void test1() {
        long start = System.currentTimeMillis();

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        FileChannel fileInputChannel = null;
        FileChannel fileOutputChannel = null;

        try {
            fileInputStream = new FileInputStream("doc/1.png");
            fileOutputStream = new FileOutputStream("doc/2.png");

            fileInputChannel = fileInputStream.getChannel();
            fileOutputChannel = fileOutputStream.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            while (fileInputChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                fileOutputChannel.write(byteBuffer);
                byteBuffer.clear();
            }
        } catch (IOException e) {
            logger.info("read or write error", e);
        } finally {
            if (fileOutputChannel != null) {
                try {
                    fileOutputChannel.close();
                } catch (IOException e) {
                    logger.info("close fileOutputChannel error", e);
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    logger.info("close fileOutputStream error", e);
                }
            }
            if (fileInputChannel != null) {
                try {
                    fileInputChannel.close();
                } catch (IOException e) {
                    logger.info("close fileInputChannel error", e);
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    logger.info("close fileInputStream error", e);
                }
            }
        }

        long end = System.currentTimeMillis();
        logger.info("spent={}ms", (end - start));
    }

    /**
     * 利用内存映射文件进行文件复制
     */
    @Test
    public void test2() throws IOException {
        long start = System.currentTimeMillis();

        FileChannel fileInputChannel = FileChannel.open(Paths.get("doc", "1.png"), StandardOpenOption.READ);
        FileChannel fileOutputChannel = FileChannel.open(Paths.get("doc", "3.png"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);

        MappedByteBuffer inputMappedByteBuffer = fileInputChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileInputChannel.size());
        MappedByteBuffer outputMappedByteBuffer = fileOutputChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileInputChannel.size());

        byte[] dst = new byte[inputMappedByteBuffer.limit()];
        inputMappedByteBuffer.get(dst);
        outputMappedByteBuffer.put(dst);

        fileOutputChannel.close();
        fileOutputChannel.close();

        long end = System.currentTimeMillis();
        logger.info("spent={}ms", (end - start));
    }
}
