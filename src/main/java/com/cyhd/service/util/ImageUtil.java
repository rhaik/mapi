package com.cyhd.service.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.common.util.DateUtil;
import com.cyhd.service.impl.QiniuService;
import com.google.zxing.WriterException;


public class ImageUtil {
	
	
	
	/**
	 * 给确定的背景background图上指定位置x,y添加二维码qrImage
	 * @param background
	 * @param qrImage
	 * @param x
	 * @param y
	 */
	public static  void composition(BufferedImage background,BufferedImage qrImage, int x, int y,int width, int height){
		Graphics2D g = background.createGraphics();
		
		g.setComposite(AlphaComposite.Src);
		// 2、设置对线段的锯齿状边缘处理
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g.drawImage(qrImage, x, y,width,height, null);
		g.setComposite(AlphaComposite.SrcAtop);
		
		g.dispose();
	
	}
	/** 
     * 给图片添加水印  
     *   
     * @param filePath  
     *            需要添加水印的图片的路径  
     * @param markContent  
     *            水印的文字  
     * @param markContentColor  
     *            水印文字的颜色  
     * @param qualNum  
     *            图片质量  
     * @return  
	 * @throws Exception 
     */   
    public static BufferedImage createMark(InputStream file, String markContent, Color markContentColor, String type) throws Exception {  
    	ByteArrayOutputStream output = null;
		try{ 
			BufferedImage bimage = ImageIO.read(file);
			Font font = new Font("华文细文", Font.PLAIN, 28);
	        int  width = bimage.getWidth();  
	        int  height = bimage.getHeight();   
	        Graphics2D g = bimage.createGraphics();  
	        g.setColor(markContentColor);  
			g.setFont(font); 
	        g.drawString(markContent, width - (markContent.length() * 19 + 80 ) , height - 80 );  // 添加水印的文字和设置水印文字出现的内容
	        String date = DateUtil.format(new Date(), "yyyy-MM-dd");
	        g.drawString(date, width -(date.length()*14+80)  , height - 40);
	        g.dispose();
	        //output = new ByteArrayOutputStream();
	        return bimage;
			//ImageIO.write(bimage, "png", output);
		}catch(Exception e){
			throw e;
		}finally{
			if(output != null){
				output.close();
			}
		}
    }  
    /** 
     *  
     * 自己设置压缩质量来把图片压缩成byte[] 
     *  
     * @param image 
     *            压缩源图片 
     * @param quality 
     *            压缩质量，在0-1之间， 
     * @return 返回的字节数组 
     */  
    public static byte[] bufferedImage(BufferedImage image, float quality) {  
        // 得到指定Format图片的writer  
        Iterator<ImageWriter> iter = ImageIO  
                .getImageWritersByFormatName("jpeg");// 得到迭代器  
        ImageWriter writer = (ImageWriter) iter.next(); // 得到writer  
  
        // 得到指定writer的输出参数设置(ImageWriteParam )  
        ImageWriteParam iwp = writer.getDefaultWriteParam();  
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // 设置可否压缩  
        iwp.setCompressionQuality(quality); // 设置压缩质量参数  
  
        iwp.setProgressiveMode(ImageWriteParam.MODE_DISABLED);  
  
        ColorModel colorModel = ColorModel.getRGBdefault();  
        // 指定压缩时使用的色彩模式  
        iwp.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel,  
                colorModel.createCompatibleSampleModel(16, 16)));  
  
        // 开始打包图片，写入byte[]  
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // 取得内存输出流  
        IIOImage iIamge = new IIOImage(image, null, null);  
        try {  
            // 此处因为ImageWriter中用来接收write信息的output要求必须是ImageOutput  
            // 通过ImageIo中的静态方法，得到byteArrayOutputStream的ImageOutput  
            writer.setOutput(ImageIO  
                    .createImageOutputStream(byteArrayOutputStream));  
            writer.write(null, iIamge, iwp);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return byteArrayOutputStream.toByteArray();  
    }  
    /** 
     * /** 
     *  
     * @param args 
     */  
    public static byte[] resize(BufferedImage bimage, int w, int h) throws IOException {  
    	int width = bimage.getWidth();
    	int height = bimage.getHeight();
    	 if (width / height > w / h) {  
    		 int toh = (int) (height * w / width);
    		 if(toh > h) {
    			 w = (int) (width * h / height);
    		 } else {
    			 h = toh;
    		 }
         } else {  
        	 int tow = (int) (width * h / height);
        	 if(tow > w) {
        		 h = (int) (height * w / width);
    		 } else {
    			 w = tow;
    		 }
         }  
        try {  
        	
            BufferedImage _image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);  
            _image.getGraphics().drawImage(bimage, 0, 0, w, h, null); // 绘制缩小后的图   
         // 开始打包图片，写入byte[]  
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // 取得内存输出流 
            ImageIO.write(_image, "JPG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }
		return null;  
    } 
 
    
	public static void main(String[] args) throws IOException, WriterException {
		
//		ImageUtil imageUtil =  new ImageUtil();
//		
//		BufferedImage image = ImageIO.read(new File("D:\\background\\2.png"));
//		
//		String content="http://www.baidu.com/s?wd=使用双层花括号来初始化简单对象";
//		BufferedImage qr = CreatQRCodeImgUtil.getBufferedImage(content, "d:\\logo.png", 254, 254);
//		
////		int y = (image.getHeight() - qr.getHeight())/2;
////		int x = (image.getWidth() - qr.getWidth())/2;
//		int y = 1024;
//		int x = 27;
//		
//		imageUtil.composition(image,qr, x, y,75,75);
//		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
//		ImageIO.write(image, "png", output);
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		QiniuService qiniuService = context.getBean(QiniuService.class);
		String imageUrl = 	qiniuService.uploadMediaFile(String.valueOf(System.currentTimeMillis())+".png", output.toByteArray());
	
		
		
		System.err.println(imageUrl);
		
	}
}
