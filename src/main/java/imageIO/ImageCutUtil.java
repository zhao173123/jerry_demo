package imageIO;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ImageCutUtil {

    /**
     * 获取图片宽度
     * @param file  图片文件
     * @return 宽度
     */
    public static int getImgWidth(File file) {
        InputStream is = null;
        BufferedImage src = null;
        int ret = -1;
        try {
            is = new FileInputStream(file);
            src = javax.imageio.ImageIO.read(is);
            ret = src.getWidth(null); // 得到源图宽
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 获取图片高度
     * @param sourcePath  图片路径
     * @return 宽度
     */
    public static int getImgWidth(String sourcePath) {
        File file=new File(sourcePath);
        return getImgWidth(file);
    }

    /**
     * 获取图片高度
     * @param file  图片文件
     * @return 高度
     */
    public static int getImgHeight(File file) {
        InputStream is = null;
        BufferedImage src = null;
        int ret = -1;
        try {
            is = new FileInputStream(file);
            src = javax.imageio.ImageIO.read(is);
            ret = src.getHeight(null); // 得到源图高
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 获取图片高度
     * @param sourcePath  图片路径
     * @return 高度
     */
    public static int getImgHeight(String sourcePath) {
        File file=new File(sourcePath);
        return getImgHeight(file);
    }

    /**
     * 图片裁切
     * 选择的总区域必须与图片原始大小相同
     * @param X 选择区域左上角的x坐标
     * @param Y 选择区域左上角的y坐标
     * @param width 选择区域的宽度
     * @param height 选择区域的高度
     * @param sourcePath 源图片路径
     * @param descpath 裁切后图片的保存路径
     */
    public static void cut(int X, int Y, int width, int height,
                           String sourcePath, String descpath) {

        FileInputStream is = null;
        ImageInputStream iis = null;
        try {
            is = new FileInputStream(sourcePath);
            String fileSuffix = sourcePath.substring(sourcePath
                    .lastIndexOf(".") + 1);
            Iterator<ImageReader> it = ImageIO
                    .getImageReadersByFormatName(fileSuffix);
            ImageReader reader = it.next();
            iis = ImageIO.createImageInputStream(is);
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            int imgWidth=getImgWidth(sourcePath);
            int imgHeight=getImgHeight(sourcePath);
            X = imgWidth/4;
            Y = imgHeight/4;
            width = 885;
            height = 663;
            Rectangle rect = new Rectangle(X, Y, width, height);
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            ImageIO.write(bi, fileSuffix, new File(descpath));
            System.out.println("图片剪切成功,生成图片路径:"+descpath);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
            if (iis != null) {
                try {
                    iis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                iis = null;
            }
        }

    }

    /**
     * 图片裁切
     * 选择的总区域与图片原始大小不同
     * @param selectWidth 选择区域的宽度
     * @param selectHeight 选择区域的高度
     * @param selectX 选择区域左上角的x坐标
     * @param selectY 选择区域左上角的y坐标
     * @param areaWidth 选择区域总宽度
     * @param areaHeight 选择区域总高度
     * @param sourcePath 源图片路径
     * @param descpath 裁切后图片的保存路径
     */
    public static void cut(int areaWidth, int areaHeight,int selectX, int selectY, int selectWidth, int selectHeight,String sourcePath, String descpath) {
        int imgWidth=getImgWidth(sourcePath);
        int imgHeight=getImgHeight(sourcePath);
        int X=selectX*imgWidth/areaWidth;
        int Y=selectY*imgHeight/areaHeight;
        int width=selectWidth*imgWidth/areaWidth;
        int height=selectHeight*imgHeight/areaHeight;
        cut(X, Y, width, height, sourcePath, descpath);
    }
    public static void main(String[] args) {
        ImageCutUtil.cut(0, 0, 885, 663, "/Users/jerry/Downloads/info/pic/IMG_0230.JPG",
                "/Users/jerry/Downloads/info/pic/IMG_0230_cut.JPG");

    }

}
