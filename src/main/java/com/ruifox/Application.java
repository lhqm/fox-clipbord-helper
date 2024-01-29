package com.ruifox;

import com.ruifox.config.AuthorizationFilter;
import com.ruifox.exception.NoAuthorizationException;
import com.ruifox.init.RunDir;
import com.ruifox.service.ClipServer;
import com.ruifox.util.JsonUtil;
import spark.Spark;
import spark.utils.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import java.io.InputStream;

import static spark.Spark.*;

/**
 * @author 离狐千慕
 * @version 1.0
 * @date 2023/10/13 13:57
 * 项目使用Spark微框架进行编写
 * 如果需要参照，敬请自行搜索，相关教程太少
 *
 */
public class Application {
    public static void main(String[] args) {
//        配置端口
        port(10086);
//        初始化文件列表
        RunDir.init();
//        配置跨域
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
            response.header("Access-Control-Allow-Credentials", "true");
        });
//        注册配置拦截器
//        Spark.before(new AuthorizationFilter());
//        注册异常处理器
        Spark.exception(NoAuthorizationException.class, (exception, request, response) -> {
            response.body(JsonUtil.failResp(exception.getMessage()));
            response.type("application/json");
        });
//        获取剪切板
        get("/clip", (req, res) -> ClipServer.getClipBoardData());
//        导入文件
        post("/upload", (request, response) -> ClipServer.uploadAndAnalysis(request));
    }
}
