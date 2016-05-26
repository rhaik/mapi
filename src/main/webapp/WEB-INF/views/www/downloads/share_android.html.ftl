<#import "/lib/util.ftl" as util>
<#include "/common/headerNews.ftl">
<style>
	body{background:#fff;font-family:Helvetica Neue,Helvetica,Arial,sans-serif;font-size:14px;color:#000;margin:0;padding:0;}
	.ui-loader{display:none;}
	/*收纳好友-普通版*/
	.friend{width:100%; height:29.4em; background:url(${util.static}images/img/bg1.png) no-repeat; background-size:100% 100%;}
	.friend_head{padding:2.2em 1.3em 0 5em;}
	.friend_head_pic{ margin-left:-3.7em;}
	.friend_head_pic a{width:3.7em; height:3.7em; display:block; border:3px solid #fff; border-radius:2em;}
	.friend_head_pic p{width: 5em; height: 1.5em; line-height: 1.5em; overflow: hidden; font-size:0.82em; color:#fff; text-align:center; display:block; margin-top:0.5em;}
	.friend_head_pic span{width:5em; height:1.5em; line-height:1.5em;overflow:hidden; font-size:0.82em; color:#fff; text-align:center; display:block; margin-top:0.5em;}
	.friend_head_txt{height:5.2em; background:#fff; border-radius:0.6em; border:1px solid #e5e5e5; margin-left:0.75em; overflow:hidden;}
	.friend_head_txt p{height:3.5em; overflow:hidden; font-size:0.9em; color:#000; margin:1.2em 1.9em; line-height:1.7em;}
	.friend_head_txt a{width:0.75em; height:0.75em; position:absolute; display:block; background:url(${util.static}images/img/icon.png) no-repeat; background-size:20.6em 20.6em;}
	.friend_head_txt a.left{left:0.56em; top:1.2em; background-position:-2em -2.8em;}
	.friend_head_txt a.right{right:0.56em; top:1.2em; background-position:-3.95em -2.8em}
	.sum{margin:1.5em 1.3em 0; border-radius:0.6em; background:rgba(255,255,255,.3); overflow: hidden;}
	.sun_bor{border:2px solid #fff; border-top:0; margin: 1.3em;}
	.sun_bor p{color:#fff; padding:0.83em 0.625em; line-height:1.5em; overflow:hidden;}
	.sun_bor p span{font-size:2.4em;}
	.sun_bor p em{font-style:normal; font-size:1.1em; margin-left:0.3em;}
	.line_top{width:100%; position:absolute; top:0;}
	.line_left{width:33%; height:3px; background:#fff; float:left;}
	.line_txt{width:34%; text-align:center; font-size:0.7em; color:#fff; float:left; margin-top:-0.4em;}
	.line_right{width:33%; height:3px; background:#fff; float:right;}
	.look_score{border-bottom:1px solid #dcdcdc; text-align:center; font-size:0.6em; color:#222; font-weight:normal; padding:1.7em 0 1em;}
	.friend_head_pic span, .name-icon span{width:20px;height:20px;background-size:100%;background-repeat:no-repeat;display:inline-block;}
	.popup_bg{width:100%; height:100%; background:rgba(0,0,0,.5); position:fixed; top:0;}
	.banner_text{width:100%; text-align:center; font-size:0.75em; color:#666; margin-top:1em;}
	.point{width:6.5em; margin:0 auto; margin-top:2em; overflow:hidden;}
	.point li{width:0.6em; height:0.6em; border:2px solid #666; border-radius:0.6em; float:left; margin:0 0.6em;}
	.point li.on{background:#666;}
	.btn{margin:2.2em 1.3em; height:3em; line-height:3em; background:#ff8003; border-radius:0.4em;}
	.btn a{text-align:center; display:block; font-size:1.2em; cursor:pointer;}
	.footer p{font-size:0.875em; color:#666; text-align:center; padding:2em 0 0.5em;}
	.footer p em{font-size:0.93em; color:#666; font-style:normal;}
	.footer span{font-size:0.75em; color:#666; display:block; text-align:center; padding-bottom:1em;}
	.munber{font-size:1.2em; color:#fff; text-align:center; line-height:1.2em; margin:0.8em 0;}
	
	.popup_bg{width:100%; height:100%; position:fixed; top:0; left:0; background:rgba(0,0,0,.5); z-index:9;}
	.popup_box{margin:30% 35px; background:#fff; border-radius:10px; padding-bottom:30px;}
	.loding_top{padding:15px 20px; overflow:hidden;}
	.lode_text span{font-size:18px; color:#000; margin-top:13px; display:block;} 
	.erect{padding:8px 0; text-align:center; font-size:14px; color:#000; border-top:1px solid #c1c1c1; border-bottom:1px solid #c1c1c1;}
	.Install_btn a.on{background:#ff8003;}
	.serch_box{width:100%; height:100%; position:fixed; top:0; left:0; background:#7f7f7f; z-index:9;}
	
	.lode_icon{width:70px; height:70px; margin-right:10px; float:left;}
	.lode_text p{font-size:13px; color:#000; margin-top:4px;} 
	.Install_btn a{margin:20px 35px 0; height:40px; line-height:40px; border-radius:5px; font-size:16px; color:#fff; background:#c1c1c1; display:block; text-align:center;}

	.safari_open{width:100%; height:100%; position:fixed; left:0; top:0; background:rgba(0,0,0,0.6); z-index:9; text-align:right;}
	.safari_open img{margin:5px 10px 0px; }
	
	/*下载*/
	.load{width:100%; height:19em; line-height:19em; background:#fff; position: absolute; top:50px; text-align:center; z-index: 999;}
		.load > div {
		  width: 20px;
		  height: 20px;
		  background-color: #ff8003;
		  border-radius: 100%;
		  display: inline-block;
		  -webkit-animation: bouncedelay 1.4s infinite ease-in-out;
		  animation: bouncedelay 1.4s infinite ease-in-out;
		  /* Prevent first frame from flickering when animation starts */
		  -webkit-animation-fill-mode: both;
		  animation-fill-mode: both;
		}

		.load .bounce1 {
		  -webkit-animation-delay: -0.32s;
		  animation-delay: -0.32s;
		}

		.load .bounce2 {
		  -webkit-animation-delay: -0.16s;
		  animation-delay: -0.16s;
		}

		@-webkit-keyframes bouncedelay {
		  0%, 80%, 100% { -webkit-transform: scale(0.0) }
		  40% { -webkit-transform: scale(1.0) }
		}

		@keyframes bouncedelay {
		  0%, 80%, 100% { 
		    transform: scale(0.0);
		    -webkit-transform: scale(0.0);
		  } 40% { 
		    transform: scale(1.0);
		    -webkit-transform: scale(1.0);
		  }
		}
</style>
<div class="bg_f pb80" id="text">
	<div class="friend">
		<div class="friend_head clearfix">
			<div class="friend_head_pic fl">
				<#if user??>
					<a><img src="${user.headImg!''}" style="border-radius:2em;"></a>
					<p>${user.uniName}</p>
					<#else>
						<a><img src="${util.static}images/logo.png" style="border-radius:2em;"></a>
						<p>秒赚大钱</p>
				</#if>
			</div>
			<div class="friend_head_txt posi_r">
				<a class="left"></a>
				<p><#if user?? && user.id gt 100>我玩秒赚大钱已经${user.registrationDay}天了<#if withdrawTotal gt 2000>，累计提现${util.fen2yuanS(withdrawTotal)}元</#if>，邀请你一起来赚大钱！
					<#else>下载试用App即可挣钱，每天半小时，轻松月挣几百元零花钱，你也可以！</#if>
				</p>
				<a class="right"></a>
			</div>
		</div>
		<div class="sum">
			<div class="sun_bor posi_r">
				<p>秒赚大钱（www.miaozhuandaqian.com）是一款专注于手机赚钱的app，至今已有千万用户。每天动动手指，下载优质软件，就能轻松赚得零花钱，支付宝微信提现当天即可到账！</p>
				<div class="line_top">
					<div class="line_left"></div>
					<div class="line_txt">简 介</div>
					<div class="line_right"></div>
				</div>
			</div>
		</div>
		<#if !isGuest><!-- div class="munber">邀请码：${user.user_identity}</div --></#if>
		<div class="btn">
			<a id="donwloadBtn">下载APP开始赚钱</a>
		</div>
	</div>
	<div class="footer">
		<p>客服微信：<em>xiaomiaozhushou</em></p>
		<span>Copyright © 2016 北京创意风暴科技有限公司</span>
	</div>
</div>

<div id="popup_div">
<div class="popup_bg" style="display:none;">
	<div class="popup_box">
		<div class="loding_top">
			<div class="lode_icon fl"><img width="100%" src="${util.ctx}/static/images/img/logo.png" /></div>
			<div class="lode_text fl">
				<span>秒赚大钱</span>
				<p>官方手机赚钱神器</p>
			</div>
		</div>
		<div class="erect">请按步骤完成安装</div>
		<div class="Install_btn">
			<a class="on">安装并打开秒赚大钱</a>
			<a>已安装，请直接打开</a>
		</div>
	</div>
</div>
  <div class="safari_open" style="display:none;">
  	<#if ios>
		<img  style="width:50%;" src="${util.ctx}/static/images/wx_download.png" />
  	<#else>
  	<img width="100%" src="${util.ctx}/static/images/img/search_pic.jpg" />
  	</#if>
 </div>
</div>

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
	      title: '轻松点一点，越玩越有钱',
	      desc: '下载试用App即可挣钱，每天半小时，轻松月挣几百元零花钱，你也可以',
	      link: '${sharemap.url!''}',
	      imgUrl: '${share_img!''}',
	      trigger: function (res) {},
	      success: function (res) {},
	      cancel: function (res) {},
	      fail: function (res) {}
	    });
	    
	    wx.onMenuShareTimeline({title: '轻松点一点，越玩越有钱',link: '${sharemap.url!''}',imgUrl: '${share_img!''}',trigger: function (res) {},
	      success: function (res) {},
	      cancel: function (res) {},
	      fail: function (res) {}
	    });
	});
	wx.error(function(res){});
</script>
</#if>
<script src="${util.jquery}"></script>
<script src="${util.swiperjs}"></script>
<script type="text/javascript">

$(function(){
	$(".Install_btn a.on").click(function(){
		$(".safari_open").css("display","block");
	})
	$(".safari_open").click(function(){
		$(".popup_bg, .safari_open").css("display","none");
	})
});

$(function(){
    var b;var id="#slide";$(id).hover(function(){clearInterval(b)},function(){b=setInterval(function(){var b=$(id),c=b.find(".score_list:last").height();b.animate({marginTop:c+3+"px"},1000,function(){b.find(".score_list:last").show();b.find(".score_list:last").prependTo(b),b.find(".score_list:gt(9)").hide(),b.find(".score_list:first").hide(),b.css({marginTop:0}),b.find(".score_list:first").fadeIn(1000)})},3000)}).trigger("mouseleave");
    $(document).ready(function() {$(".score_list").hover(function() {$(this).stop().fadeTo(300, 1)},function() {$(this).stop().fadeTo(300, 1)})});
    var isClick = false;
    $('.foot_sort>a').click(function(){
    	$('.foot_sort>a').removeClass('on');
    	$(this).addClass('on');
    	
    	var clickId = $(this).attr('data-click');
    	var hiddenId = clickId == "pic" ? "text" : "pic";
    	$('#'+hiddenId).hide();
    	$('#'+clickId).show();
    	
    	if(isClick == false) {
    		isClick = true;
	    	var isPic = $('.swiper-wrapper').attr('data-pic') ? true : false;
	    	if($(this).attr('data-click') =='pic' && isPic==false) {
	    		 loadImages();
			} else {
				initSwiper();
			}
		}
    });
    $(".content").click(function(){
    	$("#text").show()
		$(this).hide();
	})
    $('#donwloadBtn').click(function(){
    	<#if isWeixin>
    		$(".safari_open").show();
    	<#else>
    		var url ='${util.ctx}/www/downloads/url';
    		window.location.href= url;
    	</#if>
    });

    <#if isWeixin>$(".safari_open").show();</#if>

});
</script>
<#include "/common/footer.ftl">