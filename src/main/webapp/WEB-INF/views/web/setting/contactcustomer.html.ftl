<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style type="text/css">
	body{background:#fff;}
	.customer{padding-bottom:2em;}
	.customer_tit{font-size:1.3em; color:#000; padding:1.9em 1.2em;}
	.customer_way{border:2px solid #ff8003; margin:0 1.2em; position:relative;}
	.customer_txt{padding:1em 0.75em; font-size:1em; color:#000; line-height:1.6em;}
	.customer_txt em{font-size:1.5em; font-style:normal; color:#ff8003;}
	.customer_txt i{color:#ff8003; font-style:normal;}
	.dot{width:20%; height:0.6em; background:#fff; position:absolute; right:40px; top:-0.35em;}
	.dot span{width:0.6em; height:0.6em; background:#ff8003; border-radius:0.6em; display:block;}
	.and{font-size:1.2em; color:#000; text-align:center; padding:2.25em 0; line-height:1.5em;}
	.customer_way1 .customer_txt ul li:first-child{margin-top:0.3em;}
	.customer_way1 .customer_txt ul li{padding-left:1.5em;}
	.customer_way1 .customer_txt ul li span{display:block; float:left; margin-left:-1.5em;}
	.customer_way1 .customer_txt .customer_pic{width:8em; height:8em; margin:0 auto; background:url(${util.static}images/img/ewm.jpg) no-repeat; background-size:cover;}
</style>
<div class="customer">
	<div class="customer_tit">请关注我们的官方微信</div>
	<div class="customer_way">
		<div class="customer_txt">在微信中选择添加朋友，输入微信号<em>秒赚大钱</em>搜索后选择<i>第一个</i>公众号点击关注，或者输入<i>la6680</i>， 搜索后关注就可以了...</div>
		<div class="dot"><span></span></div>
	</div>
	<div class="and">或</br>者</div>
	<div class="customer_way customer_way1">
		<div class="customer_txt clearfix">
			<div class="customer_pic" onclick="saveImg()"></div>
			<ul>
				<li><span>1、</span>点击图片保存到手机相册</li>
				<li><span>2、</span>在微信中打开扫一扫点击右上角相册</li>
				<li><span>3、</span>找到相册里储存的二维码然后点击关注</li>
			</ul>
		</div>
		<div class="dot"><span></span></div>
	</div>
</div>
<script>
function saveImg(){
	if(typeof MiJSBridge == 'object') {
		MiJSBridge.call('saveImage', {image:"http://"+document.domain+"${util.static}images/img/ewm.jpg"}, function(ret){
			var message = ret.status =="ok" ? '保存二维码图片成功，请打开相册查看您的图片！' : ret.err_msg;
		 	MiJSBridge.call('alert', {title: message}, function(){});
		});
	}
}
</script>
<#include "/common/footer.ftl">