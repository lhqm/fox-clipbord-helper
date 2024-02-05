package com.ruifox.util.doc;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class DocToHtml {

    /**
     * 传递文件路径
     * @param filePath 文件路径
     * @return 返回html标签
     */
    public static String inputDocPath(String filePath) {
        File file = new File(filePath);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            HWPFDocument hwpfDocument = new HWPFDocument(inputStream);
//            提前关闭，让gc线程和文件系统进入回收阶段
            inputStream.close();
            // 提取图像数据并转换为 BASE64 编码字符串
            List<Picture> pictures = hwpfDocument.getPicturesTable().getAllPictures();
            List<String> base64ImageStrings = new ArrayList<>();
            for (Picture picture : pictures) {
                byte[] imageData = picture.getContent();
                String base64ImageString = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageData);
                base64ImageStrings.add(base64ImageString);
            }

            // 转换为 HTML 文本
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            wordToHtmlConverter.processDocument(hwpfDocument);
            Document document = wordToHtmlConverter.getDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "html");
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
            String html = outputStream.toString("UTF-8");
            //查找所有图片
            List<String> matches = findMatchesToPic(html);

            //查找无用标签并替换成空，用自己写入的标签
            html = findMatchesToLable(html);
            // 在 HTML 中插入图像
            // 替换图片链接为 base64 编码
            for (int i = 0; i < base64ImageStrings.size() && i < matches.size(); i++) {
                html = html.replace(matches.get(i), "<img src=\"" + base64ImageStrings.get(i) + "\">");
            }

            return html;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println("关闭失败");
                }
            }
            boolean delete = file.delete();
            System.out.println("删除文件：" + filePath + "成功：" + delete);
        }
    }

    //查找所有图片集合
    private static List<String> findMatchesToPic(String input) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile("<!--.*?-->");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String match = matcher.group();
            matches.add(match);
        }
        return matches;
    }

    //查找没用的标签，并替换
    private static String findMatchesToLable(String html) {
        Pattern pattern = Pattern.compile("<META.*?>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            html = html.replace(matcher.group(), "");
        }

        Pattern patternMeta = Pattern.compile("<meta.*?>", Pattern.DOTALL);
        Matcher matcherMeta = patternMeta.matcher(html);
        if (matcherMeta.find()) {
            html = html.replace(matcherMeta.group(), "");
        }

        Pattern patternBody = Pattern.compile("<body.*?>", Pattern.DOTALL);
        Matcher matcherBody = patternBody.matcher(html);
        if (matcherBody.find()) {
            html = html.replace(matcherBody.group(), "");
        }

//        Pattern patternStyle = Pattern.compile("<style.*?</style>", Pattern.DOTALL);
//        Matcher matcherStyle = patternStyle.matcher(html);
//        if (matcherStyle.find()) {
//            html = html.replace(matcherStyle.group(), "");
//        }

        html = html.replace("<html>", "");
        html = html.replace("<head>", "");
        html = html.replace("</head>", "");
        html = html.replace("</body>", "");
        html = html.replace("</html>", "");

        return html;
    }


}
