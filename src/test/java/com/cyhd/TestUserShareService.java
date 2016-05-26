package com.cyhd;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.common.util.MoneyUtils;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.impl.UserIncomeService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.impl.UserShareService;
import com.cyhd.service.util.CreatQRCodeImgUtil;

public class TestUserShareService {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		UserShareService shareService  = context.getBean(UserShareService.class);
		UserIncomeService userIncomService = context.getBean(UserIncomeService.class);
		UserService userService = context.getBean(UserService.class);
		
		User user = userService.getUserById(1);
		String SHARE_URL = "Http://www.mapi.lieqicun.cn";
		String share_pic_path = UserShareService.class.getResource("/").getPath()+"images/";
		BufferedImage  sharOrderBG = ImageIO.read(new File(share_pic_path+"tmp.png"));
		BufferedImage backgroud  = new BufferedImage(sharOrderBG.getWidth(),sharOrderBG.getHeight(), sharOrderBG.getType());
		backgroud.setData(sharOrderBG.getData());
		
		//邀请码的字体
		Font InviteFont = new Font("华文细文", Font.PLAIN, 29);
		Color inviteColor = new Color(0xff,0xff, 0xff);
		UserIncome userIncome = userIncomService.getUserIncome(user.getId());
		String content = SHARE_URL+"?u="+user.getUser_identity();
		BufferedImage qrCode = CreatQRCodeImgUtil.getBufferedImage(content, null, 231, 244);
		Graphics2D g = backgroud.createGraphics();
		g.setComposite(AlphaComposite.Src);
		// 2、设置对线段的锯齿状边缘处理
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		//加上收徒二维码
		g.drawImage(qrCode, 452,720,231,244, null);
		String userIncomeStr = MoneyUtils.fen2yuanS(userIncome == null ? 0:userIncome.getIncome());
		g.setFont(InviteFont);
		g.setColor(inviteColor);
		//添加收入
		g.drawString(userIncomeStr, 290, 290);
		//添加邀请码
		g.drawString(String.valueOf(user.getUser_identity()), 302, 460);
		
		g.dispose();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(backgroud, "png", output);
		String path = share_pic_path+"test.png";
		FileUtils.writeByteArrayToFile(new File(path), output.toByteArray());
		System.err.println("ok : "+path);
	}
}
