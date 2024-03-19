package com.ruifox.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author 离狐千慕
 * @version 1.0
 * @date 2024/1/11 16:11
 * 文件列表初始化类。该类用于程序在重启后删掉本地的所有中转文件，以避免意外情况下文件未被正常删除的情况
 */
public class RunDir {
    public static final String directoryPath = "./files";
    static Logger log = LoggerFactory.getLogger(RunDir.class);
    public static void init() {

        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            // 如果目录存在，则删除目录
            deleteDirectory(directory);
        }

        // 创建目录
        boolean created = directory.mkdirs();
        if (created) {
            log.info("文件临时目录已初始化");
        } else {
            log.error("文件临时目录初始化失败");
        }
    }

    // 递归删除目录及其内容
    public static void deleteDirectory(File directory) {
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
