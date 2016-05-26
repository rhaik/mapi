<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.swipercss}">
	<script src="${util.jquery}"></script>
	<script src="${util.swiperjs}"></script>
	<style type="text/css">
		body{background:#ee5857;}
		.logo{width:10em; margin:4.2em auto 2em; display:block;}
		.dianji{font-size:1.1em; color:#fff; text-decoration:underline; display:block; text-align:center;}
		.box h1{font-size:1em; color:#fcd281; text-align:center; padding-top:1.6em;}
		.step_btn a{height:3.75rem; line-height:3.75rem; display:block; background:#fff; border-radius:3rem; font-size:1.1rem; color:#ff8003;margin:1.5em 1.5em 0;}
		.step_btn a em{border:1px solid #ff8003; border-radius:0.3em; font-size:0.75rem; color:#ff8003; font-style:normal; padding:0.1em 0.5em; display:inline; margin:0 0.9em 0 3.5em;}
		.step_btn a.step3{margin-top:0; background:none; color:#fff;}
		.step_btn a.step3 em,.step_btn a.step1_finish em{border:1px solid #fff; color:#fff;}
		.step_btn a.step1_finish{background:#ccc; color:#fff;}
		.step_btn a.installed {background:#fcd281; margin:1.5em 1em; text-align:center; color:#ee5857; font-weight:bold;}
		.bottom{width:100%; position:fixed; bottom:0; left:0;font-size:1em; color:#E1A58A; text-align:center; margin:0.5em 0;}

		.bg1,.bg2{width:100%; height:100%; background:rgba(0,0,0,.5); position:fixed; top:0; left:0;}
		.popup,.main{width:90%; margin:0 5%; background:#fff; border-radius:0.5em; display:inline-block; position:absolute; top:50%; margin-top:-11em; overflow:hidden;}
		.popup em{width:5em; height:5em; display:block; margin:0 auto; padding:3em 0 2em;}
		.popup p{font-size:1.1em; color:#000; padding:0 2em; line-height:1.5em; font-family:"微软雅黑";}
		.pic_step{position:relative; height:19.5em;}
		.main_image{height:18em;position:relative;}
		.main_image ul{width:100%;height:18em;overflow:hidden;position:absolute;top:0;left:0}
		.main_image li{width:20em;height:18em;margin:0 auto;}
		.main_image li img{display:block; margin:0 auto;}
		.main_image li a{display:block;}

		.swiper-container,.swiper-wrapper{width:100%; margin-top:-10px;}
		.swiper-slide{width:100%;text-align:center; font-size:18px; -webkit-box-pack:center; -ms-flex-pack:center; -webkit-justify-content:center; justify-content:center; -webkit-box-align:center; -ms-flex-align:center;
		-webkit-align-items:center; align-items:center; float:left;}
		.swiper-wrapper img{width:260px; display:block; margin:0 auto;}
		.main_num{position:absolute;top:19em;left:50%;z-index:999;width:300px;height:21px;margin:0 0 0 -50px;}
		.main_num a{float:left;width:0.4em;height:0.4em;display:inline-block;border:2px solid #666;border-radius:0.4em;margin:0 0.2em;background:#fff;}
		.main_num a.on{background:#666;}
		.set_btn{position:relative;}
		.set{margin:2.2em 1.5em 0; background:#ff8003; border-radius:0.3em; height:3.5em; line-height:3.5em; text-align:center;}
		.set a{font-size:1.25em; color:#fff; font-weight:bold;}
		.main b{font-weight:normal; font-size:1.8em; color:#999; position:absolute; z-index:9999; top:3px; right:10px;}
		.last_btn{margin:10px 20px; height:2em; line-height:2em; border-radius:0.3em; display:none; background:#ff8003; font-size:1.1em; font-weight:bold; text-align:center; color:#fff;}
		.main_finish{display: none;}
		.main_tit h1{font-size:1.1em; font-weight:normal; padding:0.5em 0; text-align:center;}
		.main_tit h1 i{font-style:normal; color:#ff8003;}
		.container{margin:0 20px 1.7em;}
		.choose_nav li a{display:block; font-size:0.9em; color:#999;}
		.choose_nav li.on a{font-size:1.1em; color:#e5b280;}
		.bar-unfill{height:10px; display:block; background:#bfbfbf; width:100%; border-radius:6px;}
		.bar-fill{
			width:100%;
			height:10px;
			display:block;
			background:#ff8003;
			border-radius:6px;
			-webkit-transition:width 10s ease;
			-moz-transition:width 10s ease;
			transition:width 10s ease;
			-webkit-animation:progressbar 10s 1;
			animation:progressbar 10s 1;
		}
		@-webkit-keyframes progressbar {
			from {
				width:0
			}
			to {
				width:100%;
			}
		}
		@keyframes progressbar {
			from {
				width:0
			}
			to {
				width:100%
			}
		}

		.login_bg{width:100%; height:100%; background:rgba(0,0,0,.5); position:fixed; top:0; left:0; z-index:2;}
		.login{width:19em; height:17em; background:#fff; border-radius:0.5em; position:fixed; top:50%; left:50%; overflow:hidden; margin:-8.5em 0 0 -9.5em;}
		.logo_key{position:relative; width:3.125em; margin:3.4em auto 2.8em;}
		.logo_key span{width:3.125em; display:block;}
		.logo_key em{width:2.9em; display:block; position:absolute; top:-1em; right:-1.5em;}
		.login h1{font-size:1.3em; color:#666; text-align:center; font-weight:normal;}
		.login p{font-size:0.875em; color:#666; text-align:center; padding-top:1.7em;}
		@media screen and (max-width:320px){
			.logo{margin:2em auto 1em;}
			.box h1{padding-top:1.5em;}
			.step_btn a{height:2.8rem; line-height:2.8rem; margin:1.2em 2.5em 0;}
			.step_btn a em {margin:0 0.9em 0 2.5em; }
			.bottom{font-size:0.875em;}
		}
	</style>

<div class="bg2">
	<div class="main">
		<div class="main_tit">
			<h1><i>"秒赚钥匙"</i>安装中...</h1>
			<div class="container">
			    <div class="bar">
			        <span class="bar-unfill">
			            <span class="bar-fill"></span>
			        </span>
			     </div>
			</div>
		</div>
		<div style="padding:10px 0;" class="main_tit main_finish">
			<h1>安装完成</h1>
		</div>
		<b>×</b>
		<#if isIOS9>
		<div class="swiper-container" style="">
	         <div class="swiper-wrapper">
	            <div class="swiper-slide"><img src="${util.static}images/img/img1.jpg"></div>
	            <div class="swiper-slide"><img src="${util.static}images/img/img2.jpg"></div>
	            <div class="swiper-slide"><img src="${util.static}images/img/img3.jpg"></div>
	            <div class="swiper-slide"><img src="${util.static}images/img/img4.jpg"></div>
	            <div class="swiper-slide"><img src="${util.static}images/img/img5.jpg"></div>
	            <div class="swiper-slide">
	            	<img src="${util.static}images/img/img6.jpg">
	            </div>
	        </div>
	        <!-- Add Pagination -->
	        <div class="swiper-pagination"></div>
	    </div>
		<div class="last_btn" id="ios9-cert">前往企业证书</div>
		</#if>
	</div>
</div>

<div class="box">
	<div class="logo">
		<img src="${util.static}/images/img/name.png" />
	</div>
	<div class="step_btn"><a class="installed" href="${yaoshiScheme!'#'}">已安装秒赚钥匙，点这里开始赚钱</a></div>
	<h1>未安装秒赚钥匙按以下步骤安装</h1>
	<div class="step">
		<div class="step_btn"><a class="step1"><em>第一步</em>安装秒赚钥匙</a></div>
		<div class="step_btn"><a class="step1_finish"><em>第一步</em>安装完成</a></div>
		<#if isIOS9>
			<div class="step_btn"><a class="step2"><em>第二步</em>点击直接设置</a></div>
			<div class="step_btn"><a class="step3"><em>第三步</em>去桌面打开秒赚钥匙</a></div>
		<#else>
			<div class="step_btn"><a class="step3"><em>第二步</em>去桌面打开秒赚钥匙</a></div>
		</#if>
	</div>
	<div class="bottom">若出现闪退，请卸载并重新安装秒赚钥匙</div>
</div>

<script type="text/javascript">
$(function(){

	$('.bg2').hide();
	$('.step1_finish').hide();
	$('.step1,.step1_finish').click(function(){
		var $this = $(this);
		$('.bg2').show();
		initSwiper();
		$(".main_tit").show();
		$(".main_finish").hide();
		$(".last_btn").hide();
		setTimeout(function () {
			$('.bg2').hide();
			$this.hide();
			$(".step1_finish").show();
		}, 10000);
		location.href = '/www/downloads/ysurl';
	})

	$('.main b').on('click',function(){
		$('.bg2').hide();
	})
	$('.step2').click(function(){
		$(".main_tit").hide();
		$(".main_finish").show();
		$(".last_btn").show();
		$('.bg2').show();
		initSwiper();
	});
	$('#ios9-cert').click(function(){
		location.href = 'prefs:root=General&path=ManagedConfigurationList';
	});
	function initSwiper(){
		var swiper = new Swiper('.swiper-container', {
			pagination: '.swiper-pagination',
			loop: true,
			paginationClickable: true,
			autoplay: 1500,
			loopAdditionalSlides : 1
		});
	}
});
</script>
<#include "/common/footer.ftl" />