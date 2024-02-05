package com.ruifox.handler.base;

/**
 * 责任链模式基类，用于后续实现文件校验和处理（基于文件头，即FileMagic）
 */
public abstract class FileHandler {
    private FileHandler nextHandler;
 
    public void setNextHandler(FileHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
 
    public String handleRequest(FileRequest request) {
//        本类可以处理，就自己处理掉
        if (canHandle(request)) {
            return processRequest(request);
        }
//        否则丢给下一条链执行
        else if (nextHandler != null) {
            return nextHandler.handleRequest(request);
        } else {
            return "非合法的文档类型.";
        }
    }
 
    protected abstract boolean canHandle(FileRequest request);
 
    protected abstract String processRequest(FileRequest request);
}