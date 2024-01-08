package com.ruifox.util;

import org.jsoup.Jsoup;

import java.awt.*;
import java.awt.datatransfer.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ClipboardParser {

    /**
     * 获取剪切板混合数据
     * @return 剪切板数据
     * @throws Exception 剪切板异常
     */
    public static String processClipboard() throws Exception {
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipTf = sysClip.getContents(null);
        StringBuilder resultBuilder = new StringBuilder();

        if (clipTf != null) {
            DataFlavor[] dataFlavors = clipTf.getTransferDataFlavors();
            for (DataFlavor dataFlavor : dataFlavors) {
                if (dataFlavor.isFlavorTextType()) {
                    try {
                        // Handle text data
                        String text = (String) clipTf.getTransferData(dataFlavor);
                        resultBuilder.append("<p>").append(text).append("</p>");
                    } catch (Exception e) {
                        // Handle non-string text data
                        String text = clipTf.getTransferData(dataFlavor).toString();
                        resultBuilder.append("<p>").append(text).append("</p>");
                    }
                }
//                else if (dataFlavor.isMimeTypeEqual("image/png")) {
                    // Handle image data
//                    Image image = (Image) clipTf.getTransferData(dataFlavor);
//                    String imageBase64 = imageToBase64(image);
//                    resultBuilder.append("<img src='data:image/png;base64,").append(imageBase64).append("' />");
//                }
            }
        }
        return resultBuilder.toString();
    }

    /**
     * 图片转base64输出
     * @param imagePath 图片路径
     * @return 图片base64编码
     */
    public static String imageToBase64(String imagePath) {
        try {
            // 读取图片文件内容
            Path path = Paths.get(imagePath);
            byte[] imageBytes = Files.readAllBytes(path);
            // 使用 Base64 编码图片数据
            return "data:image/jpeg;base64,"+Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取到所有的数据字段
     * @param input 输入
     * @return 数据
     */
    public static List<String> getWrappedStrings(String input) {
        String startTag = "<!--StartFragment-->";
        String endTag = "<!--EndFragment-->";
        List<String> wrappedStrings = new ArrayList<>();
        int startIndex = 0;
        int endIndex = 0;
        while (startIndex != -1 && endIndex != -1) {
            startIndex = input.indexOf(startTag, endIndex);
            endIndex = input.indexOf(endTag, startIndex + startTag.length());

            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex + startTag.length()) {
                String wrappedString = input.substring(startIndex + startTag.length(), endIndex);
                wrappedStrings.add(wrappedString);
            }
        }
        return wrappedStrings;
    }

    /**
     * 获取到第一条剪切截取字段
     * @param input 输入
     * @return 数据字段
     */
    public static String getWrappedString(String input) {
        String startTag = "<!--StartFragment-->";
        String endTag = "<!--EndFragment-->";

        int startIndex = input.indexOf(startTag);
        int endIndex = input.indexOf(endTag);
//        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n"+input+"\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        //RTF文本,直接解析
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex + startTag.length()) {
            return input.substring(startIndex + startTag.length(), endIndex);
        }
        //类RTF文本,其数据格式仍旧是HTML，但是转变为了多行HTML模式，典型应用是JB系列的编译器
        else if (input.contains("<html>") && input.contains("</html>")){
            startIndex=input.indexOf("<html>");
            endIndex=input.indexOf("</html>");
            String dom = input.substring(startIndex, endIndex);
            dom = Jsoup.parse(dom).body().html();
            return dom;
        }
        //都没有命中，视为普通文本，处理返回
        else {
            //截取第一段数据
            startIndex=input.indexOf("<p>");
            endIndex=input.indexOf("</p>");
            //判断第一段是否是java-io标志位，如果是，取第二段
            //截取dom数据，获取到其中一个小片段
            String dom = input.substring(startIndex+3, endIndex);
            //裁掉这一段，取后边一段
            if (dom.startsWith("java.io.InputStreamReader@")){
                input=input.substring(endIndex+4);
                startIndex=input.indexOf("<p>");
                endIndex=input.indexOf("</p>");
                dom = input.substring(startIndex+3, endIndex);
            }
            //分片返回
            String[] split = dom.split("\n");
            String res="";
            for (String s : split) {
                res+="<p>"+s+"</p>";
            }
            return res;
        }
    }
    public static void main(String[] args) {
//        try {
////            获取原始剪切板数据
//            String clipboardData = processClipboard();
////            解析获取HTML数据
//            String wrappedString = "<html><head></head><body>"+getWrappedString(clipboardData)+"</body><html>";
////            锁定获取到body，获取子元素进行数据处理，里边的数据就是剪切板数据
//            StringBuilder result = new StringBuilder();
//            Elements elements = Jsoup.parse(wrappedString).getElementsByTag("body").get(0).children();
//            for (Element item : elements) {
//                removeClassAndMsoStyles(item);
//                result.append(item.outerHtml());
//            }
//            String renderHtml = HtmlUtil.covertImageInHtmlStringToBase64(result.toString());
//            System.out.println(renderHtml);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}