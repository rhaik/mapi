<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<link rel="stylesheet" type="text/css" href="${util.swipercss}">
<style type="text/css">
	.body{background:#f0eff5;}
	.swiper-container {
        width: 100%;
        padding: 0.8em 0;
        background: #fff;
    }
    .swiper-slide {
        text-align: center;
        font-size: 18px;
        background: #fff;
		background: #000;
        /* Center slide text vertically */
        display: -webkit-box;
        display: -ms-flexbox;
        display: -webkit-flex;
        display: flex;
        -webkit-box-pack: center;
        -ms-flex-pack: center;
        -webkit-justify-content: center;
        justify-content: center;
        -webkit-box-align: center;
        -ms-flex-align: center;
        -webkit-align-items: center;
        align-items: center;
    }
    .wrap{padding-bottom:4.4em;}
    .test_list{padding:0.75em 0.9em; background:#fff; border-top:1px solid #dcdcdc; border-bottom:1px solid #dcdcdc; margin-top:20px;}
	.test_left{padding:0 0 0 5em;}
	.text_header{width:4.2em; height:4.2em; margin-left:-5em; display:block; float:left;}
	.test_left_txt a{width:100%; height:1.3em; overflow:hidden; text-overflow:ellipsis; display:-webkit-box; -webkit-line-clamp: 1; -webkit-box-orient:vertical; font-size:1em; line-height:1.3em; color:#000; margin-top:0.3em; margin-bottom:0.4em;}
	.test_left_txt span{font-size:0.8em; color:#666; display:block; margin-top:0.2em;}
	.test_left_txt span em{width:0.85em; height:0.85em; display:inline-block; background:url(${util.static}images/task_11.png) 0 0 / 100% 100% no-repeat; margin-right:0.2em;}
	.tit{font-size:0.8em; color:#222; padding:0.5em 0.9em; font-weight:normal;background:#f0eff5;}
	.introduce{padding: 0.8em 0.9em; background:#fff; font-size:0.875em; color:#444; line-height:22px;}
	.download_btn{position:fixed; bottom:0; left:0; width:100%; background:#fff; z-index:9;}
	.download_btn a{height:2.8em; line-height:2.8em; margin:0.8em 0.9em; display:block; background:#ff8003; border-radius:0.3em; font-size:1em; color:#fff; text-align:center;}
	.full_screen{height:100%; position:fixed; top:0; left:0; z-index:999; background:rgba(0,0,0,.8); display:none;}
	.full_screen_pic{float:left; width:375px;}
</style>
</head> 
<body> 
<div class="wrap">
	<div class="test_list on">
		<div class="test_left clearfix">
			<div class="text_header"><img width="100%" src="${util.static}images/img/ico1.png"></div>
			<div class="test_left_txt fl">
				<a>${game.name}</a>
				<span><em></em>${game.download_size}MB</span>
				<span>${game.title}</span>
			</div>
		</div>
	</div>
	<h1 class="tit">游戏截图</h1>
	<div class="swiper-container">
        <div class="swiper-wrapper">
        <#if game.images??>
        	<#assign imageArray=(game.images)?eval/>
				<#list imageArray as image>
           			 <div class="swiper-slide"><img width="100%" src="${image}" /></div>
				</#list>
		</#if>
        </div>
    </div>
    <h1 class="tit">游戏介绍</h1>
    <div class="introduce">${game.description}</div>
    <div class="download_btn">
    	<a href="${game.url}">立即下载</a>
    </div>
</div>
<script src="${util.swiperjs}"></script>
<script src="${util.jquery}"></script>
<script>
	var swiper = new Swiper('.swiper-container', {
        pagination: '.swiper-pagination',
        slidesPerView: 2.3,
        paginationClickable: true,
        spaceBetween: 20,
    });   
</script>
<script type="text/javascript">
$(function(){
	$('.swiper-slide').click(function(){
		var index = $(this).index();
		$('.full_screen').show();
		$('.full_screen_pic').eq(index).show().siblings().hide();
	})
	$('.full_screen_pic').click(function(){
		$('.full_screen').hide();
	})
	
	if(window.MiJSBridge){	//window.MiJSBridge对象不为空，可以直接调用
	     checkAppInstall();
	}else{
	     document.addEventListener('MiJSBridgeReady', checkAppInstall);
	}
	
	var isInstall = false;
	function checkAppInstall(){
		if(typeof MiJSBridge !="undefined"){
			MiJSBridge.call('checkApp', {protocol: '${game.agreement!''}', bundle:'${game.bundle_id!''}'}, function(ret){
				if(ret.installed){
					isInstall=true;
					$(".download_btn a").html("打开");
				}
			});
		}
	};
	
	$(".download_btn a").click(function(){
		if(typeof MiJSBridge !="undefined"){
			if(isInstall){
				MiJSBridge.call('launchApp', {protocol: '${game.agreement!''}', bundle:'${game.bundle_id!''}'});
			}else{
				MiJSBridge.call('downLoad', {downloadUrl: '${game.url!''}',name:'${game.name!''}',bundle:'${game.bundle_id!''}'});
			}
		}
	});
});
</script>
<#include "/common/footer.ftl">