package imageIO;

import org.junit.Test;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Iterator;

/**
 * Graphics2D
 */
public class TestImageIO {

    @Test
    public void testMeNoBorder() throws IOException {
        int width = 295;
        int height = 496;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        //填充白板
        ImageIcon imgIcon0 = new ImageIcon("/Users/jerry/Downloads/info/pic/baitu.png");
        Image imageLogo0 = imgIcon0.getImage();
        g2d.drawImage(imageLogo0, 0, 0, 295, 496, null);
        //填充封面
        ImageIcon imageIcon2 = new ImageIcon("/Users/jerry/Downloads/info/pic/Group17.png");
        Image imageLogo2 = imageIcon2.getImage();
        g2d.drawImage(imageLogo2, 0, 0, 295, 221, null);
        //填充头像，处理成圆形
        URL uri = new URL("https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eodm6biaNTVQKia2ic0rrUqT5DZkria11kmKZaN9dsmibcA6gThmJrG12e9cP0Nv8MES8d0u21qpUIPxCw/132");
        BufferedImage imageIcon3 = ImageIO.read(uri);
//        BufferedImage imageIcon3 = ImageIO.read(new File("/Users/jerry/Downloads/info/pic/avatar.jpeg"));
        BufferedImage imageIcon31 = new BufferedImage(imageIcon3.getWidth(), imageIcon3.getHeight(), BufferedImage.TYPE_INT_RGB);
        Ellipse2D.Double shape31 = new Ellipse2D.Double(0, 0, imageIcon3.getWidth(), imageIcon3.getHeight());
        Graphics2D g2d31 = imageIcon31.createGraphics();
        g2d31.setBackground(Color.WHITE);
        g2d31.fill(new Rectangle(imageIcon31.getWidth(), imageIcon31.getHeight()));
        g2d31.setClip(shape31);
        //消除锯齿
        g2d31.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d31.drawImage(imageIcon3, 0, 0, null);
        g2d31.dispose();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(imageIcon31, 14, 233, 48, 48, null);
        //填充昵称|描述
        g2d.setPaint(new Color(0, 0, 0));
        Font font1 = new Font("黑体", Font.BOLD, 16);
        g2d.setFont(font1);
        String nickName = "黑体的色彩se";
        g2d.drawString(nickName.length() > 4 ? nickName.substring(0, 4) : nickName, 78, 262);
        Font font2 = new Font("黑体", Font.PLAIN, 10);
        g2d.setFont(font2);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("正在转租TA的房源", 180, 262);
        //房源图标|地址|租金
        ImageIcon imageIcon4 = new ImageIcon("/Users/jerry/Downloads/info/pic/Group15.png");
        Image imageLogo4 = imageIcon4.getImage();
        g2d.drawImage(imageLogo4, 14, 292, 8, 10, null);
        Font font3 = new Font("黑体", Font.PLAIN, 12);
        g2d.setFont(font3);
        g2d.setPaint(new Color(180, 180, 180));
        g2d.drawString("美丽珊公寓110号", 26, 302);
        Font font4 = new Font("黑体", Font.PLAIN, 12);
        g2d.setFont(font4);
        g2d.setPaint(new Color(242, 92, 98));
        g2d.drawString("3500元/月", 220, 302);
        //房源描述第一行
        Font font5 = new Font("黑体", Font.PLAIN, 12);
        g2d.setFont(font5);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("2楼，朝南主卧，三室1厅1卫，室友和我都是美女。", 14, 327);
        //房源描述第二行，最后3个用省略号替换
        Font font6 = new Font("黑体", Font.PLAIN, 12);
        g2d.setFont(font6);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("3楼，朝北次卧，三室1厅1卫，室友和我都是渣...", 14, 345);
        //二维码
        ImageIcon imageIcon5 = new ImageIcon("/Users/jerry/Downloads/info/pic/qr.png");
        Image imageLogo5 = imageIcon5.getImage();
        g2d.drawImage(imageLogo5, 102, 357, 88, 91, null);
        //二维码说明
        Font font7 = new Font("黑体", Font.PLAIN, 10);
        g2d.setFont(font7);
        g2d.setPaint(new Color(165, 165, 165));
        g2d.drawString("长按二维码查看更多详情", 93, 465);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g2d.dispose();
        ImageIO.write(image, "png", new File("/Users/jerry/Downloads/info/pic/me2.png"));
    }


