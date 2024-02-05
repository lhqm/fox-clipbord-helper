package com.ruifox.handler.base;

import java.io.File;

/**
 * 兜底的责任链，走到这条证明前边所有责任链都失败了，这时候要安全删文件返回
 */
public class NoSuchFileTypeHandler extends FileHandler{
    @Override
    protected boolean canHandle(FileRequest request) {
//        都能处理，走到这一步了必须处理掉
        return true;
    }

    @Override
    protected String processRequest(FileRequest request) {
//        删文件
        boolean delete = new File(request.getFilePath()).delete();
//        返回错误报告
        return String.format("不支持解析的文档类型,文件 %s 成功删除",delete?"已":"未");
    }
}
