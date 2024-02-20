package com.ruifox.handler.base;

import com.ruifox.handler.DOCHandler;
import com.ruifox.handler.DOCXHandler;
import com.ruifox.handler.PDFHandler;
import com.ruifox.handler.PPTHandler;

import java.util.List;
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
            // 设置责任链的head节点，为责任链进行实例化
            HANDLER_CHAIN.set(fileProcessFilterRegistry());
        }
        // 返回责任链的head节点
        return HANDLER_CHAIN.get();
    }

    /**
     * 注册过滤器链
     * @return 过滤器链头部节点
     */

    private static FileHandler fileProcessFilterRegistry(){
//        构造处理器链
//        这里注意，PPT的处理器需要放在第一个，因为PPT的头和doc的fileMagic有重叠
        return constructorChain(
                List.of(new PPTHandler(),new DOCHandler(),new DOCXHandler(),new PDFHandler())
        );
    }

    /**
     * 过滤器链条构造器
     * 主要将处理器全部由list转换为chain
     * 并为该chain拼接一个默认处理器作为尾部
     * @param handlers 处理器集合
     * @return 处理器头部节点
     * @param <E> 处理器
     */

    private static <E extends FileHandler> FileHandler constructorChain(List<E> handlers){
//        循环添加处理器
        for (int i = 0; i < handlers.size()-1; i++) {
            handlers.get(i).setNextHandler(handlers.get(i+1));
        }
//        最后一个处理器，加上非空处理器
        handlers.get(handlers.size()-1).setNextHandler(new NoSuchFileTypeHandler());
//        返回第一个处理器
        return handlers.get(0);
    }
}