    @Test
    public void testMeBorder() throws Exception {
        int width = 375;
        int height = 667;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        //设置背景色
        g2d.setBackground(new Color(242, 92, 98));
        g2d.clearRect(0, 0, width, height);
        //消除线条锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //填充白板
        ImageIcon imgIcon0 = new ImageIcon("/Users/jerry/Downloads/info/pic/baitu.png");
        Image imageLogo0 = imgIcon0.getImage();
        g2d.drawImage(imageLogo0, 40, 113, 295, 496, null);
        //填充title
        ImageIcon imgIcon1 = new ImageIcon("/Users/jerry/Downloads/info/pic/Group_3.png");
        Image imageLogo1 = imgIcon1.getImage();
        g2d.drawImage(imageLogo1, 87, 58, 202, 36, null);
        //填充封面
        ImageIcon imageIcon2 = new ImageIcon("/Users/jerry/Downloads/info/pic/11.jpg");
        Image imageLogo2 = imageIcon2.getImage();
        g2d.drawImage(imageLogo2, 40, 113, 295, 221, null);
        //覆盖封面边角
        ImageIcon imageIcon21 = new ImageIcon("/Users/jerry/Downloads/info/pic/corner1.png");
        Image imageLogo21 = imageIcon21.getImage();
        g2d.drawImage(imageLogo21, 40, 113, 10, 10, null);
        ImageIcon imageIcon22 = new ImageIcon("/Users/jerry/Downloads/info/pic/corner2.png");
        Image imageLogo22 = imageIcon22.getImage();
        g2d.drawImage(imageLogo22, 325, 113, 10, 10, null);
        //填充头像
        URL uri = new URL("https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eodm6biaNTVQKia2ic0rrUqT5DZkria11kmKZaN9dsmibcA6gThmJrG12e9cP0Nv8MES8d0u21qpUIPxCw/132");
        BufferedImage imageIcon3 = ImageIO.read(uri);
//        BufferedImage imageLogo3 = scaleByPercentage(imageIcon3, 50, 50);
//        ImageIO.write(imageLogo3, "png", new File("/Users/jerry/Downloads/info/pic/avatar_zip.png"));
        g2d.drawImage(imageIcon3, 54, 346, 48, 48, null);

        //使用白图遮住头像边角
        ImageIcon imageIcon32 = new ImageIcon("/Users/jerry/Downloads/info/pic/avatar_corner2.png");
        Image imageLogo32 = imageIcon32.getImage();
        g2d.drawImage(imageLogo32, 54, 346, 48, 48, null);
//        //处理成圆形
//        BufferedImage imageIcon31 = new BufferedImage(imageIcon3.getWidth(), imageIcon3.getHeight(), BufferedImage.TYPE_INT_RGB);
//        Ellipse2D.Double shape31 = new Ellipse2D.Double(0, 0, imageIcon3.getWidth(), imageIcon3.getHeight());
//        Graphics2D g2d31 = imageIcon31.createGraphics();
//        g2d31.setBackground(Color.WHITE);
//        g2d31.fill(new Rectangle(imageIcon31.getWidth(), imageIcon31.getHeight()));
//        g2d31.setClip(shape31);
//        //消除锯齿
//        g2d31.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d31.drawImage(imageIcon3, 0, 0, null);
//        g2d31.dispose();

        //填充昵称|描述
        g2d.setPaint(new Color(0, 0, 0));
        Font font1 = new Font("黑体", Font.BOLD, 16);
        g2d.setFont(font1);
        g2d.drawString("Jerry", 118, 375);
        Font font2 = new Font("黑体", Font.PLAIN, 10);
        g2d.setFont(font2);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("正在转租TA的房源", 175, 375);
        //房源图标|地址|租金
        ImageIcon imageIcon4 = new ImageIcon("/Users/jerry/Downloads/info/pic/Group15.png");
        Image imageLogo4 = imageIcon4.getImage();
        g2d.drawImage(imageLogo4, 54, 405, 8, 10, null);
        Font font3 = new Font("黑体", Font.PLAIN, 12);
        g2d.setFont(font3);
        g2d.setPaint(new Color(180, 180, 180));
        g2d.drawString("美丽珊公寓110号", 66, 415);
        Font font4 = new Font("黑体", Font.PLAIN, 12);
        g2d.setFont(font4);
        g2d.setPaint(new Color(242, 92, 98));
        g2d.drawString("3500元/月", 260, 415);
        //房源描述第一行
        Font font5 = new Font("黑体", Font.PLAIN, 12);
        g2d.setFont(font5);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("2楼，朝南主卧，三室1厅1卫，室友和我都是美女。", 54, 440);
        //房源描述第二行，最后3个用省略号替换
        Font font6 = new Font("黑体", Font.PLAIN, 12);
        g2d.setFont(font6);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("3楼，朝北次卧，三室1厅1卫，室友和我都是渣男。", 54, 458);
        //二维码
        ImageIcon imageIcon5 = new ImageIcon("/Users/jerry/Downloads/info/pic/qr.png");
        Image imageLogo5 = imageIcon5.getImage();
        g2d.drawImage(imageLogo5, 142, 470, 88, 91, null);
        //二维码说明
        Font font7 = new Font("黑体", Font.PLAIN, 10);
        g2d.setFont(font7);
        g2d.setPaint(new Color(165, 165, 165));
        g2d.drawString("长按二维码查看更多详情", 133, 578);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g2d.dispose();
        ImageIO.write(image, "png", new File("/Users/jerry/Downloads/info/pic/me1.png"));
        //存入又拍云
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(image, "png", baos);
//        byte[] result = baos.toByteArray();
//        String bucketName = "mogo-video";
//        String filePath = "jerry/2";
//        UpYun upYun = new UpYun(bucketName, "qianduan", "hy@mogoroom.com");
//        boolean isUpOk = false;
//        try {
//            isUpOk = upYun.writeFile(filePath, result, true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (UpException e) {
//            e.printStackTrace();
//        }
//        if(isUpOk){
//            System.out.println(filePath);
//        }
    }

