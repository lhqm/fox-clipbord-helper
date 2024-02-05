package com.ruifox.util.pdf;

import com.ruifox.init.RunDir;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;

public class PDFToHtml {
    /**
     * 传递文件路径
     * @param filePath 文件路径
     * @return 返回html标签
     */
    public static String inputDocPath(String filePath) {
        File file = new File(filePath);
        try {
            PDDocument document = PDDocument.load(file);
            Writer writer = new StringWriter();
            new PDFDomTree().writeText(document, writer);
            writer.close();
            document.close();
            Document parse = Jsoup.parse(writer.toString());

            String body = parse.getElementsByTag("body").get(0).html();
            String style = parse.getElementsByTag("style").get(0).outerHtml();
//            删掉字体声明
            String regex="@font-face\\s*\\{[^}]*\\}";
            style=style.replaceAll(regex,"");
//            返回数据
            return style+body;
        }catch (Exception e){
            return "PDF解析失败";
        }finally {
            file.delete();
        }
    }
}
