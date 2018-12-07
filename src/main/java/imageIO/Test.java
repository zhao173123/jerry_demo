package imageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;

public class Test {

    public static void main(String[] args) throws IOException {
//        try {
//            //图片的本地地址
//            Image src = ImageIO.read(new File("/Users/jerry/Downloads/info/pic/avatar.jpeg"));
//            BufferedImage url = (BufferedImage) src;
//            //处理图片将其压缩成正方形的小图
//            BufferedImage convertImage = scaleByPercentage(url, 100, 100);
//            //裁剪成圆形 （传入的图像必须是正方形的 才会 圆形 如果是长方形的比例则会变成椭圆的）
//            convertImage = convertCircular(url);
//            //生成的图片位置
//            String imagePath = "/Users/jerry/Downloads/info/pic/avatar-test.jpeg";
//            ImageIO.write(convertImage, imagePath.substring(imagePath.lastIndexOf(".") + 1), new File(imagePath));
//            System.out.println("ok");
//        }catch(Exception e) {
//            e.printStackTrace();
//        }
        String sourceUrl = "/Users/jerry/Downloads/info/pic/IMG_0230.JPG";
        String dstUrl = "/Users/jerry/Downloads/info/pic/IMG_0230-cut.JPG";

//        reduceImageEqualProportion(sourceUrl, dstUrl, 3);

        int x = 0;
        int y = 295;
        int width = 885;
        int height = 663;
        String readImageFormat = "JPG";
        String writeImageFormat = "JPG";

        cropImage(sourceUrl, dstUrl, x, y, width, height, readImageFormat, writeImageFormat);
    }

    /**
     * 缩小Image，此方法返回源图像按给定宽度、高度限制下缩放后的图像
     *
     * @param inputImage
     * @param newWidth   ：压缩后宽度
     * @param newHeight  ：压缩后高度
     * @throws java.io.IOException return
     */
    public static BufferedImage scaleByPercentage(BufferedImage inputImage, int newWidth, int newHeight) throws Exception {
        // 获取原始图像透明度类型
        int type = inputImage.getColorModel().getTransparency();
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        // 开启抗锯齿
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 使用高质量压缩
        renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        BufferedImage img = new BufferedImage(newWidth, newHeight, type);
        Graphics2D graphics2d = img.createGraphics();
        graphics2d.setRenderingHints(renderingHints);
        graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0, width, height, null);
        graphics2d.dispose();
        return img;
    }

    /**
     * 通过网络获取图片
     *
     * @param url
     * @return
     */
    public static BufferedImage getUrlByBufferedImage(String url) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            // 连接超时
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(25000);
            // 读取超时 --服务器响应比较慢,增大时间
            conn.setReadTimeout(25000);
            conn.setRequestMethod("GET");
            conn.addRequestProperty("Accept-Language", "zh-cn");
            conn.addRequestProperty("Content-type", "image/jpeg");
            conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727)");
            conn.connect();
            BufferedImage bufImg = ImageIO.read(conn.getInputStream());
            conn.disconnect();
            return bufImg;
        }catch(MalformedURLException e) {
            e.printStackTrace();
        }catch(ProtocolException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 传入的图像必须是正方形的 才会 圆形 如果是长方形的比例则会变成椭圆的
     *
     * @return
     * @throws IOException
     */
    public static BufferedImage convertCircular(BufferedImage bi1) throws IOException {

//		BufferedImage bi1 = ImageIO.read(new File(url));

        // 这种是黑色底的
//		BufferedImage bi2 = new BufferedImage(bi1.getWidth(), bi1.getHeight(), BufferedImage.TYPE_INT_RGB);

        // 透明底的图片
        BufferedImage bi2 = new BufferedImage(bi1.getWidth(), bi1.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, bi1.getWidth(), bi1.getHeight());
        Graphics2D g2 = bi2.createGraphics();
        g2.setClip(shape);
        // 使用 setRenderingHint 设置抗锯齿
        g2.drawImage(bi1, 0, 0, null);
        // 设置颜色
        g2.setBackground(Color.green);
        g2.dispose();
        return bi2;
    }

    public static void reduceImageEqualProportion(String srcImagePath, String toImagePath, int ratio) throws IOException{
        FileOutputStream out = null;
        try{
            //读入文件
            File file = new File(srcImagePath);
            // 构造Image对象
            BufferedImage src = javax.imageio.ImageIO.read(file);
            int width = src.getWidth();
            int height = src.getHeight();
            // 缩小边长
            BufferedImage tag = new BufferedImage(width / ratio, height / ratio, BufferedImage.TYPE_INT_RGB);
            // 绘制 缩小  后的图片
            tag.getGraphics().drawImage(src, 0, 0, width / ratio, height / ratio, null);
            out = new FileOutputStream(toImagePath);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(out != null){
                out.close();
            }
        }
    }

    /**
     * 对图片裁剪，并把裁剪新图片保存
     * @param srcPath 读取源图片路径
     * @param toPath    写入图片路径
     * @param x 剪切起始点x坐标
     * @param y 剪切起始点y坐标
     * @param width 剪切宽度
     * @param height     剪切高度
     * @param readImageFormat  读取图片格式
     * @param writeImageFormat 写入图片格式
     * @throws IOException
     */
    public static void cropImage(String srcPath, String toPath, int x, int y, int width, int height,
                                 String readImageFormat, String writeImageFormat) throws IOException{
        FileInputStream fis = null ;
        ImageInputStream iis =null ;
        try{
            //读取图片文件
            fis = new FileInputStream(srcPath);
            Iterator it = ImageIO.getImageReadersByFormatName(readImageFormat);
            ImageReader reader = (ImageReader) it.next();
            //获取图片流
            iis = ImageIO.createImageInputStream(fis);
            reader.setInput(iis,true) ;
            ImageReadParam param = reader.getDefaultReadParam();
            //定义一个矩形
            Rectangle rect = new Rectangle(x, y, width, height);
            //提供一个 BufferedImage，将其用作解码像素数据的目标。
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0,param);
            //保存新图片
            ImageIO.write(bi, writeImageFormat, new File(toPath));
        }finally{
            if(fis!=null)
                fis.close();
            if(iis!=null)
                iis.close();
        }
    }
}
