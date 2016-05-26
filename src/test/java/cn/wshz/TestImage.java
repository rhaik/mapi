package cn.wshz;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


public class TestImage {
	
	
	
	public static void main(String[] args) throws Exception {
		//test();
		
	}

	private static void test() throws IOException, FileNotFoundException {
		String imagePath="D:\\work\\mapi\\mapi\\src\\main\\resources\\images\\down_src_3.png";
		BufferedImage image = ImageIO.read(new File(imagePath));
		
		Graphics2D g = image.createGraphics();
		
		String inviteCode = "12345643";
		Font InviteFont = new Font("华文细文", Font.PLAIN, 28);
		Color inviteColor = new Color(0xff,0xff,0xff );
		
		g.setFont(InviteFont);
		g.setColor(inviteColor);
		//g.drawString(inviteCode, 188, 462);
		//g.drawString(inviteCode, 179, 506);
		g.drawString(inviteCode, 189, 255);
		FileOutputStream os = new FileOutputStream("d://test\\test3.JPG");
		ImageIO.write(image, "JPG", os);
		if(os != null){
			os.close();
		}
		System.out.println("ok");
	}
}
