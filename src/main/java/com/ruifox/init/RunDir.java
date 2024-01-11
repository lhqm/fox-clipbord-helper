package com.ruifox.init;

import java.io.File;

/**
 * @author 离狐千慕
 * @version 1.0
 * @date 2024/1/11 16:11
 * 文件列表初始化类。该类用于程序在重启后删掉本地的所有中转文件，以避免意外情况下文件未被正常删除的情况
 */
public class RunDir {
    public static final String directoryPath = "./files";
    public static void init() {

        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            // 如果目录存在，则删除目录
            deleteDirectory(directory);
        }

        // 创建目录
        boolean created = directory.mkdirs();
        if (created) {
            System.out.println("目录创建成功");
        } else {
            System.out.println("目录创建失败");
        }
    }

    // 递归删除目录及其内容
    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}
