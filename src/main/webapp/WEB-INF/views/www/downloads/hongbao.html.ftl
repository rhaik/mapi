<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
<link rel="stylesheet" type="text/css" href="${util.static}css/style.css">
<link rel="stylesheet" type="text/css" href="${util.static}css/animate.min.css">
<style>
	body {background: #ee5857;}
	.bg{width:100%; height:100%; position:fixed; top:0; left:0; background:gradient(linear, 0 0, 0 100%, from(#019ebb), to(#e5e4c8)); background:-webkit-gradient(linear, 0 0, 0 100%, from(#019ebb), to(#e5e4c8));}
	.head_top span{width:3.75em; height:3.75em; border-radius:3.75em; display:block; margin:2em auto 0;}
	.head_top p{font-size:1.1em; color:#fff; text-align:center; margin-top:0.5em;}
	.main{width:18em; margin:3.75em auto; border:2px solid #fff; border-top:0; position:relative;}
	.main p{padding:2.5em 1em 2em; font-size:0.875em; color:#fff; line-height:1.5em;}
	.main h1{width:100%; position:absolute; top:-0.5em; left:0; font-size:1.125em; color:#fff; font-weight:normal; text-align:center;}
	.main span{width:30%; border-top:2px solid #fff; position:absolute; top:0;}
	.main span.left{left:0;}
	.main span.right{right:0;}
	.start{width:7.5em; height:2.5em; line-height:2.5em; text-align:center; color:#fff; margin:5.5em auto 0; background:#38dcfc; border-radius:2em; box-shadow:3px 1px 5px rgba(1,176,206,.75)}
	.serch{width:100%; height:100%; background:rgba(0,0,0,.8); position:fixed; top:0; left:0;}
	.serch_pic{margin:1em 2.35em;}
	.serch_text{position:absolute; top:1.5em; left:2.35em;}
	.serch_text p{color:#fff; font-size:1em; width:100%; padding-bottom:0.5em;}
	.serch_text p b{width:1.875em; display:inline-block;}
</style>
<!-- 红包 -->
<div class="shake_box yaoiphone">
	<div class="shake_ordinary">
		<!-- 更新时间提示 -->
		<div class="shake_title"><img src="${util.static}images/yaoiphone/title.png" /></div>
		<!-- 开抢时间 -->
		<#--
		<div class="start" id="start-hint" style="display:none;">明日 10：00 准时开抢</div>
		-->
		<!-- 红包个数 -->
		<div class="summation" id="remain-hongbao">
			<em>试试今日手气</em>
		</div>
		<!-- 摇一摇 -->
		<div class="shake_bg">
			<span class="shake_pic animated infinite" id="shake-annim"></span>
		</div>
	</div>

	<div class="get_shake_bg" id="share-dialog">
		<!-- 抢红包页面 -->
		<div class="get_shake" >
			<i>×</i>
			<em><img src="${util.static}images/img/logo.png" /></em>
			<h1>秒赚大钱</h1>
			<!-- 抢到红包 -->
			<div class="hasHongbao">
				<p>您需要先下载秒赚大钱App</p>
				<h2>下载秒赚大钱，开始抢红包</h2>
				<span class="open_shake" id="goto-download">去下载</span>
			</div>
		</div>
	</div>
	<#if isInAppView><div class="btn" id="shareBtn"><a><em><img src="${util.static}images/img/fenxiang.png"></em>点此分享</a></div></#if>
	<!-- 专属红包提示 -->
	<div class="shake_exclusive">
		<#--
		<#if !isInAppView><h1 class="sexclusive">你还没有专属红包</h1></#if>
		-->
		<div class="lantern">
			<p>看看大家的手气</p>
		</div>
		<div class="table">
			<table id="hongbao-list">
				<#list hongbaoList as hongbao>
					<tr>
						<td width="40%">${hongbao.user.uniName}</td>
						<td width="30%">${hongbao.userDrawLog.reason}</td>
						<td width="30%">${hongbao.userDrawLog.timeText}</td>
					</tr>
				</#list>
			</table>
		</div>
	</div>
</div>

<div class="serch" id="weixin-dialog" style="display:none;">
	<div class="serch_text">
		<p>1、点击右上角“<b><img src="${util.static}images/img/point.png" /></b>” </p>
		<p>2、点击“在Sarfari中打开” </p>
	</div>
	<div class="serch_pic"><img src="${util.static}images/img/serch.png" /></div>
</div>
<script src="${util.jquery}"></script>
<#if isWeixin>
	<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
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
	      title: '收徒赚红包啦',
	      desc: '每天都有一部iPhone6S、5000夺宝币和数千万金币等你拿哦！赶紧收徒去吧',
	      link: '${sharemap.url!''}',
	      imgUrl: '${share_img!''}',
	      trigger: function (res) {},
	      success: function (res) {},
	      cancel: function (res) {},
	      fail: function (res) {}
	    });

	    wx.onMenuShareTimeline({title: '收徒赚红包啦',link: '${sharemap.url!''}',imgUrl: '${share_img!''}',trigger: function (res) {},
	      success: function (res) {},
	      cancel: function (res) {},
	      fail: function (res) {}
	    });
	});
	wx.error(function(res){});
</script>
</#if>
<script>
$(function(){
	var threshold = 15;
	var x = t = z = lastX = lastY = lastZ = 0;
	var times = 0;
	window.addEventListener('devicemotion', function () {
		var acceleration = event.accelerationIncludingGravity;
		x = acceleration.x;
		y = acceleration.y;
		if (Math.abs(x - lastX) > threshold || Math.abs(y - lastY) > threshold) {
			if(times == 0) { //shake start
				$(window).trigger($.Event('shakestart'));
			}
			times += 1;
			if(times > 2){ //shake happen
				$(window).trigger($.Event('shaked'));
				times = 0;
			}
		};
		lastX = x;
		lastY = y;
	},false);

	var shaking = false;
	var tm = new Date().getTime();
	$(window).on('shaked', function(){
		var now = new Date().getTime();
		if(now - tm < 5000)return;
		tm = now;
		if(!shaking){
			playShake();
			shaking = true;
			$('#shake-annim').addClass('swing');
			setTimeout(function(){
				shaking = false;
				$('#shake-annim').removeClass('swing');
				<#if !isInAppView>
				$('#share-dialog').show();
				</#if>
			}, 5000);
		}
	});

	$('body').on('click', function(){
		<#if isInAppView>

		<#else>
		$('#share-dialog').show();
		</#if>
	});

	$('#share-dialog i').click(function(){
		$('#share-dialog').hide();
		return false;
	});

	$('#weixin-dialog').click(function(){
		$(this).hide();
		return false;
	});

	$('#goto-download').click(function(){
		$('#share-dialog').hide();

		<#if isWeixin>
		$('#weixin-dialog').show();
		<#else>
		location.href = "/www/downloads/safari";
		</#if>
		return false;
	});

	function playShake(){
		var audio = new Audio('/static/music/red-01.mp3');
		audio.play();
	}

	 <#if isInAppView>
    function doAppShare(){
    	if(typeof MiJSBridge == 'object'){
    		MiJSBridge.call('share', {title:"收徒赚红包啦", image:"https://m.miaozhuandaqian.com/static/images/logo.png", content:"每天都有一部iPhone6S、5000夺宝币和数千万金币等你拿哦！赶紧收徒去吧", url:"${share_url!''}"}, function(ret){

    		});
		}
    }


    $('#shareBtn').click(function(){
		doAppShare();
    });
    </#if>

});

</script>
<#include "/common/footer.ftl" />