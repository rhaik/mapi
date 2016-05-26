package cn.wshz;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class CreatQRCodeImgUtil {

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF; 
	
	/**
	 *  最终调用该方法生成二维码图片
	 * @param content 要生成二维码的content 
	 * @param imgPath 二维码生成的绝对路径
	 * @param logoPath 二维码中间logo绝对地址 这个可以是网络地址
	 * @param width 为null的默认值是320
	 * @param height 同上
	 * @throws Exception
	 */
	public static void get2CodeImageFile(String content,String imgPath,String logoPath,Integer  width, Integer height) throws Exception{
		//这个其实没有什么影响 
		String format = "png";
		
		if(width == null){
			width = 320;
		}
		if(height == null){
			height = 320;
		}
		
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(); 
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		File qrcodeFile = new File(imgPath);  
		CreatQRCodeImgUtil.writeToFile(bitMatrix, format, qrcodeFile, logoPath);  
	}
	
	/*public static void get2CodeImageBufferedImage(String content,){
		
	}*/
	
	/**
	 * 
	 * @param matrix 二维码矩阵相关
	 * @param format 二维码图片格式
	 * @param file 二维码图片文件
	 * @param logoPath logo路径 可以是网络上的路径
	 * @throws IOException
	 */
	public static void writeToFile(BitMatrix matrix,String format,File file,String logoPath) throws IOException {
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
		
		if(!ImageIO.write(image, format, file)){
			throw new IOException("Could not write an image of format " + format + " to " + file);  
		}
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
	
	public static void writeToStream(BitMatrix matrix, String format,OutputStream stream) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format "
					+ format);
		}
	}

	
	public static void main(String[] args) {
		try {
			get2CodeImageFile("http://www.baidu.com/s?wd=QRCode 在线制作","d:\\logocodeNoLogo.jpg",null,null,null);
			get2CodeImageFile("http://www.baidu.com/s?wd=QRCode 在线制作","d:\\logocode.jpg","d:\\logo.png",null,null);
			get2CodeImageFile("http://www.baidu.com/s?wd=QRCode 在线制作","d:\\logocode.png","d:\\logo.png",null,null);
			get2CodeImageFile("https://www.baidu.com/s?wd=%E4%BA%8C%E7%BB%B4%E7%A0%81%E6%9C%89%E6%95%88%E5%8C%BA%E5%9F%9F&rsp=0&f=1&oq=%E4%BA%8C%E7%BB%B4%E7%A0%81%E5%88%B6%E5%AE%9A%E5%8C%BA%E5%9F%9F&tn=baiduhome_pg&ie=utf-8&rsv_idx=2&rsv_pq=cf1bdaff00004903&rsv_t=6270%2Bjr3ps7ENUJJAULKbrAlGcUg78bmF07wq%2B%2BJzUzhVfGyDW4bVWoKJfiSKlIDL2jK&rsv_ers=xn0&rs_src=0","d:\\logocode2.png","d:\\logo.png",null,null);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
