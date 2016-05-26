<#import "/lib/util.ftl" as util>
<#include "/common/headerNews.ftl">
<link rel="stylesheet" type="text/css" href="${util.swipercss}">
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
	.sum{height:10em; margin:2.2em 1.3em 0; border-radius:0.6em; background:rgba(255,255,255,.3); overflow: hidden;}
	.sun_bor{border:3px solid #fff; border-top:0; margin: 1.3em;}
	.sun_bor p{color:#fff; text-align:center; margin:0 2em; line-height:6.7em; overflow:hidden; text-shadow:2px 1px 2px rgba(0,0,0,.75);}
	.sun_bor p span{font-size:2.4em;}
	.sun_bor p em{font-style:normal; font-size:1.1em; margin-left:0.3em;}
	.line_top{width:100%; position:absolute; top:0;}
	.line_left{width:33%; height:3px; background:#fff; float:left;}
	.line_txt{width:34%; text-align:center; font-size:0.7em; color:#fff; float:left; margin-top:-0.4em;}
	.line_right{width:33%; height:3px; background:#fff; float:right;}
	.look_score{border-bottom:1px solid #dcdcdc; text-align:center; font-size:0.6em; color:#222; font-weight:normal; padding:1.7em 0 1em;}
	.score_list{padding:0 0 0 4em;}
	.score_pic{width:2.8em; height:2.8em; margin:0.75em 0 0.75em -2.8em; float:left; border:1px solid #dcdcdc; border-radius:2em;}
	.score_pic img{border-radius:2em;}
	.score_left{padding:0.75em 0.75em 0; border-bottom:1px solid #dcdcdc;}
	.score_text b{width:4.2em; height:1em; line-height:1em; overflow:hidden; font-size:0.9em; color:#000; font-weight:normal; margin-top:0.3em; display:block;}
	.friend_head_pic span, .name-icon span{width:20px;height:20px;background-size:100%;background-repeat:no-repeat;display:inline-block;}
	.score_text em{font-style:normal; font-size:0.67em; color:#666; margin-top:0.5em; display:block;}
	.score_right{float:right; font-size:0.9em; color:#666; line-height:2.8em;}
	.popup_bg{width:100%; height:100%; background:rgba(0,0,0,.5); position:fixed; top:0;}
	.share{position:absolute; bottom:0; width:100%; height:15em; background:#fff;}
	.share li{width:33.3%; float:left; margin-top:1em;}
	.share li a{width:4.4em; height:4.4em; display:block; margin:0 auto;}
	.share li span{font-size:0.9em; color:#666; display:block; text-align:center; margin-top:0.5em;}
	/*收纳好友-图片版*/
	.swiper-container{width:100%;padding-top:50px;padding-bottom:50px;}
	.swiper-slide{background-position:center; width:14.2em; height:19em;}
	.banner_text{width:100%; text-align:center; font-size:0.75em; color:#666; margin-top:1em;}
	.point{width:6.5em; margin:0 auto; margin-top:2em; overflow:hidden;}
	.point li{width:0.6em; height:0.6em; border:2px solid #666; border-radius:0.6em; float:left; margin:0 0.6em;}
	.point li.on{background:#666;}
	.btn{margin:2.2em 1.3em; height:3em; line-height:3em; background:#ff8003; border-radius:0.4em;}
	.btn a{text-align:center; display:block; font-size:1.2em; cursor:pointer;}
	.btn_top{margin:0 1.3em;}
	.foot{width:100%; height:3.25em; position:fixed; bottom:0; background:#fff; border-top:1px solid #999;z-index:999;}
	.foot_sort{width:50%; border-left:1px solid #dcdcdc; margin-left:-1px; float:left; height:2.25em; line-height:2.25em; margin-top:0.5em; text-align:center;}
	.foot_sort a{font-size:1.1em; color:#222; display:block;}
	.foot_sort a.on{color:#ff8003;}
	.munber{font-size:1.2em; color:#fff; text-align:center; line-height:1.2em; margin:0.8em 0;}
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
	<div class="friend" <#if isGuest>style="height:27.4em"</#if>>
		<#if isGuest>
		<div class="friend_head clearfix" style="padding-left:1.3em;">
			<div class="friend_head_txt posi_r" style="margin-left:0em;">
				<p style="margin:1em 1em;">${source!''}</p>
			</div>
		</div>	
		<#else>
		<div class="friend_head clearfix">
			<div class="friend_head_pic fl">
				<a><img src="${user.headImg!''}" style="border-radius:2em;"></a>
				<p>${user.uniName}</p>
			</div>
			<div class="friend_head_txt posi_r">
				<a class="left"></a>
				<p>我玩秒赚大钱已经${user.registrationDay}天了，邀请你一起来赚钱！完全免费哦！</p>
				<a class="right"></a>
			</div>
		</div>
		</#if>
		<div class="sum">
			<div class="sun_bor posi_r">
				<p><span>${income_total}</span><em>元</em></p>
				<div class="line_top">
					<div class="line_left"></div>
					<div class="line_txt">已累计分成</div>
					<div class="line_right"></div>
				</div>
			</div>
		</div>
		<#if !isGuest><div class="munber">邀请码：${user.user_identity}</div></#if>
		<div class="btn<#if !isGuest> btn_top</#if>">
			<#if isInAppView><a id="shareBtn">分享好友收徒</a><#else><a id="donwloadBtn">下载APP开始赚钱</a></#if></a>
		</div>
	</div>
	<div class="score">
		<h1 class="look_score">看看网友成绩</h1>
		<div id="slide">
		<#list userFriendIncome as uf>
		<#if uf.user??>
		<div class="score_list clearfix" <#if (uf_index >=10)>style="display:none;"</#if>>
			<div class="score_pic"><img width="100%" src="${uf.user.headImg}"></div>
			<div class="score_left clearfix">
				<div class="score_text fl">
					<b class="name-icon">${uf.user.uniName!""}</b>
					<em>${uf.beforMinute}</em>
				</div>
				<span class="score_right">${uf.remarks}</span>
			</div>
		</div>
		</#if>
		</#list>
		</div>
	</div>
</div>

<div class="swiper-container" id="pic"  style="display:none;">
	<div id="pic_load" class="swiper-wrapper" data-pic="<#if pics??>true</#if>">
	</div>
	<div class="load" style="display:none;">
		<div class="bounce1"></div>
		<div class="bounce2"></div>
		<div class="bounce3"></div>
	</div>
	<p class="banner_text">点击下面按钮保存后，将图片发送给好友完成收徒</p>
	<ol class="swiper-pagination">
		<li class="swiper-pagination-bullet"></li>
		<li class="swiper-pagination-bullet swiper-pagination-bullet-active"></li>
		<li class="swiper-pagination-bullet"></li>
	</ol>
	<div class="btn">
		<a id="saveBtn">保存到相册</a>
	</div>
</div>
<script>
	var shareUrl = 'http://wx.hongjie68888.cn/www/downloads/share/<#if user??>${user.user_identity}<#else>0</#if>';
</script>
<#if isInAppView>
<footer class="foot">
	<div class="foot_sort"><a  class="on" data-click='text'>普通版</a></div>
	<div class="foot_sort"><a data-click='pic'>图片版</a></div>
</footer>
<#elseif isWeixin=="true">
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
	    
	    wx.onMenuShareTimeline({title: '轻松点一点，越玩越有钱',link: shareUrl,imgUrl: '${share_img!''}',trigger: function (res) {},
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
    <#if isInAppView>
    $('#shareBtn').click(function(){
    	if(typeof MiJSBridge == 'object')
    		MiJSBridge.call('share', {title:"轻松点一点，越玩越有钱", image:"${share_img!''}", content:"下载试用App即可挣钱，每天半小时，轻松月挣几百元零花钱，你也可以", url:shareUrl}, function(ret){
    			if(ret.channel) {
	    			$.ajax({type: "POST",url: "${util.ctx}/api/v1/user/share_complete",dataType:'json',timeout: 3000,success: function(r){
					   	 	var message = r.code!='0' ? r.message : '分享成功！';
					   	 	MiJSBridge.call('alert', {title: message}, function(){ console.log('OK');});
					   },
					   error: function(xhr) {
					   		MiJSBridge.call('alert', {title: '请求出错(请检查相关度网络状况)'}, function(){ console.log('OK');});
		                }
					});
				}
    		});
    });</#if>
    $(".content").click(function(){
    	$("#text").show()
		$(this).hide();
	})
    $('#donwloadBtn').click(function(){
    	if(${ios}){
    	if (${isWeixin}){
    		var url= "${util.ctx}/www/downloads/app/${unionIdMd5!''}";
			window.location.href= url;
    	} else if(${isNotDownload}) {	//不能下载的
    		alert('请复制链接地址到Safari打开下载');
    	} else {	
    		var url ='${util.ctx}/www/downloads/url';
    		window.location.href= url;
    	}}else{
    		alert("对不起，只支持苹果手机！");
    	}
    });<#if isInAppView>
 	$('#saveBtn').click(function(){
 		var shareImage = $('.swiper-slide-active').css('background-image');
 		shareImage =shareImage.replace("url(", "").replace(")","");
    	if(typeof MiJSBridge == 'object')
    		MiJSBridge.call('saveImage', {image:shareImage}, function(ret){
    			var message = ret.status =="ok" ? '保存分享图片成功，请打开相册查看您的图片！' : ret.err_msg;
    			MiJSBridge.call('toast', {message: message});}
			)
    });</#if>
    function loadImages() {
    	<#if isInAppView>
    	$(".load").css("display","block");
    	$.ajax({type: "POST",url: "${util.ctx}/api/v1/share/pic_url/${unionIdMd5!''}",dataType:'json',timeout: 5000,success: function(r){
		   	 	if(r.code) {
		   	 		MiJSBridge.call('alert', {title: r.message}, function(){ console.log('OK');});
		   	 	} else {
		   	 		if(r.pics) {
		   	 			var picArr = r.pics.split(",");
		   	 			for(var i = 0; i < picArr.length; i++){
		   	 				var id="pic_"+i;
		   	 				var divContent = "<div id='"+id+"' class='swiper-slide'  style='background:url("+picArr[i]+") no-repeat; background-size:100% 100%;'></div>";
		   	 				$("#pic_load").append(divContent);
		   	 				$(".load").css("display","none");
					
			   	 			$('#'+id).click(function(){
			   	 				var ind = $(this).attr("id").split("_")[1];
					   	     	if(typeof MiJSBridge == 'object'){
					   	     		MiJSBridge.call('viewImage',{images:picArr,index:ind});
					   	 		}
				   	 		});
		   	 			}
		   	 			initSwiper();
		   	 		} else {
		   	 			MiJSBridge.call('alert', {title: "加载图片失败，请稍后再试！"}, function(){ console.log('OK');});
		   	 		}
		   	 	}
		   },
		   error: function(xhr) {
            	MiJSBridge.call('alert', {title: '请求出错(请检查相关度网络状况)'}, function(){ console.log('OK');});
            }
		});
    	</#if>
    }
    function initSwiper() {
    	var swiper = new Swiper('.swiper-container', {
		    pagination: '.swiper-pagination',
		    effect: 'coverflow',
		    grabCursor: true,
		    centeredSlides: true,
		    slidesPerView: 'auto',
		    loop:true,
		    loopedSlides:3,
		    coverflow: {
		        rotate: 40,
		        stretch: 0,
		        depth: 100,
		        modifier: 1,
		        slideShadows : true,
		    }
		});
    }
});
</script>
<#include "/common/footer.ftl">