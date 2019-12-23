package com.xuecheng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Auther: likui
 * @Date: 2019/6/15 21:30
 * @Description:文件分块合并的测试类
 */
public class TestFile {
    //测试文件分块
    @Test
    public void testChunk() throws IOException {
        //测试文件分块
        File sourceFile = new File("F:\\project\\xcEdu\\video\\lucene.avi");
        //块文件目录
        String chunkFileFloder = "F:\\project\\xcEdu\\video\\chunks\\";
        //定义块文件大小
        long chunkFileSize = 1024 * 1024L;
        //源文件块数
        long chunkFileNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkFileSize);
        //创建读文件对象
        RandomAccessFile file_read = new RandomAccessFile(sourceFile, "r");
        byte[] bytes = new byte[1024];
        for (int i = 0; i < chunkFileNum; i++) {
            //块文件
            File chunkFile = new File(chunkFileFloder + i);
            //创建向块文件写对象
            RandomAccessFile file_write = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = file_read.read(bytes)) != -1) {
                file_write.write(bytes, 0, len);
                if (chunkFile.length() >= chunkFileSize) {
                    break;
                }
            }
            file_write.close();
        }
        file_read.close();
    }

    //测试文件合并
    @Test
    public void testMerge() throws IOException {
        //块文件目录
        String chunkFileFloderPath = "F:\\project\\xcEdu\\video\\chunks\\";
        //块文件目录对象
        File chunkFileFloder = new File(chunkFileFloderPath);
        //块文件列表
        File[] files = chunkFileFloder.listFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                    return 1;
                }
                return -1;
            }
        });
        //合并文件
        File file_merge = new File("F:\\project\\xcEdu\\video\\lucene_merge.avi");
        //创建写对象
        RandomAccessFile write_file = new RandomAccessFile(file_merge, "rw");
        byte[] bytes = new byte[1024];
        for (File file : fileList) {
            RandomAccessFile read_file = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len = read_file.read(bytes)) != -1) {
                write_file.write(bytes, 0, len);
            }
            read_file.close();
        }
        write_file.close();
    }
}