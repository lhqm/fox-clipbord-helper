package com.ruifox.api;

import com.ruifox.service.ClipServer;

import static spark.Spark.get;
import static spark.Spark.post;

public class CpApi {
    public static void defineRoutes(){
        //        获取剪切板
        get("/clip", (req, res) -> ClipServer.getClipBoardData());
//        导入文件
        post("/upload", (request, response) -> ClipServer.uploadAndAnalysis(request));
    }
}
