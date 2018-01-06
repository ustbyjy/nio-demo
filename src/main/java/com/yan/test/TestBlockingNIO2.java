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

public class TestBlockingNIO2 {
    private static Logger logger = LoggerFactory.getLogger(TestBlockingNIO2.class);

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

        // 通知服务端完成了数据上传
        socketChannel.shutdownOutput();

        // 接收服务器的反馈
        int len = 0;
        while ((len = socketChannel.read(byteBuffer)) != -1) {
            byteBuffer.flip();
            logger.info(new String(byteBuffer.array(), 0, len));
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

        byteBuffer.put("服务端接收客户端数据成功".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);

        fileChannel.close();
        socketChannel.close();
        serverSocketChannel.close();
    }

}
