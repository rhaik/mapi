<#import "/lib/util.ftl" as util>
<head>
	<meta charset="utf-8"> 
	<meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1, user-scalable=no">
	<meta name = "format-detection" content="telephone = no" />
	<title>分享</title> 
	<link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
        <style type="text/css">
		body{background:#f0eff5;font-family:Helvetica Neue,Helvetica,Arial,sans-serif;font-size:14px;color:#000;margin:0;padding:0;}
		.personal_center{background:#f0eff5; padding:20px 0;}
		.ewm{padding:8px 0 23px; text-align:center; font-size:14px;}
		.ewm a{width:18px; height:18px; display:inline-block; background:url(${util.static}images/img/ewm.png) 0 0 / 100% 100% no-repeat; vertical-align:middle; margin-left:5px;}
		.ewm_big{width:100%; height:100%; background:rgba(0,0,0,.5); position:fixed; top:0; left:0; z-index:2;}
		.ewm_box{width:250px; height:300px; background:#fff; border-radius:5px; position:absolute; top:50%; left:50%; margin:-150px 0 0 -125px; overflow:hidden;}
		.ewm_big img{width:200px; height:200px; display:block; margin:35px auto 15px;}
		.ewm_box p{font-size:12px; color:#999; text-align:center; margin:7px 0;}
	</style>
</head>
<body>
<div class="personal_center">
	<div class="ewm">收徒二维码：<a></a></div>
	<div class="ewm_big dn">
		<div class="ewm_box">
			<img width="100%" src="${(u.user_qrcode)!''}" />
			<p id="image_ttl"><#if ttl??>图片有效期为${ttl!''}天</#if>
			</p>
			<p style="font-size:14px;color:#000;">长按保存并且发送好友</p>
		</div>
	</div>
<#include "/common/footer.ftl">
