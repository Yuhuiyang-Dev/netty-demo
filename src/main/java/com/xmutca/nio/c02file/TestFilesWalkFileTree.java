package com.xmutca.nio.c02file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * 目录文件遍历
 *
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.21
 */
@Slf4j
public class TestFilesWalkFileTree {

    public static void main(String[] args) throws IOException {
        findDemo1();
        findDemo2();
//        deleteDemo();
    }

    /**
     * 递归删除目录文件
     *
     * @throws IOException IOException
     */
    private static void deleteDemo() throws IOException {
        Files.walkFileTree(Paths.get("E:\\tc"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.debug("====> 进入{}", dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.debug("{}", file);
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                log.debug("====> 退出{}", dir);
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    private static void findDemo2() throws IOException {
        final AtomicInteger jarCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("C:\\Program Files\\Java\\jdk1.8.0_191"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")) {
                    log.debug("{}", file);
                    jarCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        log.debug("jar count: {}", jarCount);
    }

    private static void findDemo1() throws IOException {
        final AtomicInteger dirCount = new AtomicInteger();
        final AtomicInteger fileCount = new AtomicInteger();

        Files.walkFileTree(Paths.get("C:\\Program Files\\Java\\jdk1.8.0_191"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.debug("===> {}", dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.debug("{}", file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });

        log.debug("dir count {}", dirCount);
        log.debug("file count {}", fileCount);
    }

}
