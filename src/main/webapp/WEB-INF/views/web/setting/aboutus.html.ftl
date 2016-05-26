<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	body{background-color:#f0eff5;}
	.content{margin:auto;text-align:center;}
	.content img{width:4.8em;height:4.8em;margin-top:4.8em;}
	.content h1{font-size:18px;font-weight:600;}
	.list{background:#fff; border-top:1px solid #dcdcdc; border-bottom:1px solid #dcdcdc; padding-left:20px; margin-top:80px;}
	.list a{width:100%; font-size:17px; color:#000; text-align:left; border-bottom:1px solid #dcdcdc; margin:0; line-height:50px; display:block; position:relative;}
	.list a:last-child{border-bottom:none;}
	.list a em{width:10px; height:14px; position:absolute; top:50%; right:20px; margin-top:-7px; background:url(${util.static}images/img/right_ico.png) 0 0 / 100% 100% no-repeat;}
	.foot_txt{width:100%; padding:10px 0; margin-top:30px;}
	.foot_txt span{font-size:14px; color:#d3d2d8; display:block; text-align:center;}
	.foot_txt p{font-size:10px; color:#d3d2d8; display:block; text-align:center; margin-top:6px;}
	.relation {text-align:left; margin-top:20px;}
	.relation_txt{border-bottom:1px solid #dcdcdc; padding:10px 0;}
	.relation_txt:first-child{margin-top:15px;}
	.relation_txt:last-child{padding:20px 0; border-bottom:none; }
	.relation_txt p{font-size:16px; color:#000; padding:0 20px; line-height:30px;}
	.relation_txt p i{color:#ff8003; font-style:normal;}
</style>
<div class="content">
	<img src="${util.static}images/img/logo.png" alt="logo" />
	<h1>秒赚大钱${os!''}－${appVer!''}</h1>
	<#if test><p>内测版</p></#if>
	<div class="relation">
		<div class="relation_txt">
			<p>北京创意风暴科技有限公司</p>
		</div>
		<div class="relation_txt">
			 <p>电话：010-57225923</p>
		</div>
		<div class="relation_txt">
			<p>网址：<i>www.miaozhuandaqian.com</i></p>
			<p>地址：北京市朝阳区望京SOHO T3 B座1209室</p>
		</div>
		<div class="relation_txt">
			<p>商务合作：<i>biz@fbaso.com</i></p>
		</div>
		<div class="relation_txt">
			<p>客服微信：<i>xiaomiaozhushou</i></p>
			<p>秒赚大钱QQ群【安卓手机】 <i>332409630</i></p>
			<p>秒赚大钱QQ群【苹果手机】 <i>157456928</i></p>
		</div>
	</div>
	</div>
	<div class="foot_txt">
		<span>秒赚大钱</span>
		<p>Copyright 秒赚大钱.All Rights Reserved</p>
	</div>
</div>
<#include "/common/footer.ftl">