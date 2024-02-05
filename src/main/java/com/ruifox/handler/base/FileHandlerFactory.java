package com.ruifox.handler.base;

import com.ruifox.handler.DOCHandler;
import com.ruifox.handler.DOCXHandler;
import com.ruifox.handler.PDFHandler;

import java.util.concurrent.atomic.AtomicReference;

public class FileHandlerFactory {

    // 使用AtomicReference来保证线程安全,虽然好像这玩意没有并发可言
    private static final AtomicReference<FileHandler> HANDLER_CHAIN = new AtomicReference<>();

    // 私有构造函数，防止外部实例化
    private FileHandlerFactory() {}

    // 获取责任链的静态方法
    public static FileHandler getHandlerChain() {
        // 如果链还没有被初始化，就创建它
        if (HANDLER_CHAIN.get() == null) {
            // 设置责任链的head节点为DOCXHandler
            HANDLER_CHAIN.set(fileProcessFilterRegistry());
        }
        // 返回责任链的head节点
        return HANDLER_CHAIN.get();
    }

    private static FileHandler fileProcessFilterRegistry(){
        // 创建DOCHandler和DOCXHandler的实例
        FileHandler docHandler = new DOCHandler();
        FileHandler docxHandler = new DOCXHandler();
        // 设置DOCXHandler的下一个处理器为DOCHandler
        docxHandler.setNextHandler(docHandler);
//        设置doc后一个处理器为PDF处理器
        PDFHandler pdfHandler = new PDFHandler();
        docHandler.setNextHandler(pdfHandler);

        // 最后，兜底的处理器会处理一切
        NoSuchFileTypeHandler noSuchFileTypeHandler = new NoSuchFileTypeHandler();
        pdfHandler.setNextHandler(noSuchFileTypeHandler);
        return docxHandler;
    }
}
