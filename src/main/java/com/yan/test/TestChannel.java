package com.yan.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

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

    /**
     * 通道之间传输数据（直接缓冲区）
     */
    @Test
    public void test3() throws IOException {
        long start = System.currentTimeMillis();

        FileChannel fileInputChannel = FileChannel.open(Paths.get("doc", "1.png"), StandardOpenOption.READ);
        FileChannel fileOutputChannel = FileChannel.open(Paths.get("doc", "3.png"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);

//        fileInputChannel.transferTo(0, fileInputChannel.size(), fileOutputChannel);
        fileOutputChannel.transferFrom(fileInputChannel, 0, fileInputChannel.size());

        fileOutputChannel.close();
        fileInputChannel.close();

        long end = System.currentTimeMillis();
        logger.info("spent={}ms", (end - start));
    }

    /**
     * 分散和聚集
     *
     * @throws IOException
     */
    @Test
    public void test4() throws IOException {
        RandomAccessFile randomAccessFile1 = new RandomAccessFile("doc/1.txt", "rw");

        FileChannel fileInputChannel = randomAccessFile1.getChannel();

        ByteBuffer byteBuffer1 = ByteBuffer.allocate(100);
        ByteBuffer byteBuffer2 = ByteBuffer.allocate(1024);

        ByteBuffer[] byteBuffers = {byteBuffer1, byteBuffer2};
        fileInputChannel.read(byteBuffers);

        for (ByteBuffer byteBuffer : byteBuffers) {
            byteBuffer.flip();
        }

        logger.info(new String(byteBuffers[0].array(), 0, byteBuffers[0].limit()));
        logger.info(new String(byteBuffers[1].array(), 0, byteBuffers[1].limit()));

        // ============================================================

        RandomAccessFile randomAccessFile2 = new RandomAccessFile("doc/2.txt", "rw");

        FileChannel fileOutputChannel = randomAccessFile2.getChannel();

        fileOutputChannel.write(byteBuffers);

        fileOutputChannel.close();
        fileInputChannel.close();
    }

    /**
     * 字符集编码与解码
     */
    @Test
    public void test5() {
        Map<String, Charset> charsetMap = Charset.availableCharsets();
        for (Map.Entry<String, Charset> entry : charsetMap.entrySet()) {
            logger.info("{}={}", entry.getKey(), entry.getValue());
        }
    }

    /**
     * 字符集
     */
    @Test
    public void test6() throws CharacterCodingException {
        Charset gbk = Charset.forName("GBK");

        CharsetEncoder charsetEncoder = gbk.newEncoder();
        CharsetDecoder charsetDecoder = gbk.newDecoder();

        CharBuffer charBuffer1 = CharBuffer.allocate(1024);
        charBuffer1.put("北京欢迎你！");
        charBuffer1.flip();

        ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer1);

        CharBuffer charBuffer2 = charsetDecoder.decode(byteBuffer);
        logger.info(charBuffer2.toString());
    }
}
