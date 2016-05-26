package cn.wshz;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.impl.UserShareService;
import com.cyhd.service.util.GlobalConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class TestQR {

	public static void main(String[] args) {
		try {
			// ApplicationContext context = new
			// ClassPathXmlApplicationContext("applicationContext.xml");

			//String content = "http://www.baidu.com/s?wd=java 程序员";
			String content="weixin://profile/gh_102c3ae62511";
			String path = "D:/";

			MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

			Map hints = new HashMap();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			BitMatrix bitMatrix = multiFormatWriter.encode(content,BarcodeFormat.QR_CODE, 310, 310, hints);
			//File file1 = new File(path, "餐巾纸.jpg");
			// MatrixToImageWriter.writeToFile(bitMatrix, "jpg", file1);
			BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
			// UserShareService shareService =
			// context.getBean(UserShareService.class);
			// shareService.
			drawImage(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void drawImage(BufferedImage image) {
		try {
			Font font = new Font("宋体", Font.BOLD, 30);

			// 水印文字颜色
			Color color = new Color(255, 128, 3);

			BufferedImage background_empty = ImageIO.read(new File("d://test.jpg"));
			BufferedImage bufferImage = new BufferedImage(background_empty.getWidth(), background_empty.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bufferImage.createGraphics();

			g.setComposite(AlphaComposite.Src);
			g.setColor(color);
			g.setFont(font);
			// 2、设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(background_empty, 0, 0, null);
			g.drawImage(image, 223, 718, null);
			g.dispose();

			FileOutputStream os = new FileOutputStream("d://createImage.jpg");
			ImageIO.write(bufferImage, "JPG", os);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	 	
	
	
}
