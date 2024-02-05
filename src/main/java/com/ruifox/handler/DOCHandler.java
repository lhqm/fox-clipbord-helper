package com.ruifox.handler;

import com.ruifox.handler.base.FileHandler;
import com.ruifox.handler.base.FileRequest;
import com.ruifox.util.doc.DocToHtml;
import org.apache.poi.poifs.filesystem.FileMagic;

public class DOCHandler extends FileHandler {
    @Override
    protected boolean canHandle(FileRequest request) {
        return FileMagic.OLE2.equals(request.getFileMagic());
    }

    @Override
    protected String processRequest(FileRequest request) {
        try {
            System.out.println("进入.doc文件处理器,文件可处理情况:"+canHandle(request));
            return DocToHtml.inputDocPath(request.getFilePath());
        } catch (Exception e) {
            return "文件读取错误";
        }
    }
}
