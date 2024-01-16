package com.ruifox.util.doc;

import com.ruifox.util.ClipboardParser;
import fr.opensagres.poi.xwpf.converter.core.BasicURIResolver;
import fr.opensagres.poi.xwpf.converter.core.FileImageExtractor;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;

public class DocxToHtml {

    /**
     * 上传docx文档，返回解析后的Html
     */
    public static String inputDocxPath(String filePath) throws Exception {
        File file = new File(filePath);
        ByteArrayOutputStream htmlStream = new ByteArrayOutputStream();
        FileInputStream is = new FileInputStream(file);
        try {
            // 将上传的文件传入Document转换
            XWPFDocument docxDocument = new XWPFDocument(is);
//            提前关闭流
            is.close();
            XHTMLOptions options = XHTMLOptions.create();
            // 设置图片存储路径
            String path = System.getProperty("java.io.tmpdir");
            String firstImagePathStr = path + "/" + System.currentTimeMillis();
            options.setExtractor(new FileImageExtractor(new File(firstImagePathStr)));
            options.URIResolver(new BasicURIResolver(firstImagePathStr));
            // 转换html
            docxDocument.createNumbering();
            XHTMLConverter.getInstance().convert(docxDocument, htmlStream, options);
            String htmlStr = htmlStream.toString();

            Document doc = Jsoup.parse(htmlStr);
//            doc.getElementsByTag("head").get(0).appendChild(new org.jsoup.nodes.Element("meta").attr("charset", "utf-8"));
            doc.getElementsByTag("img").forEach(img -> {
//                获取到图片路径
                String src = img.attr("src");
                // Replace forward slashes with backslashes
                String correctedPath = src.replace('/', '\\').replace("\\\\", "\\");
                System.out.println("图片路径：" + correctedPath);
                img.attr("src", ClipboardParser.imageToBase64(correctedPath));
            });

            String body = doc.getElementsByTag("body").get(0).html();
            String style = doc.getElementsByTag("style").get(0).outerHtml();
            return style + body;
        } catch (Exception e) {

        } finally {
            is.close();
            htmlStream.close();
            boolean delete = file.delete();
            System.out.println("删除文件：" + filePath + (delete ? "成功" : "失败"));
        }
        return "";
    }
}

