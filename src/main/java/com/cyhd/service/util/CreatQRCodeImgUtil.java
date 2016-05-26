package com.cyhd.service.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.impl.QiniuService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
/**
 *  生成带logo的二维码
 *  logo需要很小的大小 不然有影响
 *
 */
public class CreatQRCodeImgUtil {

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF; 
	
	//二维码的大小
	private static final int WIDTH=320;
	private static final int HEIGHT=320;
	
	/**
	 *  该方法生成二维码图片 生成在磁盘上
	 * @param content 要生成二维码的content 
	 * @param imgPath 二维码生成的绝对路径
	 * @param logoPath 二维码中间logo绝对地址 这个可以是网络地址
	 * @param width 为null的默认值是320
	 * @param height 同上
	 * @throws Exception
	 */
	public static void get2CodeImageFile(String format,String content,String imgPath,String logoPath,Integer  width, Integer height) throws Exception{
		
		BitMatrix bitMatrix = getBitMatrix(content, width, height);
		File qrcodeFile = new File(imgPath);  
		CreatQRCodeImgUtil.writeToFile(bitMatrix, format, qrcodeFile, logoPath);  
	}
	
	public static void writeToStream(String format,String content,String logoPath,Integer  width, Integer height,OutputStream stream) throws WriterException, IOException{
		BufferedImage image = getBufferedImage( content, logoPath, width, height);
		ImageIO.write(image, format, stream);
	}
	
	/**
	 * 得到BufferedImage对象 方便和其他图片合成
	 * @param content 二维码上的内容
	 * @param logoPath 图片的绝对位置 
	 * @param width  ：
	 * @param height ：生成的二维码大小
	 * @return  
	 * @throws IOException
	 * @throws WriterException
	 */
	public static BufferedImage getBufferedImage(String content,String logoPath,Integer  width, Integer height) throws IOException, WriterException{
		BitMatrix bitMatrix = getBitMatrix(content, width, height);
		return addLogo(bitMatrix, logoPath);
	}
	
	private static BitMatrix  getBitMatrix(String content,Integer width,Integer height) throws WriterException{
		if (width == null) {
			width = WIDTH;
		}
		if (height == null) {
			height = HEIGHT;
		}

		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, 1);
		return new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE,width, height, hints);
	}
	
	/**
	 * 
	 * @param matrix 二维码矩阵相关
	 * @param format 二维码图片格式
	 * @param file 二维码图片文件
	 * @param logoPath logo路径 可以是网络上的路径
	 * @throws IOException
	 */
	public static void writeToFile(BitMatrix matrix,String format,File file,String logoPath) throws IOException {
		BufferedImage image = addLogo(matrix, logoPath);
		
		if(!ImageIO.write(image, format, file)){
			throw new IOException("Could not write an image of format " + format + " to " + file);  
		}
	}

	private static BufferedImage addLogo(BitMatrix matrix, String logoPath)throws IOException, MalformedURLException {
		BufferedImage image = toBufferedImage(matrix);
		
		//带有中间Logo
		if(logoPath != null){
			Graphics2D gs = image.createGraphics();
			
			BufferedImage img = null;
			//载入logo
			//网络地址
			if(logoPath.startsWith("http")){
				img = ImageIO.read(new URL(logoPath));
			}else{//磁盘地址
				img = ImageIO.read(new File(logoPath));
			}
			//放在中间 
			gs.drawImage(img,(image.getWidth()-img.getWidth())/2,(image.getHeight()-img.getHeight())/2,null);
			
			gs.dispose();
			img.flush();
		}
		return image;
	}
	
	public static BufferedImage toBufferedImage(BitMatrix matrix){
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}
		return image;	
	}

	
	public static void main(String[] args) {
		try {
//			get2CodeImageFile("JPG","http://www.baidu.com/s?wd=QRCode 在线制作","d:\\logocodeNoLogo.jpg",null,null,null);
//			get2CodeImageFile("JPG","http://www.baidu.com/s?wd=QRCode 在线制作","d:\\logocode.jpg","d:\\logo.png",null,null);
//			get2CodeImageFile("JPG","http://www.baidu.com/s?wd=QRCode 在线制作","d:\\logocode.png","d:\\logo.png",null,null);
//			get2CodeImageFile("JPG","https://www.baidu.com/s?wd=%E4%BA%8C%E7%BB%B4%E7%A0%81%E6%9C%89%E6%95%88%E5%8C%BA%E5%9F%9F&rsp=0&f=1&oq=%E4%BA%8C%E7%BB%B4%E7%A0%81%E5%88%B6%E5%AE%9A%E5%8C%BA%E5%9F%9F&tn=baiduhome_pg&ie=utf-8&rsv_idx=2&rsv_pq=cf1bdaff00004903&rsv_t=6270%2Bjr3ps7ENUJJAULKbrAlGcUg78bmF07wq%2B%2BJzUzhVfGyDW4bVWoKJfiSKlIDL2jK&rsv_ers=xn0&rs_src=0","d:\\logocode2.png","d:\\logo.png",null,null);
			
			String content="Swing程序员通常只要按几下快捷键即可生成成百上千的匿名类。在多数情况下，只要遵循接口、不违反SPI子类型的生命周期(SPI subtype lifecycle)，这样做也无妨。 但是不要因为一个简单的原因——它们会保存对外部类的引用，就频繁的使用匿名、局部或者内部类。因为无论它们走到哪，外部类就得跟到哪。例如，在局部类的域外操作不当的话，那么整个对象图就会发生微妙的变化从而可能引起内存泄露。"+
"规则：在编写匿名、局部或内部类前请三思能否将它转化为静态的或普通的顶级类，从而避免方法将它们的对象返回到更外层的域中。"+
"注意：使用双层花括号来初始化简单对象：";
			
			ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
			QiniuService qiniuService = context.getBean(QiniuService.class);
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			writeToStream("png", content,"d:\\logo.png",null,null,stream);
			
			String imageUrl = 	qiniuService.uploadMediaFile(String.valueOf(System.currentTimeMillis())+".png", stream.toByteArray());
		
			System.out.println(imageUrl);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}