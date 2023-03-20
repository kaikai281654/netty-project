package cn.dakaizi.netty.c1;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class TestFilesWalkFileTree {

    public static void main(String[] args) throws IOException {
        AtomicInteger jarcount=new AtomicInteger();
        Files.walkFileTree(Paths.get("C:\\Program Files\\Java\\jdk1.8.0_291"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(file.toString().endsWith(".jar")){
                    System.out.println(file);
                    jarcount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }

        });
        System.out.println(jarcount);
    }

    private static void m1() throws IOException {
        //原子计数器
        AtomicInteger dircount=new AtomicInteger();
        AtomicInteger filecount=new AtomicInteger();


        Files.walkFileTree(Paths.get("C:\\Program Files\\Java\\jdk1.8.0_291"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>"+dir);
                dircount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>"+file);
                filecount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });

        System.out.println(dircount);
        System.out.println(filecount);
    }
}
