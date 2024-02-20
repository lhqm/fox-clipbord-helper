package com.ruifox.util.ppt;

import com.ruifox.init.RunDir;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

/**
 * @author RedRush
 * @date 2022/6/29 17:10
 * @description: ppt转化工具类
 */
public class PPTUtil {

    public static void main(String[] args) {
        new PPTUtil().transPPTXToPic("C:/Users/Administrator/Desktop/ppt测试.ppt",FileMagic.OLE2);
    }
    /**
     * @Author: RedRush
     * @Date: 2022/6/29 22:32
     * @description: ppt/pptx 转换为图片
     */
    public static String transPPTXToPic(String fullPath, FileMagic fileMagic) {
        int sep = fullPath.lastIndexOf("/");
        String root = fullPath.substring(0,sep+1);
        String fileName = fullPath.substring(sep+1);
        System.out.println(root+"\n"+fileName);
        try {
            if (fileMagic.equals(FileMagic.OOXML)) {
                return transPPTXToPic(root, fileName);
            } else if (fileMagic.equals(FileMagic.OLE2)) {
                return transPPTToPic(root, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
//        return getAllImagesAndDeleteTempFiles(fullPath,root);
    }

    private static String getAllImagesAndDeleteTempFiles(String fullPath,String root) {
        int i = fullPath.lastIndexOf(".");
//        找到文件夹
        String dirPath=fullPath.substring(0,i);
//        读取文件夹内容
        File directory = new File(dirPath);
//        遍历获取文件(只要文件)
        File[] files = directory.listFiles(File::isFile);
        if (files==null) return null;
        StringBuilder result= new StringBuilder();
        for (File file : files) {
            result.append("<img src=\"").append(fileStreamToBase64(file)).append("\"").append(" />");
        }
        return result.toString();
    }
    private static String fileStreamToBase64(File file){
        try {
            byte[] imageBytes = Files.readAllBytes(Path.of(file.getPath()));
            // 使用 Base64 编码图片数据
            return "data:image/jpeg;base64,"+ Base64.getEncoder().encodeToString(imageBytes);
        }catch (Exception e){
            return "图片读取失败<br>";
        }
    }

    // PPT输出为图片 hslf解析
    private static String transPPTToPic(String filePath, String fileName) {
        // 生成输出
        String outRoot = RunDir.directoryPath+"/" + fileName.substring(0, fileName.indexOf('.')) + File.separator;
        System.err.printf("图片输出路径为:%s\n", outRoot);
        // 不存在则创建文件夹
        mkdir(outRoot);
        // ppt读取路径
        String pptName = filePath + fileName;
        System.err.printf("PPT读取路径为:%s\n", pptName);
        FileInputStream fis = null;

        try {
            // 获取系统可用字体
            GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = e.getAvailableFontFamilyNames();

            // 读取ppt
            fis = new FileInputStream(pptName);
            HSLFSlideShow ppt = new HSLFSlideShow(fis);


            /*
             * 解析PPT基本内容
             * */
            Dimension sheet = ppt.getPageSize();
            int width = sheet.width, height = sheet.height;
            List<HSLFSlide> pages = ppt.getSlides();

            System.err.printf("ppt基本信息: 共%s页,尺寸: %s , %s\n", pages.size(), width, height);

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            StringBuilder result= new StringBuilder();
            int i = 1;
            // 逐页遍历
            for (HSLFSlide slide : pages) {
                // 清空画板
                graphics.setPaint(Color.white);

                graphics.fill(new Rectangle2D.Float(0, 0, width, height));

                slide.draw(graphics);
//                slide.getBackground().draw(graphics,new Rectangle2D.Float(0, 0, width, height));
                // 输出为图片
                File f = new File(outRoot + i++ + ".png");
                System.out.printf("输出图片：%s\n", f.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(f);
                javax.imageio.ImageIO.write(img, "PNG", fos);
                fos.close();
                result.append("<img src=\"").append(fileStreamToBase64(f)).append("\"").append(" />");
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //            删文件
            RunDir.deleteDirectory(new File(outRoot));
            System.out.println("删除文件:"+new File(pptName).delete());
        }
        return null;
    }

    // PPTX输出为图片 xmls包解析
    private static String transPPTXToPic(String filePath, String fileName) {
        // 生成输出
        String outRoot = RunDir.directoryPath+"/" + fileName.substring(0, fileName.indexOf('.')) + File.separator;
        System.err.printf("图片输出路径为:%s\n", outRoot);
        // 不存在则创建文件夹
        mkdir(outRoot);
        // ppt读取路径
        String pptName = filePath + fileName;
        System.err.printf("PPT读取路径为:%s\n", pptName);
        FileInputStream fis = null;

        try {
            // 获取系统可用字体
            GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = e.getAvailableFontFamilyNames();

            // 读取ppt
            fis = new FileInputStream(pptName);
            XMLSlideShow ppt = new XMLSlideShow(fis);
            /*
             * 解析PPT基本内容
             * */
            Dimension sheet = ppt.getPageSize();
            int width = sheet.width, height = sheet.height;
            int count = ppt.getSlides().size();
            System.err.printf("ppt基本信息: 共%s页,尺寸: %s , %s", count, width, height);


            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            int i = 1;
            StringBuilder result=new StringBuilder();
            // 逐页遍历
            for (XSLFSlide shape : ppt.getSlides()) {

                // 清空画板
                graphics.setPaint(Color.white);
                graphics.fill(new Rectangle2D.Float(0, 0, width, height));
                shape.draw(graphics);
                // 输出为图片
                File f = new File(outRoot + i++ + ".png");
                System.out.printf("输出图片：%s\n", f.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(f);
                javax.imageio.ImageIO.write(img, "PNG", fos);
                fos.close();
                result.append("<img src=\"").append(fileStreamToBase64(f)).append("\"").append(" />");
            }
            return result.toString();
        } catch (Exception e) {
            System.err.println("======ppt转换异常");
            e.printStackTrace();
        } finally {
//            关文件
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            删文件
            RunDir.deleteDirectory(new File(outRoot));
            System.out.println("删除文件:"+new File(pptName).delete());
        }
        return null;
    }

    // 生成文件夹
    private static void mkdir(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
    }
}
