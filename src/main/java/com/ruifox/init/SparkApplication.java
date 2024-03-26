package com.ruifox.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

import static spark.Spark.port;

public class SparkApplication {
    static Logger log = LoggerFactory.getLogger(SparkApplication.class);
    public static void run(int startPort){
        int port = startPort; // 起始端口号
        int maxPort = startPort+1000; // 尝试的最大端口号，根据需要进行调整

        // 循环尝试端口，直到找到可用的端口
        while (port <= maxPort) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                log.info("端口[" + port + "]处于空闲状态，正在尝试从JVM环境拉起控件程序");
                // 设置Spark的端口
                port(port);
                log.info("程序已绑定端口["+port+"]进行TCP监听");
                break; // 退出循环
            } catch (IOException e) {
                log.error("端口[" + port + "]已经被占用，系统将等待一秒后向下寻找下一个端口进行TCP监听...");
                port++;
                // 这里可以选择是否等待一段时间再尝试下一个端口，以避免过快地尝试
                try {
                    Thread.sleep(1000); // 等待1秒
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
