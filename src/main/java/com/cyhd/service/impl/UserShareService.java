package com.cyhd.service.impl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.MoneyUtils;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.util.CreatQRCodeImgUtil;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RedisUtil;
import com.google.zxing.WriterException;

@Service
public class UserShareService extends BaseService {

	@Resource
	UserIncomeService userIncomService;

	@Resource
	UserFriendService userFriendService;

	@Resource
	private QiniuService qiniuService;
	
	@Resource
	private UserService userService;
	
	private static int width = 132;

	private static String shareBaseDir = GlobalConfig.share_base_dir;
	
	private static String imageBase = "/share/";

	private static String templateDir = shareBaseDir +imageBase+ "template/";

	public static String shareDir = shareBaseDir + imageBase+ "images/";
	
	BufferedImage background_empty = null;
	
	BufferedImage background_avatar = null;

//	//生成分享的三个图片
	private BufferedImage backgroud_1 = null;
	private BufferedImage backgroud_2 = null;
	private BufferedImage backgroud_3 = null;
	
	private static final Object backgroud_lock_1 = new Object();
	private static final Object backgroud_lock_2 = new Object();
	private static final Object backgroud_lock_3 = new Object();
	
	private static int namex = 310;
	private static int namey = 210;
	
	private static float alpha = 1.0f;
	private static int basex = 590;
	private static int basey = 320;
	private static int gap = 90;
	
	// 水印横向位置
	private static int positionWidth = 313;
	// 水印纵向位置
	private static int positionHeight = 16;
	
	public  String share_pic_path=null;
	//邀请码的字体
	private Font InviteFont = new Font("华文细文", Font.PLAIN, 29);
	private Color inviteColor = new Color(0xff,0xff, 0xff);
	
	//private final ExecutorService threadES = Executors.newFixedThreadPool(120);
	/**图片的最小索引 0+index **/
	private final int pic_min_index = 1;
	
	@Resource(name=RedisUtil.NAME_ALIYUAN)
	private IJedisDao userShareCache;
	
	@PostConstruct
	private void initSharePath(){
		share_pic_path = UserShareService.class.getResource("/").getPath()+"images/";
		logger.error("share_pic_path:{}",share_pic_path);
	}
	
	private void init(){
		try{
			background_empty = ImageIO.read(new File(templateDir + "share-empty.jpg"));
			background_avatar = ImageIO.read(new File(templateDir + "share-default-avatar.jpg"));
		}catch(Exception e){
			logger.error("UserShare init error!", e);
		}
		
	}
	private BufferedImage sharOrderBG = null;
	
	private void initShareOrderBackground(){
		try {
			if(sharOrderBG == null){
				synchronized (this) {
					if(sharOrderBG == null){
						sharOrderBG = ImageIO.read(new File(share_pic_path+"share_order_bg.png"));
					}
				}
			}
		} catch (Exception e) {
			logger.error("load shareorder backGround ,cause:",e);
		}
	}
	
	/**
	 * 用户分享的页面的地址
	 * @param u
	 * @return
	 */
	public String getUserShareUrl(User u){
		String destUrl = Constants.share_wx_pre_link + u.getInvite_code();
		return destUrl;
	}
	
	public String makeShareImage(User u) {
		if(u == null){
			u = new User();
			u.setId(1);
			u.setUser_identity(11111);
		}
		int dayGap = DateUtil.getTwoDatesDifDay(new Date(), u.getCreatetime(), true);
		int friendCount = userFriendService.countUserFriends(u.getId());

		UserIncome ui = userIncomService.getUserIncome(u.getId());

		String avatar = u.getAvatar();
		String userName = u.getName();
		//String avatar="http://wx.qlogo.cn/mmopen/ajNVdqHZLLBLBticuq1beWGpHEIyrM7ZMy6cybICzv6kJZibbqmI8UoicSJnf6sjblSHMkGpBiatGtibxdAs6mmkx1Q/0";
//		if (!StringUtils.isEmpty(u.getAvatar())) {
//			avatar = u.getAvatar();
//		}
		try {
			BufferedImage rounded = null;
			if(!StringUtils.isEmpty(avatar)){
				BufferedImage icon = null;
				if(avatar.startsWith("http")){
					icon = ImageIO.read(new URL(avatar));
				}else{
					icon = ImageIO.read(new File(avatar));
				}
				BufferedImage resized = createResizedCopy(icon, width, width, true);
				rounded = makeRoundedCorner(resized, width);
			}

			String fileName = u.getUser_identity() + "_" + System.currentTimeMillis() + ".jpg";
			return markImage(rounded, fileName, userName, dayGap + "", MoneyUtils.fen2yuanS(ui.getIncome()) , friendCount+"", MoneyUtils.fen2yuanS(ui.getInvite_total()));
		} catch (Exception e) {
			logger.error("UserShareService.makeShareImage error!", e);
			e.printStackTrace();
		}
		return null;
	}

