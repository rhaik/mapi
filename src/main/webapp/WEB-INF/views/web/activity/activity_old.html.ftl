<#import "/lib/util.ftl" as util>
<#include "/common/headerNews.ftl">
 <link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
	<style type="text/css">
		/* 公共部分开始 */
		body{background:#ee5857;}
		/* 公共部分结束 */
		.con_top h1{font-size:1.2em; color:#fff; font-weight:normal; text-align:center; padding-top:1.6em;}
		.con_top h1 i{font-style:normal; font-weight:normal;}
		.con_top p{font-size:0.875em; color:#fffb91; text-align:center; padding:0.5em 0 0.93em;}
		.friend_head_txt{background:#fff; border-radius:0.6em; border:1px solid #e5e5e5; margin:0 0.83em 1.5em; overflow:hidden;}
		.friend_head_txt p{font-size:0.75rem; color:#222; margin:0.6em 2.2em; line-height:1.7em; padding:0;text-align:left;}
		.friend_head_txt p.text1{text-align:center;}
		.friend_head_txt p.text1 a{font-size:0.94rem; text-align:center; color:#ee5857; border-bottom:1px solid #ee5857; display:inline-block;}
		.friend_head_txt p.text1 span{display:block; padding-top:0.5em;}
		.friend_head_txt em{width:0.75em; height:0.75em; position:absolute; display:block; background:url(${util.static}images/img/icon.png) no-repeat; background-size:20.6em 20.6em;}
		.friend_head_txt em.left{left:0.56em; top:0.5em; background-position:-2em -2.8em;}
		.friend_head_txt em.right{right:0.56em; top:0.5em; background-position:-3.95em -2.8em}
		.yichai,.weichai{display:block; margin:0 8%; position:relative;}
		.weichai a{width:5.6em; height:5.6em; line-height:5.6em; border-radius:5em; position:absolute; top:50%; left:50%; margin:-2.8em 0 0 -2.8em; font-size:1.25em; color:#fff; text-align:center;}
		.yichai p{width:100%; color:#ee5857; position:absolute; top:2.3em; left:0; text-align:center;}
		.yichai p em{font-size:2.25em; font-style:normal;}
		.yichai p i{font-size:1.1em; font-style:normal;}
		.yichai span{width:100%; position:absolute; top:7em; left:0; text-align:center; font-size:0.75em; color:#7f2424;}
		.button{width:13.6em; height:2.2em; line-height:2.2em; background:#ffaa3b; border-radius:0.3em; position:absolute; bottom:1.9em; left:50%; margin-left:-6.9em;}
		.button a{font-size:0.94em; display:block; text-align:center;}
		.border{width:100%; margin:-5px 0 -5px;}
		.main_box{background:#fff; padding:1.5em 0;}
		.main{margin:0 6%; border:1px solid #ee5857; border-radius:0.2em; position:relative;}
		.look_score{text-align:center; font-size:0.75em; color:#fff; font-weight:normal; padding:1em 0;}
		.score_list{padding:0 0 0 4em;}
		.score_pic{width:2.34em; height:2.34em; margin:0.5em 0 0.5em -2.8em; float:left; border-radius:2em; overflow:hidden;}
		.score_left{padding:0.5em 0 0 0.5em; border-bottom:1px solid #E74D45; margin-right:0.83em;}
		.score_text b{width:4.2em; height:1em; line-height:1em; overflow:hidden; font-size:0.8em; color:#fff; font-weight:normal; margin-top:0.3em; display:block;}
		.score_text em{font-style:normal; font-size:0.75em; color:#fff; margin-top:0.5em; display:block;}
		.score_right{float:right; font-size:0.75em; color:#fff; line-height:2.8em;}
		#gundong{height:10em; overflow:hidden;}
	</style>
</head> 
<body>
<div class="content">
	<div class="con_top">
		<h1>您还有<i id="nums"><#if userDraw??>${userDraw.balance_times}<#else>0</#if></i>次拆红包机会</h1>
		<h1 style="display:none;">您还未满足拆红包的条件</h1><!-- 没有徒弟 -->
		<p>已拆过<i id="balance_nums"><#if userDraw??>${userDraw.incre_times}<#else>0</#if></i>个红包</p>
		<div class="friend_head_txt posi_r">
			<em class="left"></em>
			<p class="text1">
				<a href="http://mp.weixin.qq.com/s?__biz=MjM5MDI5NDkwMQ==&mid=403250123&idx=1&sn=1eab50b444954755e0c24d14f98884b2">点击查看活动详情&gt;&gt; </a>
				<span>1、收徒得红包&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2、徒弟提现得红包</span>
			</p>
			<em class="right"></em>
		</div>
	</div>
	<div class="border"><img width="100%" src="${util.static}images/img/bor_top.png" /></div>
	<div class="main_box">
		<!-- 未拆红包 -->
	<#if userDraw?? && ((userDraw.balance_times) > 0)>
		<div id="hongbao" class="weichai"><img width="100%" src="${util.static}images/img/hongbao_weichai.png" /><a>拆红包</a></div>
	<#else>
		<div class="weichai" ><img width="100%" src="${util.static}images/img/hongbao_weichai.png" /><a href="<#if fromSafari??>/ios/share.html<#else>/web/discovery/share.html</#if>">去收徒</a></div><!-- 没有徒弟 -->
	</#if>
		<!-- 已拆红包 -->
		<div class="yichai" style="display:none;"><img width="100%" src="${util.static}images/img/hongbao_yichai.png" />
			<p><em>0</em><i>元</i></p>
			<span>已存入我的收入，可直接提现</span>
			<div class="button">
				<a>继续拆红包</a>
			</div>
		</div>
	</div>
	<div class="border"><img width="100%" src="${util.static}images/img/bor_bot.png" /></div>
	<div class="score">
		<h1 class="look_score">看看赚友成绩</h1>
			<div id="gundong">
				<div class="gundong_con">
					<#list logs as log>
					<#if log??>
					<div class="score_list clearfix">
						<div class="score_pic"><img width="100%" src="${log.user.headImg}"></div>
						<div class="score_left clearfix">
							<div class="score_text fl">
								<b>${log.user.uniName}</b>
								<em>${log.userDrawLog.timeText}</em>
							</div>
							<span class="score_right">抢到红包${util.fen2yuan(log.userDrawLog.draw_amount)}元</span>
						</div>
					</div>
				</#if>
				</#list>
				</div>
		</div>
	</div>

<script src="${util.jquery}"></script>
<script type="text/javascript">
	$("#hongbao a").click(function(){
		draw();
	});
		
	$(".button a").click(function(){	
		draw();
	});
	
	function draw(){
		
		var tm = parseInt($(".button a").attr('tm'));
		var now = new Date().getTime();
		if(tm > 0 && (now - tm) < 5000){
			return false;
		}
		$(".button a").attr('tm', now);
		
		var num= parseInt($("#nums").text());
		if(num <= 0){
			$(".button a").attr("href","<#if fromSafari??>/ios/share.html<#else>/web/discovery/share.html</#if>");
			$(".button a").text("去收徒，赚拆红包机会");	
			hongbaoShow();
			return ;
		}else{
			num = num-1;
			$("#nums").text(num);
		}	
		$(".button a").attr('disabled',"true");
		var url='<#if fromSafari??>/ios/api/activity/draw<#else>/api/v1/activity/draw</#if>';
		
		if(typeof _MiJS != 'undefined' && _MiJS.os.android){
			MiJSBridge.call('ajax', { url: url, data: {'se_id':'${se_id!''}','d_id':'${_data!''}'}, type:'POST'}
				, openHongbao);
		}else{
			$.ajax({
				type: "POST",
				url: url,
				data: {'se_id':'${se_id!''}','d_id':'${_data!''}'},
				dataType:'json',
				success:openHongbao
			});
		}
	}
	
	function openHongbao(ret){
		if(ret){
			if(ret.code != '0'){
				showAlert(ret.message);
			}else{
				$(".yichai p em").html(ret.data.amount);
				$("#nums").html(ret.data.balance_times);
				$("#balance_nums").html(ret.data.incre_times);
				hongbaoShow();
				showAlert('恭喜您获得红包'+ret.data.amount+'元');
			}
		}
		$(".button a").removeAttr("disabled");
	}
	
	function hongbaoShow(){
		$("#hongbao").hide();
		$(".yichai").show();
	}
	
	function showAlert(message){
		if(typeof MiJSBridge=="object"){
			MiJSBridge.call('alert', {message:message}, function(){ console.log('OK');});
		}else{
			alert(message);
		}
	}
</script>
<script type="text/javascript">
	var $this = $("#gundong"); 
	var scrollTimer; 
	$this.hover(function() { 
		clearInterval(scrollTimer); 
	}, function() { 
		scrollTimer = setInterval(function() { 
			scrollNews($this); 
		}, 1200); 
	}).trigger("mouseleave"); 

	function scrollNews(obj) { 
		var $self = obj.find(".gundong_con"); 
		var lineHeight = $self.find(".score_list:first").height(); 
		$self.animate({ 
			"marginTop": -lineHeight + "px" 
		}, 1000, function() { 
			$self.css({ 
				marginTop: 0 
			}).find(".score_list:first").appendTo($self); 
		}) 
	} 
</script>
<#include "/common/footer.ftl">	