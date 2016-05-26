<#import "/lib/util.ftl" as util>
<head>
	<meta charset="utf-8"> 
	<meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1, user-scalable=no">
	<meta name = "format-detection" content="telephone = no" />
	<title>个人中心</title> 
	<link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
        <style type="text/css">
		body{background:#f0eff5;font-family:Helvetica Neue,Helvetica,Arial,sans-serif;font-size:14px;color:#000;margin:0;padding:0;}
		.personal_center{background:#f0eff5; padding:20px 0;}
		.personal{padding:15px; border-top:1px solid #dcdcdc; border-bottom:1px solid #dcdcdc; background:#fff;}
		.personal_img{width:60px; height:60px; border-radius:5px; margin-right:10px; overflow:hidden;}
		.personal_text a{height:20px; line-height:20px; font-size:18px; display:block; margin-top:8px; color:#000;}
		.personal_text a span{height:20px; line-height:20px; vertical-align:middle;}
		.personal_text p{font-size:15px; margin-top:6px;}
		.ewm{padding:8px 0 23px; text-align:center; font-size:14px;}
		.ewm a{width:18px; height:18px; display:inline-block; background:url(${util.static}images/img/ewm.png) 0 0 / 100% 100% no-repeat; vertical-align:middle; margin-left:5px;}
		.ewm_big{width:100%; height:100%; background:rgba(0,0,0,.5); position:fixed; top:0; left:0; z-index:2;}
		.ewm_box{width:250px; height:300px; background:#fff; border-radius:5px; position:absolute; top:50%; left:50%; margin:-150px 0 0 -125px; overflow:hidden;}
		.ewm_big img{width:200px; height:200px; display:block; margin:35px auto 15px;}
		.ewm_box p{font-size:12px; color:#999; text-align:center; margin:7px 0;}
		.list{background:#fff; border-top:1px solid #dcdcdc; border-bottom:1px solid #dcdcdc; padding-left:20px;}
		.list a{font-size:15px; color:#000; text-align:left; border-bottom:1px solid #dcdcdc; margin:0; line-height:45px; position:relative; display:block;}
		.list a:last-child{border-bottom:none;}
		.righr_ico{width:10px; height:14px; position:absolute; top:50%; right:20px; margin-top:-7px; background:url(${util.static}images/img/right_ico.png) 0 0 / 100% 100% no-repeat;}
		.list_icon{padding-left:60px;}
		.list_icon a span{width:18px; height:18px; display:inline-block; margin:0 20px 0 -30px;}
		.mar{margin:30px 0;}
		.emojicon-m{width:20px;height:20px;background-size:100%;background-repeat:no-repeat;display:inline-block;}
	</style>
</head>
<body>
<div class="personal_center">
	<div class="personal clearfix posi_r">
		<div class="personal_img fl"><img src="${(u.headImg)!''}" /></div>
		<div class="personal_text fl">
			<a>${(u.uniName)!''}</a>
			<p>ID: ${(u.user_identity)!''}</p>
		</div>
		<em class="righr_ico"></em>
	</div>
	<div class="ewm">收徒二维码：<a></a></div>
	<div class="ewm_big dn">
		<div class="ewm_box">
			<img width="100%" src="${(u.user_qrcode)!''}" />
			<p id="image_ttl"><#if ttl??>图片有效期为${ttl!''}天</#if>
			</p>
			<p style="font-size:14px;color:#000;">长按保存并且发送好友</p>
		</div>
	</div>
	<div class="list list_icon">
		<a href="/weixin/my/invites.html"><span><img src="${util.static}images/myincome_04.png" /></span>收徒记录<em class="righr_ico"></em></a>
		<a href="/weixin/my/enchashments.html"><span><img src="${util.static}images/myincome_07.png" /></span>提现记录<em class="righr_ico"></em></a>
	</div>
	<div class="list mar">
		<a href="/weixin/user/withdraw_type.html">提现账号设置<em class="righr_ico"></em></a>
	</div>
	<div class="list">
		<a href="${util.static}html\help\qa.html">常见问题<em class="righr_ico"></em></a>
		<a href="/weixin/user/aboutus.html">关于我们<em class="righr_ico"></em></a>
	</div>
</div>
<script src="${util.jquery}"></script>
<script>
	$(".ewm a").click(function(){
		$(".ewm_big").fadeIn();
	})
	$(".ewm_big").click(function(){
		$(this).fadeOut();
	})
	
	$(".personal").click(function(){
		window.location.href="/weixin/my/to_bind_mobile.html";		
	})
	
	<#if isCreateQR>
		$(function(){
			$.ajax({type: "POST",url: "/weixin/api/get_qrCode.ajax?uid=${(u.user_identity)!0}",dataType:'JSON',timeout: 3000,
				success: function(r){
					$(".ewm_box img").attr("src",r.user_qrcode);
					$("#image_ttl").html("图片有效期为7天");
					console.info(r.user_qrcode);
				},
				error: function(r){
				}
			});
		});
	</#if>
</script>
<#include "/weixin/wx_share.ftl">
<#include "/common/footer.ftl">