	public String markImage(Image img, String fileName, String userName, String days, String income, String number, String inviteIncome) throws Exception {
		OutputStream os = null;
		try {
			// 水印文字字体
			Font font = new Font("宋体", Font.BOLD, 30);
			
			Font font_name = new Font("宋体", Font.BOLD, 36);
			// 水印文字颜色
			Color color = new Color(255,128,3);
			
			Color color_name = Color.WHITE;
			
			String shareFileName = shareDir + fileName;
			if(this.background_empty == null){
				init();
			}
			BufferedImage srcImg = background_empty;
			if(img == null){
				srcImg = background_avatar;
			}
			
			BufferedImage buffImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_INT_RGB);

			// 1、得到画笔对象
			Graphics2D g = buffImg.createGraphics();

			g.setComposite(AlphaComposite.Src);
			g.setColor(color);
			g.setFont(font);
			// 2、设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(srcImg, 0, 0, null);

			// 4、水印图片的路径 水印图片一般为gif或者png的，这样可设置透明度
//			ImageIcon imgIcon = new ImageIcon(iconPath);
//
//			// 5、得到Image对象。
//			Image img = imgIcon.getImage();

			g.setComposite(AlphaComposite.SrcAtop);

			if(img != null){
				g.drawImage(img, positionWidth, positionHeight, 123, 123, null);
			}
			
			g.setColor(color_name);
			g.setFont(font_name);
			g.drawString(userName, calculateX(days, namex), namey);
			
			g.setColor(color);
			g.setFont(font);
			g.drawString(days+"", calculateX(days, basex), basey);
			g.drawString(income+"", calculateX(income,basex), basey + gap);
			g.drawString(number+"", calculateX(number, basex), basey + 2*gap);
			g.drawString(inviteIncome+"", calculateX(inviteIncome, basex), basey + 3*gap);
			
			// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN));
			// 7、释放资源
			g.dispose();

			// 8、生成图片
			os = new FileOutputStream(shareFileName);
			ImageIO.write(buffImg, "JPG", os);

			return GlobalConfig.base_url + imageBase+ "images/" + fileName;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static int calculateX(String value, int basex){
		String x = String.valueOf(value);
		int l = x.length();
		return basex - l*10;
	}

	public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = output.createGraphics();

		// This is what we want, but it only does hard-clipping, i.e. aliasing
		// g2.setClip() ;

		// so instead fake soft-clipping by first drawing the desired clip shape
		// in fully opaque white with antialiasing enabled...
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.fill(new RoundRectangle2D.Float(0, 0, width, width, cornerRadius, cornerRadius));

		// ... then compositing the image on top,
		// using the white shape from above as alpha source
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(image, 0, 0, null);

		g2.dispose();

		return output;
	}

