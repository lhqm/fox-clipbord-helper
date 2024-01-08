package com.ruifox.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 * @author 离狐千慕
 * @version 1.0
 * @date 2023/10/16 11:22
 */
public class HtmlUtil {
    /**
     * 该方法废弃。原因是不能满足获取到复杂元素的CSS。
     * @param element /
     * @return /
     */
    public static String transHtmlElementToParagraph(Element element) {
        StringBuilder sb = new StringBuilder();
//      循环标签，获取内部数据
        for (Node node : element.childNodes()) {
//            如果是标签，获取数据
            if (node instanceof Element) {
                Element childElement = (Element) node;
                String tagName = childElement.tagName();
//                是图片，终止递归并且获取图片源
                if ("img".equals(tagName)) {
                    String src = childElement.attr("src");
                    src = src.replace("file:///", "");
                    sb.append("<img src=\"data:image/png;base64,").append(ClipboardParser.imageToBase64(src)).append("\">");
                }
//                非图片元素，进递归
                else {
                    sb.append(transHtmlElementToParagraph(childElement));
                }
            }
//            如果是文本之类的，直接拼接
            else {
//                System.out.println(node.parent().outerHtml());
                sb.append(node.toString());
            }
        }
//        return "<p>" + sb.toString() + "</p>";
        return sb.toString();
    }

    /**
    删去无用的style
     */
    public static void removeClassAndMsoStyles(Element element) {
        element.removeAttr("class");
        for (Element child : element.children()) {
            // 递归处理子元素
            removeClassAndMsoStyles(child);
            // 删除子元素的style属性中以mso-开头的属性值
            String style = child.attr("style");
            String[] attributes = style.split(";");
            StringBuilder newStyle = new StringBuilder();

            for (String attribute : attributes) {
                if (!attribute.trim().startsWith("mso-")) {
                    newStyle.append(attribute).append(";");
                }
            }
            // 删去多余的分号和空格
            String attributeValue = newStyle.toString().replace(" ","");
            while (attributeValue.endsWith(";")){
                attributeValue=attributeValue.substring(0,attributeValue.length()-1);
            }
            // 为空了就删去，否则重新写回到style
            if (attributeValue.equals("")){
                child.removeAttr("style");
            }else {
                child.attr("style", attributeValue);
            }

            // 替换 <o:p> 标签为 <div>
            if (child.tagName().equals("o:p")) {
                child.tagName("code");
            }
        }
    }

    /**
     * 将代码中的图片转换成base64输出
     * @param html html文本
     * @return 图片经过转换的HTML
     */

    public static String covertImageInHtmlStringToBase64(String html){
        Document doc = Jsoup.parse(html);

        Elements imgs = doc.select("img");
        for (Element img : imgs) {
            String src = img.attr("src");
            if (src.startsWith("file:///")) {
                src = src.substring("file:///".length());
                String base64Image = ClipboardParser.imageToBase64(src);
                img.attr("src", base64Image);
            }
        }
        return doc.getElementsByTag("body").get(0).html();
    }
}
