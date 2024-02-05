package com.ruifox.handler;

import com.ruifox.handler.base.FileHandler;
import com.ruifox.handler.base.FileRequest;
import com.ruifox.util.doc.DocxToHtml;
import org.apache.poi.poifs.filesystem.FileMagic;

public class DOCXHandler extends FileHandler {
    @Override
    protected boolean canHandle(FileRequest request) {
        return FileMagic.OOXML.equals(request.getFileMagic());
    }

    @Override
    protected String processRequest(FileRequest request) {
        try {
            System.out.println("进入.docx文件处理器,文件可处理情况:"+canHandle(request));
            return DocxToHtml.inputDocxPath(request.getFilePath());
        } catch (Exception e) {
            return "文件读取错误";
        }
    }
}
