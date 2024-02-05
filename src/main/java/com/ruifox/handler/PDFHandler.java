package com.ruifox.handler;

import com.ruifox.handler.base.FileHandler;
import com.ruifox.handler.base.FileRequest;
import com.ruifox.util.pdf.PDFToHtml;
import org.apache.poi.poifs.filesystem.FileMagic;

public class PDFHandler extends FileHandler {
    @Override
    protected boolean canHandle(FileRequest request) {
        return request.getFileMagic().equals(FileMagic.PDF);
    }

    @Override
    protected String processRequest(FileRequest request) {
        try {
            System.out.println("进入.pdf文件处理器,文件可处理情况:"+canHandle(request));
            return PDFToHtml.inputDocPath(request.getFilePath());
        } catch (Exception e) {
            return "文件读取错误";
        }
    }
}
