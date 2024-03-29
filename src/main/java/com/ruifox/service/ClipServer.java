package com.ruifox.service;

import com.ruifox.handler.base.FileHandlerFactory;
import com.ruifox.handler.base.FileRequest;
import com.ruifox.init.RunDir;
import com.ruifox.util.doc.DocToHtml;
import com.ruifox.util.doc.DocxToHtml;
import com.ruifox.util.HtmlUtil;
import com.ruifox.util.JsonUtil;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import spark.Request;
import spark.utils.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.*;

import static com.ruifox.util.ClipboardParser.getWrappedString;
import static com.ruifox.util.ClipboardParser.processClipboard;
import static com.ruifox.util.HtmlUtil.removeClassAndMsoStyles;

/**
 * @author 离狐千慕
 * @version 1.0
 * @date 2023/10/13 15:26
 */
public class ClipServer {
    public static String getClipBoardData(){
        try {
//            获取原始剪切板数据
            String clipboardData = processClipboard();
//            处理剪切板数据，如果是空的就直接返回报错
            String wr = getWrappedString(clipboardData);
            if (wr==null){
                return JsonUtil.failResp("<p>剪切板内数据非RTF(office标记语言)或剪切板为空！</p>");
            }
//            解析获取HTML数据
            String wrappedString = "<html><head></head><body><div>"+ wr +"</div></body><html>";
//            锁定获取到body，获取子元素进行数据处理，里边的数据就是剪切板数据
            StringBuilder result = new StringBuilder();
            Elements elements = Jsoup.parse(wrappedString).getElementsByTag("body").get(0).children();
//            如果为0，则可能是里边是纯文本
//            if (elements.size()==0){
//                return JsonUtil.successResp(Jsoup.parse(wrappedString).getElementsByTag("body").get(0).html());
//            }
            for (Element item : elements) {
                removeClassAndMsoStyles(item);
                result.append(item.outerHtml());
            }
            return JsonUtil.successResp(HtmlUtil.covertImageInHtmlStringToBase64(result.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.failResp("本地服务异常！请重新复制数据到剪切板。如果仍旧无效，请查看确保本程序有系统访问权限！");
        }
    }

    public static String uploadAndAnalysis(Request request) throws ServletException, IOException {
        // 设置文件上传配置
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

        // 获取上传的文件部分
        Part filePart = request.raw().getPart("file");
        System.out.println("文件名:" + filePart.getSubmittedFileName());
        // 读取文件内容
        InputStream is = null;
        try (InputStream fis = filePart.getInputStream();) {
//            建立输出流
            new FileOutputStream(RunDir.directoryPath + "/" + filePart.getSubmittedFileName()).write(IOUtils.toByteArray(fis));
            FileInputStream inputStream = new FileInputStream(RunDir.directoryPath + "/" + filePart.getSubmittedFileName());
            is = new BufferedInputStream(inputStream);
            //获取文件类型
            FileMagic fileMagic = FileMagic.valueOf(is);
            System.out.println(fileMagic);
            //关闭输入流
            inputStream.close();
            is.close();
            //通过责任链模式返回数据
            String s = FileHandlerFactory
                    .getHandlerChain()
                    .handleRequest(new FileRequest(fileMagic, RunDir.directoryPath + "/" + filePart.getSubmittedFileName()));
//            包装返回数据
            return JsonUtil.successResp(s);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.failResp("文件上传失败！");
        } finally {
            // 删除临时文件
            filePart.delete();
        }
    }
}
