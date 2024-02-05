package com.ruifox.handler;

import com.ruifox.handler.base.FileHandler;
import com.ruifox.handler.base.FileRequest;
import com.ruifox.util.doc.DocxToHtml;
import org.apache.poi.poifs.filesystem.FileMagic;

public class DOCXHandler extends FileHandler {
    @Override
    protected boolean canHandle(FileRequest request) {
        return request.getFileMagic().equals(FileMagic.OOXML);
    }

    @Override
    protected String processRequest(FileRequest request) {
        try {
            return DocxToHtml.inputDocxPath(request.getFilePath());
        } catch (Exception e) {
            return "文件读取错误";
        }
    }
}
