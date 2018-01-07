package com.yan.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;

public class TestPath {
    private static Logger logger = LoggerFactory.getLogger(TestPath.class);

    /**
     * 获取目录，及取得路径的各种信息
     */
    @Test
    public void test1() {
        Path path = Paths.get(System.getProperty("user.home"), "Documents", "Downloads");
        logger.info("toString={}", path.toString());
        logger.info("getFileName={}", path.getFileName());
        logger.info("getName(0)={}", path.getName(0));
        logger.info("getNameCount={}", path.getNameCount());
        logger.info("subpath(0, 2)={}", path.subpath(0, 2));
        logger.info("getParent={}", path.getParent());
        logger.info("getRoot={}", path.getRoot());
    }

    /**
     *
     */
    @Test
    public void test2() {
        logger.info("=========================relativePath to absolutePath======================");
        Path relativePath = Paths.get("doc", "1.png");
        logger.info("relativePath={}", relativePath);
        Path absolutePath = relativePath.toAbsolutePath();
        logger.info("absolutePath={}", absolutePath);

        logger.info("=========================resolve path======================");
        Path path1 = Paths.get(System.getProperty("user.home"));
        Path path2 = Paths.get("Documents");
        Path path3 = path1.resolve(path2);
        logger.info("resolvePath={}", path3);

        logger.info("=========================relativize path======================");
        Path p1 = Paths.get(System.getProperty("user.home"));
        Path p2 = Paths.get("C:\\Program Files");
        Path p1ToP2 = p1.relativize(p2);
        logger.info("relativizePath={}", p1ToP2);

        logger.info("=========================equals path======================");
        Path userHomePath1 = Paths.get(System.getProperty("user.home"));
        Path userHomePath2 = Paths.get("C:\\Users\\Administrator");
        logger.info("equals={}", userHomePath1.equals(userHomePath2));

        logger.info("=========================file exists======================");
        Path doc = Paths.get("doc");
        boolean fileExists = Files.exists(doc);
        logger.info("fileExists={}", fileExists);
    }

    /**
     * 属性读取和设定
     */
    @Test
    public void test3() throws IOException {
        Path path = Paths.get("C:\\Windows");
        BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
        logger.info("creationTime={}", basicFileAttributes.creationTime());
        logger.info("lastAccessTime={}", basicFileAttributes.lastAccessTime());
        logger.info("lastModifiedTime={}", basicFileAttributes.lastModifiedTime());
        logger.info("isDirectory={}", basicFileAttributes.isDirectory());
        logger.info("isOther={}", basicFileAttributes.isOther());
        logger.info("isSymbolicLink={}", basicFileAttributes.isSymbolicLink());
        logger.info("size={}", basicFileAttributes.size());
    }

    /**
     * 获取存储
     */
    @Test
    public void test4() {
        FileSystem fileSystem = FileSystems.getDefault();
        for (FileStore fileStore : fileSystem.getFileStores()) {
            print(fileStore);
        }
    }

    private void print(FileStore fileStore) {
        try {
            long total = fileStore.getTotalSpace();
            long used = fileStore.getTotalSpace() - fileStore.getUnallocatedSpace();
            long usable = fileStore.getUnallocatedSpace();
            DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
            logger.info("store={}", fileStore.toString());
            logger.info("\t-total={}字节", decimalFormat.format(total));
            logger.info("\t-used={}字节", decimalFormat.format(used));
            logger.info("\t-usable={}字节", decimalFormat.format(usable));
        } catch (IOException ioe) {
            logger.info("", ioe);
        }
    }
}
