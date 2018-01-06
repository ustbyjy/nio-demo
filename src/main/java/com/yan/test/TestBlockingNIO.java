package com.yan.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Selector：选择器，是SelectableChannel的多路复用器，用于监控SelectableChannel的IO状况
 */
public class TestBlockingNIO {
    private static Logger logger = LoggerFactory.getLogger(TestBlockingNIO.class);

    /**
     * 客户端
     */
    @Test
    public void client() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        FileChannel fileChannel = FileChannel.open(Paths.get("doc", "1.png"), StandardOpenOption.READ);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (fileChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        socketChannel.close();
        fileChannel.close();
    }

    /**
     * 服务端
     */
    @Test
    public void server() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(9898));

        SocketChannel socketChannel = serverSocketChannel.accept();

        FileChannel fileChannel = FileChannel.open(Paths.get("doc", "2.png"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (socketChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        fileChannel.close();
        socketChannel.close();
        serverSocketChannel.close();
    }

}