    @Test
    public void testRoommateNoBorder() throws IOException {
        int width = 885;
        int height = 1488;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        //消除线条锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //填充白板
        ImageIcon imgIcon0 = new ImageIcon("/Users/jerry/Downloads/info/pic/baitu.png");
        Image imageLogo0 = imgIcon0.getImage();
        g2d.drawImage(imageLogo0, 0, 0, 885, 1488, null);
        //填充封面
        //处理封面图片
        URL sourceUrl = new URL("https://mogovideo.mgzf.com/1532331303000/wxfile:/tmp_0a0323f74253d955591978d4d581156e.jpg");
        ImageIcon imageIcon2 = new ImageIcon(sourceUrl);
//        ImageIcon imageIcon2 = new ImageIcon("/Users/jerry/Downloads/info/pic/test1.png");
//        ImageIcon imageIcon2 = new ImageIcon("/Users/jerry/Downloads/info/pic/test22.JPG");
        Image image2 = imageIcon2.getImage();
        int image2Width = image2.getWidth(null);
        int image2Height = image2.getHeight(null);
        //1.宽< 885 & 高 < 663，原图（需计算xy坐标和width+height）
        //2.宽>=高，按照高比列截图
        //3.宽<高，按照宽等比例缩图，然后截图
        int coverX = 0, coverY = 0;
        if(image2Width <= 885 && image2Height <= 663){
            coverX = (885 - image2Width) / 2;
            coverY = (663 - image2Height) / 2;
            g2d.drawImage(image2,  coverX, coverY, image2Width, image2Height, null);
        }else if(image2Width >= image2Height){
            int radio = image2Height / 663;
            int realWidth = image2Width / radio;
            int realHeight = image2Height / radio;
            BufferedImage image2tag = new BufferedImage(realWidth, realHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2dImage2Tag = image2tag.createGraphics();
            g2dImage2Tag.drawImage(image2, 0, 0, realWidth, realHeight, null);
            g2d.drawImage(image2tag, 0, 0, 885, 663, null);
        }else{
            int radio = image2Width / 885;
            int realWidth = image2Width / radio;
            int realHeight = image2Height / radio;
            BufferedImage image2tag = new BufferedImage(realWidth, realHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2dImage2Tag = image2tag.createGraphics();
            g2dImage2Tag.drawImage(image2, 0, 0, realWidth, realHeight, null);
            ByteArrayOutputStream out = (ByteArrayOutputStream)cut(image2tag);
//            ByteArrayOutputStream out = (ByteArrayOutputStream)cut(null, image2tag);
            ImageIcon coverIcon = new ImageIcon(out.toByteArray(), "封面图");
            int coverWeightTmp = coverIcon.getIconWidth();
            if(coverWeightTmp < 885){
                coverX = (885- coverWeightTmp) / 2;
            }else{
                coverWeightTmp = 885;
            }
            int coverHeightTmp = coverIcon.getIconHeight();

            if(coverHeightTmp < 663){
                coverY = (663 - coverHeightTmp) / 2;
            }else{
                coverHeightTmp = 663;
            }
            System.out.println("coverX = " + coverX + ",coverY = " + coverY + ",coverWeightTmp = " + coverWeightTmp + ",coverHeightTmp" + coverHeightTmp);
            g2d.drawImage(coverIcon.getImage(), coverX, coverY, coverWeightTmp, coverHeightTmp, null);
        }
        //填充头像，处理成圆形
        BufferedImage imageIcon3 = ImageIO.read(new File("/Users/jerry/Downloads/info/pic/avatar.jpeg"));
        g2d.drawImage(imageIcon3, 42, 699, 144, 144, null);
        ImageIcon imageIcon32 = new ImageIcon("/Users/jerry/Downloads/info/pic/avatar_corner.png");
        Image imageLogo32 = imageIcon32.getImage();
        g2d.drawImage(imageLogo32, 42, 699, 144, 144, null);
        //填充昵称|描述
        g2d.setPaint(new Color(0, 0, 0));
        Font font1 = new Font("黑体", Font.BOLD, 48);
        g2d.setFont(font1);
        g2d.drawString("我就喜欢", 225, 769);
        Font font2 = new Font("黑体", Font.PLAIN, 30);
        g2d.setFont(font2);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("正在招募室友", 450, 774);
        //室友标准
        Font fontOStd = new Font("黑体", Font.PLAIN, 30);
        g2d.setFont(fontOStd);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("室友标准：健身达人、购物狂、脑抽者", 225, 819);
        //房源图标|地址|租金
        ImageIcon imageIcon4 = new ImageIcon("/Users/jerry/Downloads/info/pic/address.png");
        Image imageLogo4 = imageIcon4.getImage();
        g2d.drawImage(imageLogo4, 42, 858, 24, 30, null);
        Font font3 = new Font("黑体", Font.PLAIN, 30);
        g2d.setFont(font3);
        g2d.setPaint(new Color(180, 180, 180));
        g2d.drawString("美丽珊公寓110号", 78, 891);
        Font font4 = new Font("黑体", Font.PLAIN, 30);
        g2d.setFont(font4);
        g2d.setPaint(new Color(242, 92, 98));
        g2d.drawString("3500元/月", 669, 891);
        //房源描述第一行
        Font font5 = new Font("黑体", Font.PLAIN, 36);
        g2d.setFont(font5);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("2楼，朝南主卧，三室1厅1卫，室友和我都是美女。", 42, 978);
        //房源描述第二行，最后3个用省略号替换
        Font font6 = new Font("黑体", Font.PLAIN, 36);
        g2d.setFont(font6);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("3楼，朝北次卧，三室1厅1卫，室友和我都是渣男。", 42, 1031);
        //二维码
        ImageIcon imageIcon5 = new ImageIcon("/Users/jerry/Downloads/info/pic/qr.png");
        Image imageLogo5 = imageIcon5.getImage();
        g2d.drawImage(imageLogo5, 306, 1092, 264, 273, null);
        //二维码说明
        Font font7 = new Font("黑体", Font.PLAIN, 30);
        g2d.setFont(font7);
        g2d.setPaint(new Color(165, 165, 165));
        g2d.drawString("长按二维码查看更多详情", 280, 1421);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g2d.dispose();
        ImageIO.write(image, "png", new File("/Users/jerry/Downloads/info/pic/roommate2-3.png"));
    }

    @Test
    public void testRoomateBorder() throws IOException {
        int width = 1125;
        int height = 2001;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        //设置背景色
        g2d.setBackground(new Color(242, 92, 98));
        g2d.clearRect(0, 0, width, height);
        //消除线条锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //填充白板
        ImageIcon imgIcon0 = new ImageIcon("/Users/jerry/Downloads/info/pic/baitu.png");
        Image imageLogo0 = imgIcon0.getImage();
        g2d.drawImage(imageLogo0, 120, 339, 885, 1488, null);
        //填充title
        ImageIcon imgIcon1 = new ImageIcon("/Users/jerry/Downloads/info/pic/papa_roommate.png");
        Image imageLogo1 = imgIcon1.getImage();
        g2d.drawImage(imageLogo1, 261, 174, 606, 108, null);
        //填充封面
        //处理封面图片
        URL sourceUrl = new URL("https://mogovideo.mgzf.com/1532331303000/wxfile:/tmp_0a0323f74253d955591978d4d581156e.jpg");
//        URL sourceUrl = new URL("https://mogovideo.mgzf.com/1532609504000/wxfile:/tmp_9e46eedb81a1328e2c3ad5ac3ede3c74.jpg");
        ImageIcon imageIcon2 = new ImageIcon(sourceUrl);
//        ImageIcon imageIcon2 = new ImageIcon("/Users/jerry/Downloads/info/pic/avatar_zip.png");
        Image image2 = imageIcon2.getImage();
        int image2Width = image2.getWidth(null);
        int image2Height = image2.getHeight(null);
        //1.宽< 885 & 高 < 663，原图（需计算xy坐标和width+height）
        //2.宽>=高，按照高比列截图
        //3.宽<高，按照宽等比例缩图，然后截图
        int coverX = 0, coverY = 0;
        if(image2Width <= 885 && image2Height <= 663){
            coverX = (885 - image2Width) / 2;
            coverY = (663 - image2Height) / 2;
            g2d.drawImage(image2, 120 + coverX, 339 + coverY, image2Width, image2Height, null);
        }else if(image2Width >= image2Height){
            int radio = image2Height / 663;
            int realWidth = image2Width / radio;
            int realHeight = image2Height / radio;
            BufferedImage image2tag = new BufferedImage(realWidth, realHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2dImage2Tag = image2tag.createGraphics();
            g2dImage2Tag.drawImage(image2, 0, 0, realWidth, realHeight, null);
            g2d.drawImage(image2tag, 120, 339, 885, 663, null);
        }else{
            //按照倍率缩小图片
//            int radio = image2Width / 885;
            int realWidth = image2Width  * 885 / image2Width;
            int realHeight = image2Height * 885 / image2Width;
            BufferedImage image2tag = new BufferedImage(realWidth, realHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2dImage2Tag = image2tag.createGraphics();
            g2dImage2Tag.drawImage(image2, 0, 0, realWidth, realHeight, null);
            ImageIO.write(image2tag, "png", new File("/Users/jerry/Downloads/info/pic/radio-1.png"));
            /** 倍率缩小图片end**/
            /** 本地测试用 start ** /
//            FileOutputStream out = new FileOutputStream("/Users/jerry/Downloads/info/pic/WechatIMG684-cut.jpeg");
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//            encoder.encode(image2tag);
//            ImageIcon coverIcon = new ImageIcon("/Users/jerry/Downloads/info/pic/WechatIMG684-cut.jpeg");
            /** end **/

            //重置图形的边长大小
            ByteArrayOutputStream out = (ByteArrayOutputStream)cut(image2tag);
            ImageIcon coverIcon = new ImageIcon(out.toByteArray(), "封面图");
            int coverWeightTmp = coverIcon.getIconWidth();
            if(coverWeightTmp < 885){
                coverX = (885- coverWeightTmp) / 2;
            }else{
//                coverX = (coverWeightTmp - 885) / 2;
                coverWeightTmp = 885;
            }
            int coverHeightTmp = coverIcon.getIconHeight();

            if(coverHeightTmp < 663){
                coverY = (663 - coverHeightTmp) / 2;
            }else{
//                coverY = (coverHeightTmp - 663) / 2;
                coverHeightTmp = 663;
            }
            System.out.println("coverX = " + coverX + ",coverY = " + coverY + ",coverWeightTmp = " + coverWeightTmp + ",coverHeightTmp = " + coverHeightTmp);
            g2d.drawImage(coverIcon.getImage(), 120 + coverX, 339 + coverY, coverWeightTmp, coverHeightTmp, null);
        }
        //遮盖圆角title
        ImageIcon imageIcon21 = new ImageIcon("/Users/jerry/Downloads/info/pic/corner1.png");
        ImageIcon imageIcon22 = new ImageIcon("/Users/jerry/Downloads/info/pic/corner2.png");
        Image imageLogo21 = imageIcon21.getImage();
        g2d.drawImage(imageLogo21, 120, 339, 40, 40, null);
        Image imageLogo22 = imageIcon22.getImage();
        g2d.drawImage(imageLogo22, 965, 339, 40, 40, null);
        //填充头像，处理成圆形
        BufferedImage imageIcon3 = ImageIO.read(new File("/Users/jerry/Downloads/info/pic/avatar.jpeg"));
        g2d.drawImage(imageIcon3, 162, 1035, 144, 144, null);
        ImageIcon imageIcon32 = new ImageIcon("/Users/jerry/Downloads/info/pic/avatar_corner.png");
        Image imageLogo32 = imageIcon32.getImage();
        g2d.drawImage(imageLogo32, 162, 1035, 144, 144, null);
        //填充昵称|描述
        g2d.setPaint(new Color(0, 0, 0));
        Font font1 = new Font("黑体", Font.BOLD, 48);
        g2d.setFont(font1);
        g2d.drawString("我就喜欢我就喜欢", 345, 1105);
        Font font2 = new Font("黑体", Font.PLAIN, 30);
        g2d.setFont(font2);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("正在招募室友", 750, 1100);
        //室友标准
        Font fontOStd = new Font("黑体", Font.PLAIN, 30);
        g2d.setFont(fontOStd);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("室友标准：健身达人、购物狂、脑抽者", 345, 1164);
        //房源图标|地址|租金
        ImageIcon imageIcon4 = new ImageIcon("/Users/jerry/Downloads/info/pic/address.png");
        Image imageLogo4 = imageIcon4.getImage();
        g2d.drawImage(imageLogo4, 162, 1222, 24, 30, null);
        Font font3 = new Font("黑体", Font.PLAIN, 36);
        g2d.setFont(font3);
        g2d.setPaint(new Color(180, 180, 180));
        g2d.drawString("美丽珊公寓110号", 198, 1250);
        Font font4 = new Font("黑体", Font.PLAIN, 36);
        g2d.setFont(font4);
        g2d.setPaint(new Color(242, 92, 98));
        g2d.drawString("3500元/月", 795, 1250);
        //房源描述第一行
        Font font5 = new Font("黑体", Font.PLAIN, 36);
        g2d.setFont(font5);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("2楼，朝南主卧，三室1厅1卫，室友和我都是美女。", 162, 1337);
        //房源描述第二行，最后3个用省略号替换
        Font font6 = new Font("黑体", Font.PLAIN, 36);
        g2d.setFont(font6);
        g2d.setPaint(new Color(92, 92, 92));
        g2d.drawString("3楼，朝北次卧，三室1厅1卫，室友和我都是渣男。", 162, 1390);
        //二维码
        ImageIcon imageIcon5 = new ImageIcon("/Users/jerry/Downloads/info/pic/qr.png");
        Image imageLogo5 = imageIcon5.getImage();
        g2d.drawImage(imageLogo5, 426, 1431, 264, 273, null);
        //二维码说明
        Font font7 = new Font("黑体", Font.PLAIN, 30);
        g2d.setFont(font7);
        g2d.setPaint(new Color(165, 165, 165));
        g2d.drawString("长按二维码查看更多详情", 400, 1760);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g2d.dispose();
        ImageIO.write(image, "png", new File("/Users/jerry/Downloads/info/pic/roommate1-1-1.png"));
    }

    @Test
    public void test2() throws IOException {
        int width = 128;
        int height = 64;
        Font font = new Font("宋体", Font.PLAIN, 16);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setBackground(new Color(255, 255, 255));
        g2d.setPaint(new Color(0, 0, 0));
        g2d.clearRect(0, 0, width, height);
        g2d.drawString("名称：娃哈哈纯净水", 0, 10);
        g2d.drawString("产地：浙江杭州", 0, 26);
        g2d.drawString("品牌：娃娃哈哈", 0, 42);
        g2d.drawString("单价：9876543210", 0, 58);
        g2d.setFont(font);
        g2d.dispose();
        ImageIO.write(image, "png", new File("/Users/jerry/Downloads/info/pic/test2.png"));
    }

    @Test
    public void test1() {
        String srcImagePath = "/Users/jerry/Downloads/info/pic/src.jpeg";
        String iconImagePath = "/Users/jerry/Downloads/info/pic/1.png";
        String targetImagePath = "/Users/jerry/Downloads/info/pic/3.jpeg";

        waterMarkImageByIcon(iconImagePath, srcImagePath, targetImagePath, 0, 0, 0, 0.1f);
    }

    public static void waterMarkImageByIcon(String iconPath, String srcImgPath, String targerPath, Integer degree, Integer width, Integer height, float clarity) {
        OutputStream os = null;
        try {
            Image srcImg = ImageIO.read(new File(srcImgPath));
            System.out.println("width:" + srcImg.getWidth(null));
            System.out.println("height:" + srcImg.getHeight(null));
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
            // 得到画笔对象
            // Graphics g= buffImg.getGraphics();
            Graphics2D g = buffImg.createGraphics();
            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);
            if(null != degree){
                // 设置水印旋转
                g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
            }
            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(iconPath);
            // 得到Image对象。
            Image img = imgIcon.getImage();
            float alpha = clarity; // 透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            // 表示水印图片的位置
            g.drawImage(img, width, height, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g.dispose();
            os = new FileOutputStream(targerPath);
            // 生成图片
            ImageIO.write(buffImg, "JPG", os);
            System.out.println("添加水印图片完成!");
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != os){
                    os.close();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

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
     * 图片裁切
     * 选择的总区域必须与图片原始大小相同
     */
    public static OutputStream cut(BufferedImage image) {

        InputStream is = null;
        ImageInputStream iis = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream result = new ByteArrayOutputStream();
        try {
            ImageOutputStream imOut = ImageIO.createImageOutputStream(out);
            ImageIO.write(image, "png",imOut);
            is = new ByteArrayInputStream(out.toByteArray());
            iis = ImageIO.createImageInputStream(is);
            Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
                ImageReader reader = it.next();
                reader.setInput(iis, true);
                ImageReadParam param = reader.getDefaultReadParam();
                int imgWidth = image.getWidth(null);
                int imgHeight = image.getHeight(null);
                int width = 885;
                int height = 663;
                int X = 0;
                int Y = imgHeight / 4;
                System.out.println("imgWidth = " + imgWidth + ", imgHeight = " + imgHeight);
                System.out.println("X = " + X + ",Y = " + Y);
                Rectangle rect = new Rectangle(0, Y, width, height);
                param.setSourceRegion(rect);
                BufferedImage bi = reader.read(0, param);
                ImageIO.write(bi, "png", result);
//                ImageIO.write(bi, "jpg", new File("/Users/jerry/Downloads/info/pic/test22-cut.jpg"));
//            }
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
        return result;
    }

}
