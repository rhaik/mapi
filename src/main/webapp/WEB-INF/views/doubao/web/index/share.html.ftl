<#import "/lib/util.ftl" as util>
<#include "/doubao/common/header.ftl">
<style>
	/* 公共部分结束 */
	.mian_bg{position:relative; width:100%; height:100%; top:0; left:0;}
	.text{position:absolute; top:0em;}
	.task{margin:0 5%; width:90%; height:22em; background:#fff; position:absolute; top:7.5em; border-radius:0.5em;}
	.user{margin:0 1.25em; padding:1.25em 0; border-bottom:1px solid #999; overflow:hidden;}
	.user img{width:3.125em; height:3.125em; border-radius:3em; display:block; float:left; margin-right:-3.625em;}
	.user_infor{padding-left:3.625em;}
	.user_infor h1{font-size:0.875em; color:#222; font-weight:normal; margin-top:0.5em;}
	.user_infor p{font-size:0.75em; color:#666; margin-top:0.5em;}
	.task .pic{width:9.5em; height:9.5em;margin:1em auto;}
	.task h2{font-size:0.9em; color:#666; font-weight:normal; text-align:center;}
	.task h2 em{font-style:normal; color:#ff0000;}
	.task h3{font-size:1.125em; width:90%; height:1.3em; line-height:1.5em; color:#111; font-weight:normal; text-align:center; margin-top:0.6em; text-overflow:ellipsis; white-space:nowrap; overflow:hidden; padding:0 0.9em;}
	.bot{width:100%; height:4.56em; background:#fff; position:fixed; bottom:0; overflow:hidden; border-top:1px solid #dcdcdc;}
	.bot img{width:3em; height:3em; float:left; margin:0.8em 0.5em 0.8em 0.9em;}
	.bot_tie h1{font-size:1em; color:#111; font-weight:normal; padding-top:1.1em;}
	.bot_tie h2{font-size:0.75em; color:#666; font-weight:normal; padding-top:0.5em;}
	.down_btn{width:6em; height:2.2em; line-height:2.2em; background:#ff8003; border-radius:0.3em; margin:1.3em 0.9em 0 0;}
	.down_btn em{font-size:0.9em; color:#fff; font-style:normal; text-align:center; display:block;}
	
</style>   
<!-- 中间内容 -->
<div class="mian_bg">
	<img src="${util.static}images/duobao/bg.png">
	<div class="text"><img src="${util.static}images/duobao/text.png" /></div>
	<div class="task">
		<#if user??>
		<div class="user">
			<img src="${user.headImg}" />
			<div class="user_infor fl">
				<h1>${user.hideMobile}</h1>
				<p>我玩赚大钱已经${user.registrationDay}天了，邀你一起去夺宝</p>
			</div>
		</div>
		</#if>
		<div class="pic"><img src="${pa.product.thumb}" /></div>
		<h2>总需<em>${pa.productActivity.number}</em>人次</h2>
		<h3>(第${pa.productActivity.product_number}期)${pa.productActivity.product_name}</h3>
	</div>
	<div class="bot">
		<img src="${util.static}images/img/logo.png" />
		<div class="bot_tie fl">
			<h1>秒赚大钱</h1>
			<h2>一款上手超快的休闲赚钱App</h2>
		</div>
		<div class="down_btn fr"><em id="donwloadBtn">立即下载</em></div>
	</div>
</div>
<#if isWeixin=="true">
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script type="text/javascript">
	wx.config({
	    debug: false,
	    appId: "${sharemap.appid!''}",
	    timestamp: ${sharemap.timestamp!0},
	    nonceStr: "${sharemap.nonceStr!''}",
	    signature: "${sharemap.signature!''}",
	    jsApiList: [
	      'onMenuShareTimeline',
	      'onMenuShareAppMessage'
	    ]
	});
	wx.ready(function () {
	    wx.onMenuShareAppMessage({
	      title: '将我的幸运分享给你，快来“秒赚大钱”领取吧',
	      desc: '最新一期${pa.productActivity.product_name}正在进行中...',
	      link: '${sharemap.url!''}',
	      imgUrl: '${baseUrl}${util.static}images/logo.png',
	      trigger: function (res) {},
	      success: function (res) {},
	      cancel: function (res) {},
	      fail: function (res) {}
	    });
	    
	    wx.onMenuShareTimeline({title: '将我的幸运分享给你，快来“秒赚大钱”领取吧',link: '${sharemap.url!''}',imgUrl: '${baseUrl}${util.static}images/logo.png',trigger: function (res) {},
	      success: function (res) {},
	      cancel: function (res) {},
	      fail: function (res) {}
	    });
	});
	wx.error(function(res){});
</script>
</#if>
<script type="text/javascript">
$(function(){
	$('#donwloadBtn').click(function(){
    	if (${isWeixin}){
    		var url= "${util.ctx}/www/downloads/app/${unionIdMd5!''}";
			window.location.href= url;
    	} else {	
    		var url ='${util.ctx}/www/downloads/url';
    		window.location.href= url;
    	}
    });
});
</script>
</body> 
</html> 