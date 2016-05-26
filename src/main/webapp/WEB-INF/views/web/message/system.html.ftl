<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	body{background-color:#fff;}
	.message{border-radius:4px;padding:16px 16px;background-color:#fff;}
	.message .logo{background:url(${util.static}images/message_01.png) no-repeat;background-size: 101px 33px;height:33px;padding-bottom:10px;border-bottom: 1px solid #dcdcdc;}
	.message h1{font-size:19px;margin:12px 0px;}
	.message .date{color:#666666;margin-bottom:12px;}
	.thumb{height:135px;margin-bottom:24px;}
	.content{line-height:24px;margin-bottom:36px;font-size:16px;color:#222222;}
	footer{margin:0 auto;text-align:center;padding:16px 16px 0px 16px;color:#c6c6c6;position:relative;bottom:10px;}
	footer span{font-weight: 300;font-size:14px;position: relative; bottom: 18px;z-index:1;background-color:#fff;line-height:32px;padding:0px 10px;}
</style>
<div class="message">
	<div class="logo"></div>
	<h1>${message.title}</h1>
	<div class="date">${message.createtime?string("yyyy-MM-dd")}</div>
	<#if message.abstracts_picture??><div class="thumb" style="background:url(${message.abstracts_picture!""}) no-repeat;background-size:100% 100%;"></div></#if>
	<p class="content">${message.htmlContent}</p> 
</div>
<footer><div style="border-top:1px dashed #cccccc;height: 1px;overflow:hidden"></div><span>2015 CopyRight 秒赚大钱</span></footer>
<#include "/common/footer.ftl">