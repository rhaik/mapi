$(function(){
    var isWithdraw = false;

	$('#withdraw-btn').click(function(){
		if(withdraw_config.balance < 1000) {
			showTips("提现余额不足十元！");
		    return ;
		}
		if(!withdraw_config.mobile){
			$('#no-mobile-dialog').dialog('show');
			return;
		}

		if(withdraw_config.wx_bank_name == "" && withdraw_config.alipay_account == ""){
			$(".ui-dialog-tip").dialog("show");
			return ;
		}
		$("#withdraw-dialog").dialog("show");
		return false;
	});

	$('ul.amount li.enabled').click(function(){
		var fee = parseInt($("div.way p.on").data('fee')) || 0;
		var $this = $(this);
		if(!$this.hasClass('on')){
			$this.addClass('on').siblings().removeClass('on');
			$this.parents('.bill_box').find('.drawAmount').text( parseInt($this.data('amount'))/ 100 - fee);
		}
	});


	$('#setting').click(function(){
		$(".ui-dialog-tip").dialog("hide");
		isWithdraw = true;
		//location.href = '/web/my/withdraw_type.html';
	});

	$('#setting-mobile').click(function(){
		isWithdraw = true;
	});



	function showTips(message) {
		if(typeof MiJSBridge=="object") {
			 MiJSBridge.call('alert', {title: message}, function(){});
		} else {
			var el=Zepto.tips({
		        content:message,
		        stayTime:2000,
		        type:"info"
		    })
		    el.on("tips:hide",function(){
		        console.log("tips hide");
		    })
		}
	}
	$('#save-btn').click(function(){
		if($(this).hasClass('grey'))return;
		var $withdrawChoice = $('.bill_box.selected li.on');
		if(!$withdrawChoice.size()) {
			showTips("请选择提现金额！");
		    return ;
		}
		var amount = parseInt($withdrawChoice.data('amount'));
		if(amount > withdraw_config.balance) {
			showTips("您的余额不足！");
		    return ;
		}
		var type = parseInt($("div.way p.on").data('type'));
		if(isNaN(type)) {
			showTips("请选择提现方式！");
			return false;
		}
		if(type=="1" && withdraw_config.wx_bank_name =="") {
			showTips("微信账号没有绑定，请先绑定后再提现！");
			return false;
		}
		if(type=="2" && withdraw_config.alipay_account =="") {
			showTips("支付宝账号没有绑定，请先绑定后再提现！");
			return false;
		}
		var tm = parseInt($(this).attr('tm'));
		var now = new Date().getTime();
		if(tm > 0 && (now - tm) < 5000){
			return false;
		}
		$(this).attr('tm', now);

		doWithdraw(type, amount);
	});

	function doWithdraw(type, amount){
		if(typeof _MiJS != 'undefined' && _MiJS.os.android){
			MiJSBridge.call('ajax', {type: 'POST', url: withdraw_config.enchash_url, data: 'type=' + type + '&amount=' + amount }, onWithdrawSuccess);
		}else{
			$.ajax({
				   type: "POST",
				   url: withdraw_config.enchash_url,
				   data: {type:type, amount:amount},
				   dataType:'json',
				   success:onWithdrawSuccess
			});
		}
	}

	function onWithdrawSuccess(r){
		$(".ui-dialog").dialog("hide");
   		var message = r.code != 0 ? r.message : '提现提交成功，一个工作日到账！';
   		if(typeof MiJSBridge=="object") {
   			MiJSBridge.call('alert', {title: message}, function(){ window.location.reload();});
   		} else {
   			alert(message);
   			window.location.reload();
   		}
	}


	$('.ui-dialog').click(function(e){
		if($(this).is('#masked-dialog')) return;
	 	var className = $(e.target).attr("className");
		if(className.indexOf('ui-dialog ') >=0) {
			$('.ui-dialog').dialog("hide");
		}
	});

	$("div.way p").click(function(){
		$(this).addClass("on").siblings().removeClass("on");
		var index=$(this).index();
		$(".bill_box").eq(index).addClass("selected").siblings().removeClass("selected");
		var $choice = $('.bill_box.selected li.on');
		if(!$choice.size()){
			var withdrawChoices = $('.bill_box.selected li.enabled');
			withdrawChoices.size() > 1? withdrawChoices.eq(1).click() : withdrawChoices.eq(0).click();
		}
		if(!$('.bill_box.selected li.enabled').size() || $(this).data('disabled')){
			$('#save-btn').addClass('grey');
		}else {
			$('#save-btn').removeClass('grey');
		}
	});

	$("div.way p").eq(0).click();

	//检查cookie里面是否是提现后的刷新
	if($.cookie('IS_WITHDRAW')){
		$.cookie('IS_WITHDRAW', 0, -1);
		$('#withdraw-btn').click();
	}

	$.onPageShowAgain = function(){
		if(isWithdraw){//如果当前正在提现，则刷新页面
			$.cookie('IS_WITHDRAW', 1, 60),
			location.reload();
		}
	};
});