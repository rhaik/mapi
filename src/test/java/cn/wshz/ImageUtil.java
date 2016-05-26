package cn.wshz;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.cyhd.service.impl.QiniuService;
import com.cyhd.service.util.CreatQRCodeImgUtil;
import com.google.zxing.WriterException;


public class ImageUtil {
	
	public  void composition(BufferedImage background,BufferedImage qrImage, int x, int y){
		Graphics2D g = background.createGraphics();
		
		g.setComposite(AlphaComposite.Src);
		// 2、设置对线段的锯齿状边缘处理
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g.drawImage(qrImage, x, y, null);
		g.setComposite(AlphaComposite.SrcAtop);
		
		g.dispose();
	
	}
	
	public static void main(String[] args) throws IOException, WriterException {
		
		ImageUtil imageUtil =  new ImageUtil();
		
		BufferedImage image = ImageIO.read(new File("d://test.png"));
		
		String content="Swing程序员通常只要按几下快捷键即可生成成百上千的匿名类。在多数情况下，只要遵循接口、不违反SPI子类型的生命周期(SPI subtype lifecycle)，这样做也无妨。 但是不要因为一个简单的原因——它们会保存对外部类的引用，就频繁的使用匿名、局部或者内部类。因为无论它们走到哪，外部类就得跟到哪。例如，在局部类的域外操作不当的话，那么整个对象图就会发生微妙的变化从而可能引起内存泄露。"+
				"规则：在编写匿名、局部或内部类前请三思能否将它转化为静态的或普通的顶级类，从而避免方法将它们的对象返回到更外层的域中。"+
				"注意：使用双层花括号来初始化简单对象：";
		
		BufferedImage qr = CreatQRCodeImgUtil.getBufferedImage(content, "d:\\logo.png", null, null);
		
		imageUtil.composition(image,qr, 100, 100);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image, "png", output);
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		QiniuService qiniuService = context.getBean(QiniuService.class);
		String imageUrl = 	qiniuService.uploadMediaFile(String.valueOf(System.currentTimeMillis())+".png", output.toByteArray());
	
		System.out.println(imageUrl);
		
	}
}
