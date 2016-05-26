<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
<!--
body{background-color:#f0eff5}
.info-box {margin:10px 10px 5px 10px;}
.btn-normal {background-color:#ff8003;background-image:none;color:#fff;}
.withdraw-info {margin:20px 15px; color: red;}
.withdraw-info h3 {font-size: 1.1em; font-weight:500; margin-bottom:.5em;}
.withdraw-info p {margin-bottom:.3em;}
-->
</style>
<div class="info-box"><h4><#if (type==2)>绑定实名认证的支付宝账号才能收款<#else>绑定微信钱包的银行卡姓名</#if></h4></div>
<div class="ui-form ui-border-t">
    <form action="#" id="account-form">
    	<#if (type==2)>
    	<input type="hidden" name="type" value="${type}" />
        <div class="ui-form-item ui-form-item-pure ui-border-b">
            <input type="text" value="${ue.alipay_name!''}" placeholder="输入支付宝姓名" name="alipay_name" id="ali-name-input">
        </div>
        <div class="ui-form-item ui-form-item-pure ui-border-b">
            <input type="text" value="${ue.alipay_account!''}" placeholder="输入支付宝账号" name="alipay_account" id="ali-account-input">
        </div>
        <#else>
        <div class="ui-form-item ui-form-item-pure ui-border-b">
            <input type="text" value="${ue.wx_bank_name!''}" placeholder="输入银行卡姓名" name="wx_bank_name" id="wx-name-input">
        </div>
        </#if>
    </form>
    <div class="ui-btn-wrap" style="background-color:#f0eff5;">
	    <button class="ui-btn-lg btn-normal" id="submit-btn">
	        确定
	    </button>
	</div>
</div>
<div class="withdraw-info">
	<h3>填写说明</h3>
	<#if type==2>
		<p>支付宝姓名是支付宝实名认证的真实姓名。支付宝账号是登录支付宝使用的邮箱或者手机号。比如：</p>
		<p>张国辉 13820678965<br/>陈晨 152786587@qq.com</p>
		<p>填写有误会影响到您的提现到账时间，请仔细填写。</p>
	<#else>
		<p>必须先关注秒赚大钱官方微信账号，才能进行微信提现</p>
		<p>银行卡姓名指的是微信钱包中绑定过的银行卡的持有人真实姓名，例如：王伟</p>
	</#if>
</div>
<script>
$(function(){
	var type = ${type};
	$('#submit-btn').click(function(){
		if(type == 2){
			if(!$('#ali-name-input').val().trim()){
				showTips('请输入支付宝姓名');
				return;
			}
			if(!$('#ali-account-input').val().trim()){
				showTips('请输入支付宝账号');
				return;
			}
		}else if(!$('#wx-name-input').val().trim()){
			showTips('请输入姓名');
			return;
		}
		
		doSubmit();
	});

	function doSubmit(){
		var url = (typeof _MiJS != 'undefined') ? "${util.ctx}/api/v1/enchashment/setting" :  "withdraw_setting.html";
		if(typeof _MiJS != 'undefined' && _MiJS.os.android){
			MiJSBridge.call('ajax', {type: 'POST', url: url, data: $('#account-form').serialize() }, onSubmitSuccess);
		}else{
			$.ajax({
			   type: "POST",
			   url: url,
			   data: $('#account-form').serialize(),
			   dataType:'json',
			   success: onSubmitSuccess
			});
		}
	}

	function onSubmitSuccess(json){
	    if(!json || json.code != 0){
			showTips(json.message || '网络错误，请稍后重试');
		}else{
			showDialog('设置' + (type == 2? '支付宝' : '微信') + '账号成功', function(index){
				if (typeof MiJSBridge != "undefined" ){
					MiJSBridge.call('close');
				}else {
					history.back();
				}
			});
		}
	}

	function showTips(message) {
		var el=Zepto.tips({
	        content:message,
	        stayTime:2000,
	        type:"info"
	    });
	}
	
	function showDialog(msg, cb){
		var dialog = $.dialog({
	        title:'',
	        content:msg,
	        button:["确认"]
	    });
	    dialog.on("dialog:action",function(e){
			cb && cb(e);
		});
	}
});
</script>
<script src="${util.static}frozenjs/1.0.1/frozen.js"></script>
<#include "/weixin/wx_share.ftl">
<#include "/common/footer.ftl">