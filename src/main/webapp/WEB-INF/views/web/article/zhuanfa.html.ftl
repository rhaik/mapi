<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<head>
<head> 
	<meta charset="utf-8"> 
	<meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1, user-scalable=no">
	<meta name = "format-detection" content="telephone = no" />
	<title>转发 </title> 
	<!-- <link rel="stylesheet" type="text/css" href="${util.static}css/css.css"> -->
	<link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
	<script src="${util.jquery}"></script>
	<style type="text/css">
		body{background:#3acce8;}
		.head_pic{width:3em; margin:0.8em auto;}
		.head_pic a{width:3em; border:2px solid #fff; border-radius:3em; overflow:hidden; display:block;}
		.head_pic span{width:100%; font-size:0.7em; color: #fff; text-align:center; display:block; padding:0.5em 0 0 2px;}
		.head_txt{background:#fff; border-radius:0.6em; border:1px solid #e5e5e5; margin:0 1.56em; overflow:hidden;}
		.head_txt p{font-size:0.9em; color:#000; margin:1.2em 1.8em; line-height:1.3em; text-align: center;}
		.head_txt a{width:0.75em; height:0.75em; position:absolute; display:block; background:url(${util.static}images/img/icon.png) no-repeat; background-size:20.6em 20.6em;}
		.head_txt a.left{left:0.56em; top:1.2em; background-position:-2em -2.8em;}
		.head_txt a.right{right:0.56em; top:1.2em; background-position:-3.95em -2.8em}
		.hongbao{margin:1.875em 1.56em; position:relative;}
		.number{position:absolute; top:0; left: 0; width:100%;}
		.number p{font-size:0.75em; color:#7f2424; text-align: center; padding:0.5em 0 0.3em;}
		.number p.ipt_num{padding:1.3em 0 0.8em;}
		.number input{width:10em; height:2.8em; line-height:2.8em; background:#fff; border-radius:0.3em; margin:0 auto 0.75em; outline:none; border:none; display:block; text-align:center; font-size:1em; color:#000;}
		.number a{width:10em; height:2.8em; line-height:2.8em; background:#dcdcdc; border-radius:0.3em; margin:0 auto 0.75em; display:block; text-align:center; font-size:1em; color:#999;}
		.number a.on{background:#ee5857; color:#fff;}
		.number span{width:100%; display:block; text-align:center; font-size:0.75em; color:#7f2424; margin-top:5.5em;}
		.btn{margin:2.34em 1.56em; height:2.8em; line-height:2.8em;}
		.btn a{width:100%; background:#ff8003; border-radius:0.3em; display:block; text-align:center; font-size:1em; color:#fff;}

		.number i{font-style:normal; font-size:2.8em; color:#ff0000; text-align:center; display:block; padding-top:0.1em;}
		.number i em{font-style:normal; font-size:1.56rem;}
		.borber img{padding-top:1em;}
		.find_box h1{font-size:0.75em; color:#222; text-align:center; font-weight:normal; padding:1em 0 0.625em; border-bottom:1px solid #dcdcdc;}
		.find_list{padding:0.625em 0 0.625em 3.86em;}
		.find_pic{width:2.3em; height:2.3em; border-radius:2.3em; overflow:hidden; margin-left:-2.3em; margin-right:0.625em;}
		.text{border-bottom:1px solid #dcdcdc; padding-bottom:0.625em;}
		.text_left p{font-size:0.85em; color:#000; padding-top:0.1em;}
		.text_left span{font-size:0.75em; color:#666; padding-top:0.3em; display:block;}
		.text_right{font-size:0.85em; color:#666; line-height:2.3em; padding-right:1.56em;}
		.number i em{font-style:normal; font-size:1.56rem;}
	</style>
</head> 

<body>
<div class="head">
	<div class="head_pic">
		<a><img src="${user.headImg}"></a>
		<span>${user.name}</span>
	</div>
	<div class="" id="preBindMobileDiv">
		<div class="head_txt posi_r">
			<a class="left"></a>
			<p>超过900万人加入秒赚大钱，累计发放现金3660万，邀请您一起来，新用户有大礼哦</p>
			<a class="right"></a>
		</div>
		<div class="hongbao">
			<div class="hongbao_box"><img src="${util.static}images/img/hongbao.png"></div>
			<div class="number">
				<p class="ipt_num">输入手机号，领取现金</p>
				<input  type="tel" placeholder="请输入您的手机号" class="mobile"/>
				<a class="get_packet <#if isWexin?? && isWexin>on</#if>">领取红包</a>
				<span><b>·</b> 仅限新用户参加哦</span>
			</div>
		</div>
		<#if isWexin?? && isWexin>
		<#else>
			<div class="btn">
				<a class="share_btn">立即转发</a>
			</div>
		</#if>
	</div>
	<div class="dn" id="afterBindMobileDiv">
		<div class="head_txt posi_r">
			<a class="left"></a>
			<p >恭喜您获得红包</p>
			<a class="right"></a>
		</div>
		<div class="hongbao">
			<div class="hongbao_box"><img src="${util.static}images/img/hongbao.png"></div>
			<div class="number">
				<i class="money"></i>
				<p class="mobile_message">现金红包已经放入您的账户：<br/>13121745623</p>
				<a class="on" href="/weixin/user/income.html">查看收入</a>
				<span><b>·</b> 仅限新用户参加哦</span>
			</div>
			<div class="borber"><img src="${util.static}images/img/border.png" /></div>
		</div>
		<#if friendLogList?? >
			<div class="find_box bg_f">
			<h1>查看好友成绩</h1>
			<#list friendLogList as friendIncomeLog >
				<#if friendIncomeLog.user??>
					<div class="find_list">
						<div class="find_pic fl"><img src="${friendIncomeLog.user.headImg}" /></div>
						<div class="text clearfix">
							<div class="text_left fl">
								<p>${friendIncomeLog.user.name}</p>
								<span>${friendIncomeLog.beforMinute}</span>
							</div>
							<div class="text_right fr">${friendIncomeLog.remarks}</div>
						</div>
					</div>
				</#if>
			</#list>
			</div>
		</#if>
	</div>
</div>
<script type="text/javaScript" src="${util.static}js/validator.js"></script>
<script type="text/javaScript">
	
	$(".hongbao .number .get_packet").click(function(){
		var mobile = $(".hongbao .number .mobile").val();
		if(mobile.length != 11 || !IsMobilePhone(mobile)){
			alert("不是有效的手机号");
			return ;
		}
		
		$.ajax({type: "POST",url: "${util.ctx}/weixin/api/new_user_bind_forward",dataType:'json',data:{'mobile':mobile,'d':'${sign}'},timeout: 5000
						,success: function(r){
					   		var message =r.message;
					   		if(r.code!='0'){
					   			//alert(r.message);
					   		}else{
					   			if(r.amount){
					   				$(".hongbao .number .money").html("<em>¥</em> "+r.amount);
					   				message = "现金红包已经放入您的账户："+r.message;
					   			}
					   		}
				   			$(".hongbao .number .mobile_message").html(message);
				   			$("#preBindMobileDiv").addClass("dn");
				   			$("#afterBindMobileDiv").removeClass("dn");
					   },
					   error: function(xhr) {
					  		alert('请求出错(请检查相关度网络状况)');
		                }
		});
		
	});
</script>
<script type="text/javaScript">
	<#if isWexin?? && isWexin>
		
	<#else>
		$(".hongbao .number .get_packet").attr('disabled','disabled');
		$(".hongbao .number .mobile").attr('disabled','disabled');
		$(".btn .share_btn").click(function(){
			if(typeof MiJSBridge !="undefined"){
				MiJSBridge.call('share', {title:"点我一下,现金到账!", content:"新人点击即可获得2-3元红包！秒赚大钱发钱啦,快来领钱啊！",image:"${logo!''}", url:"${shareUrl!''}"},
				 function(ret){
					if(typeof _MiJS != 'undefined' && _MiJS.os.android){
							MiJSBridge.call('ajax', { url: '/web/article/article/share_success', data: {'se_id':'${se_id!''}','d_id':'${_data!''}'}, type:'POST'}
								, onShareSuccess);
						}else{
							$.ajax({
				   			type: "POST",
				   			url: '/web/article/article/share_success',
				   			data: {'se_id':'${se_id!''}','d_id':'${_data!''}'},
				   			dataType:'json',
				   			success:onShareSuccess
						});
					}
				});
			}
		});

	function onShareSuccess(result){
		var message = result.message;
		if(result.code && result.code){
			message = '转发成功';
		}
		MiJSBridge.call('alert', {message:message}, function(){ console.log('OK');});
	}
	</#if>
</script>
</body> 
<#include "/weixin/wx_share.ftl">
<#include "/common/footer.ftl">