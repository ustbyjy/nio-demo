package com.yan.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Selector：选择器，是SelectableChannel的多路复用器，用于监控SelectableChannel的IO状况
 */
public class TestNonBlockingNIO {
    private static Logger logger = LoggerFactory.getLogger(TestNonBlockingNIO.class);

    public static void main(String[] args) throws IOException {
        // 客户端
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                    // 切换成非阻塞IO
                    serverSocketChannel.configureBlocking(false);
                    serverSocketChannel.bind(new InetSocketAddress(9898));

                    // 获取选择器，并将通道注册到选择器上，并指定监听“接收”事件
                    Selector selector = Selector.open();
                    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                    while (selector.select() > 0) {
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey selectionKey = iterator.next();
                            if (selectionKey.isAcceptable()) {
                                SocketChannel socketChannel = serverSocketChannel.accept();
                                // 切换非阻塞IO
                                socketChannel.configureBlocking(false);
                                socketChannel.register(selector, SelectionKey.OP_READ);
                            } else if (selectionKey.isReadable()) {
                                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                int len = 0;
                                while ((len = socketChannel.read(byteBuffer)) > 0) {
                                    byteBuffer.flip();
                                    logger.info(new String(byteBuffer.array(), 0, len));
                                    byteBuffer.clear();
                                }
                            }
                            // 取消选择键
                            iterator.remove();
                        }
                    }
                } catch (IOException ioe) {
                    logger.error("", ioe);
                }
            }
        }).start();

        // 服务端
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

                    // 切换成非阻塞IO
                    socketChannel.configureBlocking(false);

                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                    Scanner scanner = new Scanner(System.in);
                    while (scanner.hasNextLine()) {
                        String str = scanner.nextLine();

                        byteBuffer.put((new Date().toString() + "：" + str).getBytes());
                        byteBuffer.flip();

                        socketChannel.write(byteBuffer);
                        byteBuffer.clear();
                    }

                    socketChannel.close();
                } catch (IOException ioe) {
                    logger.error("", ioe);
                }
            }
        }).start();
    }

}
