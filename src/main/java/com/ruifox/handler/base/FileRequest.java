package com.ruifox.handler.base;

import org.apache.poi.poifs.filesystem.FileMagic;

// 请求类
public class FileRequest {
    private FileMagic fileMagic;
    private String filePath;
 
    public FileRequest(FileMagic fileMagic, String filePath) {
        this.fileMagic = fileMagic;
        this.filePath = filePath;
    }
 
    public FileMagic getFileMagic() {
        return fileMagic;
    }
 
    public String getFilePath() {
        return filePath;
    }
}