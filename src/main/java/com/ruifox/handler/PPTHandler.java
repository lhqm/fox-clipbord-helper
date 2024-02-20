package com.ruifox.handler;

import com.ruifox.handler.base.FileHandler;
import com.ruifox.handler.base.FileRequest;
import com.ruifox.util.ppt.PPTUtil;
import org.apache.poi.poifs.filesystem.FileMagic;

public class PPTHandler extends FileHandler {
    @Override
    protected boolean canHandle(FileRequest request) {
        return (request.getFileMagic().equals(FileMagic.OLE2) || request.getFileMagic().equals(FileMagic.OOXML))
                && (request.getFilePath().endsWith(".ppt") || request.getFilePath().endsWith(".pptx"));
    }

    @Override
    protected String processRequest(FileRequest request) {
        return PPTUtil.transPPTXToPic(request.getFilePath(),request.getFileMagic());
    }
}