	public static BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight, boolean preserveAlpha) {
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
		Graphics2D g = scaledBI.createGraphics();
		if (preserveAlpha) {
			g.setComposite(AlphaComposite.Src);
		}
		g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
		g.dispose();
		return scaledBI;
	}
	
	
	//TODO 修改为Future模式 可能效果好些 改啦老师异常 不可控
	public String[] getUserShareURLByQiNiuPic(User user){
		
		//如果用户生成过分享图片则不用再去生成
		if(user.isGen_share_pic()){
			//return getPictureURLByDefault(user);
		}
		
		String[] pic_url = new String[3];
		if(share_pic_path == null){
			initSharePath();
		}
		
		for(int i = 0; i <pic_url.length; i++){
				try {
					pic_url[i]= getUserShareURLByQiNiuPic(user,i);
				} catch (Exception e) {
					if(logger.isErrorEnabled()){
						logger.error("生成用户:{},的第{}张图片异常,cause by:{}",user.getId(),i,e);
					}
				}
		}
		for(int i = 0; i <pic_url.length; i++){
			if( StringUtils.isBlank(pic_url[i]) ){
				return pic_url;
			}
		}
		userService.setGenSharePic(user.getId());
		return pic_url;
	}
	/***
	 * 如果用户生成过 就直接返回图片地址</br/>
	 * 上线后去掉注释 测试环境方便测试
	 * @param user
	 * @return
	 */
	public String[] getPictureURLByDefault(User user) {
		//TODO 方便测试 注释掉
		if(user.isGen_share_pic()){
			String[] pic_url = new String[3];
			String fileName = null;
			for(int i = 0 ; i < 3; i++){
				fileName = user.getInvite_code()+"_"+(i+pic_min_index)+".png";
				pic_url[i] = qiniuService.getResourceURLByFileName(fileName);
			}
			return pic_url;
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		// String avatar = templateDir + "/" + "a2.jpeg";
		// BufferedImage icon = ImageIO.read(new File(avatar));
		//
		// //BufferedImage pic = ImageIO.read(new
		// File("/home/june/桌面/zhang.jpg"));
		// BufferedImage resized = createResizedCopy(icon, width, width, true);
		// // ImageIO.write(resized, "jpg", new File(shareDir + "/a22.jpg"));
		//
		// BufferedImage rounded = makeRoundedCorner(resized, width);
		// ImageIO.write(rounded, "png", new File(shareDir + "/1111.jpg"));

//		UserShareService service = new UserShareService();
//		service.makeShareImage(null);
//		File file = new File("");
//		System.out.println(file.getAbsolutePath());
//		String path = UserShareService.class.getResource("/").getPath();
//		System.out.println(path);
		
	}
	
		/**
		 * 
		 * @param content 二维码中扫描后的内容
		 * @param pic_name 存放在七牛上的名字
		 * @param backgroundName 背景图片的名字
		 * @param x 二维码在背景图上的X 
		 * @param y 二维码在图片山的y<br/>
		 * 如果x,y都为null 就放在中间
		 * @return  七牛返回图片的URL
		 * @throws Exception 
		 */
		private String genUserShareURLByQiNiu(String content,String pic_name,BufferedImage background,Integer positionWidth,Integer positionHeight,Integer width, Integer height,String inviteCode,int inviteX,int inviteY) throws Exception{
			
			ByteArrayOutputStream output = null;
			try{
				
				BufferedImage qr = CreatQRCodeImgUtil.getBufferedImage(content, share_pic_path+"logo.png", 250, 250);
					
				if(positionWidth == null){
					positionWidth = (background.getWidth() - width)/2;
				}
				if(positionHeight == null){
					positionHeight = (background.getHeight() - height)/2;
				}
				
				Graphics2D g = background.createGraphics();
				g.setComposite(AlphaComposite.Src);
				// 2、设置对线段的锯齿状边缘处理
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				
				g.drawImage(qr, positionWidth,positionHeight,width,height, null);
				g.setComposite(AlphaComposite.SrcAtop);
				//添加邀请码
				g.setFont(InviteFont);
				g.setColor(inviteColor);
				g.drawString(inviteCode, inviteX, inviteY);
				
				g.dispose();
				
				output = new ByteArrayOutputStream();
				ImageIO.write(background, "png", output);
				return qiniuService.uploadMediaFile(pic_name, output.toByteArray());
			}catch(Exception e){
				throw e;
			}finally{
				if(output != null){
					try {
						output.close();
					} catch (Exception e2) {
					}
				}
			}
		}
		
		/**
		 * 图片位置<br/>
		 * resources/images/down_src_1.png
		 * @param 
		 * @return
		 * @throws Exception 
		 */
		private boolean getUserShareURLByQiNiuPic_1(String content,String pic_name,String inviteCode){
			try {
				if(qiniuService.checkFileExist(pic_name)){
					return true;
				}
				
				if(backgroud_1 == null){
					synchronized (backgroud_lock_1) {
						if(backgroud_1 == null){
							backgroud_1 = ImageIO.read(new File(share_pic_path+"down_src_1.png"));
						}
					}
				}
				
				BufferedImage backgroud  = new BufferedImage(backgroud_1.getWidth(),backgroud_1.getHeight(), backgroud_1.getType());
				backgroud.setData(backgroud_1.getData());
				
				String ret = genUserShareURLByQiNiu(content, pic_name, backgroud, null, 230,160,160,inviteCode,184, 462);
				return org.apache.commons.lang.StringUtils.isNotBlank(ret);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("生成用户-id:{}，第一张图片异常：{}",inviteCode,e);
				}
				return qiniuService.checkFileExist(pic_name);
			}
		}

		private boolean getUserShareURLByQiNiuPic_2(String content,String pic_name,String inviteCode) {
			 try {
				 if(qiniuService.checkFileExist(pic_name)){
					 return true;
				 }
				 if(backgroud_2 == null){
						synchronized (backgroud_lock_2) {
							if(backgroud_2 == null){
								backgroud_2 = ImageIO.read(new File(share_pic_path+"down_src_2.png"));
							}
						}
					}
				 
				 BufferedImage backgroud  = new BufferedImage(backgroud_2.getWidth(),backgroud_2.getHeight(), backgroud_2.getType());
				 backgroud.setData(backgroud_2.getData());
				 
				String ret =  genUserShareURLByQiNiu(content, pic_name, backgroud, 20, 30,160,160,inviteCode,175, 506);
				return org.apache.commons.lang.StringUtils.isNotBlank(ret);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("生成用户-inviteCode:{}，第2张图片异常：{}",inviteCode,e);
				}
				return qiniuService.checkFileExist(pic_name);
			}
		}
		private boolean getUserShareURLByQiNiuPic_3(String content,String pic_name,String inviteCode) {
			try {
				if(qiniuService.checkFileExist(pic_name)){
					return true;
				}
				
				 if(backgroud_3 == null){
						synchronized (backgroud_lock_3) {
							if(backgroud_3 == null){
								backgroud_3 = ImageIO.read(new File(share_pic_path+"down_src_3.png"));
							}
						}
					}
				 
				 BufferedImage backgroud  = new BufferedImage(backgroud_3.getWidth(),backgroud_3.getHeight(), backgroud_3.getType());
				 backgroud.setData(backgroud_3.getData());
				 
				 String ret = genUserShareURLByQiNiu(content, pic_name, backgroud, null, 280,160,160,inviteCode,188, 255);
				 return org.apache.commons.lang.StringUtils.isNotBlank(ret);
				
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("生成用户-inviteCode:{}，第3张图片异常：{}",inviteCode,e);
				}
				return qiniuService.checkFileExist(pic_name);
			}
		}
		
		private String  getUserShareURLByQiNiuPic(User user,int index){
			//int flag = 0;
			String content =Constants.share_wx_pre_link+user.getInvite_code();
			String pic_name = user.getInvite_code()+"_"+(index+pic_min_index)+".png";
			String pic_url  = qiniuService.getResourceURLByFileName(pic_name);
			String inviteCode = Integer.toString(user.getUser_identity());
			switch (index) {
			case 0:
				if(getUserShareURLByQiNiuPic_1(content, pic_name,inviteCode)){
					return pic_url;
				}
				break;
			case 1:
				if(getUserShareURLByQiNiuPic_2(content, pic_name,inviteCode)){
					return pic_url;
				}
				break;
			case 2:
				if(getUserShareURLByQiNiuPic_3(content, pic_name,inviteCode)){
					return pic_url;
				}
				break;
			}
			return null;

		}
		private static  String SHARE_URL = "https://api.miaozhuandaqian.cn/";
		static{
			if(!GlobalConfig.isDeploy){
				SHARE_URL = "http://mapi.lieqicun.cn/";
			}
		}
		public String genShareOrderImage(User user) throws IOException, Exception{
			String pic_name = "show_order_image_n_"+user.getUser_identity();
			logger.info("user 访问生成晒单开始,user_identity：{}",user.getUser_identity());
			try{
				if(userShareCache.exists(pic_name) && qiniuService.checkFileExist(pic_name)){
					logger.info("user 访问生成晒单,存在图片,user_identity：{}",user.getUser_identity());
					return qiniuService.getResourceURLByFileName(pic_name);
				}
			}catch(Exception e){
				logger.error("redis check user gen share order image ,user:{},cause:",user.getId(),e);
			}
			initShareOrderBackground();
			logger.info("user 访问生成晒单不存在图片,user_identity：{}",user.getUser_identity());
			BufferedImage backgroud  = new BufferedImage(sharOrderBG.getWidth(),sharOrderBG.getHeight(), sharOrderBG.getType());
			backgroud.setData(sharOrderBG.getData());
			
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
			
			if(qiniuService.checkFileExist(pic_name)){
				//删除以前的数据
				qiniuService.deleteFile(pic_name);
			}
			
			String path= qiniuService.uploadMediaFile(pic_name, output.toByteArray());
			logger.info("user 访问生成晒单结束,user_identity：{},path:{}",user.getUser_identity(),path);
			if(StringUtil.isNotBlank(path)){
				//过期时间是到午夜的时间
				int seconds = (int)((DateUtil.getTodayEndDate().getTime() - GenerateDateUtil.getCurrentDate().getTime())/1000);
				try{
					//今日的图片生成标记
					userShareCache.set(pic_name, "1",seconds);
				}catch(Exception e){
					logger.error("redis set user gen share order image ,user:{},cause:",user.getId(),e);
				}
			}
			return path;
		}
}