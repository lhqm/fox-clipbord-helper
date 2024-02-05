package com.ruifox.handler;

import com.ruifox.handler.base.FileHandler;
import com.ruifox.handler.base.FileRequest;
import com.ruifox.util.doc.DocToHtml;
import org.apache.poi.poifs.filesystem.FileMagic;

public class DOCHandler extends FileHandler {
    @Override
    protected boolean canHandle(FileRequest request) {
        return request.getFileMagic().equals(FileMagic.OLE2);
    }

    @Override
    protected String processRequest(FileRequest request) {
        try {
            return DocToHtml.inputDocPath(request.getFilePath());
        } catch (Exception e) {
            return "文件读取错误";
        }
    }
}
