$(function(){

    $.initTaskList = function(){
        if(window.MiJSBridge){//window.MiJSBridge对象不为空，可以直接调用
			 onMiJSBridgeReady();
		}else{
			 document.addEventListener('MiJSBridgeReady', onMiJSBridgeReady);
		}

		function onMiJSBridgeReady(){
			$('#task-list').show();
			$('#tishi-box').css('top', 0).show();
			MiJSBridge.call('startDetect');
			MiJSBridge.on("onRefreshPage", function(){ location.reload(); } );

//			var appid = [];
//			var agreement =[];
//			setTimeout(function(){
//				if(appid.length > 0){
//					$.ajax({type: "POST",url: "/api/v1/task/repost_installed",data:{appid:appid,agreement:agreement},
//						success: function(r){
//							console.debug("success response"+r);
//						},
//						error: function(xhr) {
//							console.debug("error response"+xhr);
//						}
//					});
//				 }
//			},3000);
//
//			$('ul>li').each(function(index){
//				var $this = $(this);
//				if($this.hasClass('ui-li-finish')) return;
//
//				var prop = $this.attr('prop-id');
//				var bd = $this.attr('bd-id');
//
//				if(prop || bd){
//					MiJSBridge.call('checkApp', {protocol: prop, bundle : bd}, function(ret){
//						if(ret.installed) {
//							appid.push($this.data("pid"));
//							agreement.push(prop);
//
//							if($this.find('.haveing').size()){ //doing task
//								$this.find('.down-message').text('等待试用完成');
//							}else {
//								$this.addClass("ui-li-finish").find('.ui-btn').addClass('ui-btn-finish');
//								$this.find('.ui-li-taskimg').html("剩余0份");
//								$this.find(".down-message").html("任务已被抢光");
//								$this.appendTo('#task-list ul');
//							}
//						}
//					});
//				}
//			});

			$('ul>li').click(function(e){
				var $this = $(this);
				if($this.hasClass('no-click')) return ;

				if($this.hasClass('ui-li-finish')){
					var msg = $.trim($(this).find('.down-message').html());
					msg && showTips(msg);
					return;
				}
				if($this.hasClass('future-task')){
					showTips('任务未开始');
					return;
				}

				if($this.hasClass('friend-task')){
					location.href = '/ios/share.html';
					return;
				}

				if($this.find('.haveing').size()){
					openUrl('/web/task/' + $this.data('id'));
					return;
				}

				var appid = $this.data('pid');
				var prop = $this.attr('prop-id');
				var bd = $this.attr('bd-id');
				MiJSBridge.call('checkApp', {protocol: prop, bundle : bd}, function(ret){
					if(ret.installed) {
						$.ajax({type: "POST",url: "/api/v1/task/repost_installed",data:{appid:appid,agreement:prop} });
						MiJSBridge.call('alert', {title: '您已安装过该应用'}, function(){location.reload();});
					}else { //接任务
						doReceiveTask($this.data('id'));
					}
				});
			});

			//提现相关
			$('#tixian-btn').click(function(){
				var $this = $(this);

				if($this.hasClass('enchashing')) {
					return showTips('您的提现正在处理中，请耐心等候');
				}
				if(	userInfo.balance < 1000) {
					showTips("您的余额不足十元！");
					return ;
				}

				if(!userInfo.alipay_account){
					return showDialog('您未设置支付宝提现账号，点击确定立即设置', '确定', function(){
						openUrl('/web/my/withdraw_account.html?type=2');
					});
				}
				$('#withdraw-dialog').dialog('show');
			});

			$('#withdraw-dialog').click(function(e){
				var className = $(e.target).attr("className");
				if(className.indexOf('ui-dialog ') >=0) {
					$('.ui-dialog').dialog("hide");
				}
			}).on('click', 'a', function(){
				var url = $(this).data('url');
				url && openUrl(url);
			}).on('click', 'div.way p', function(){
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
			}).on('click', 'ul.amount li.enabled', function(){
				var fee = parseInt($("div.way p.on").data('fee')) || 0;
				var $this = $(this);
				if(!$this.hasClass('on')){
					$this.addClass('on').siblings().removeClass('on');
					$this.parents('.bill_box').find('.drawAmount').text( parseInt($this.data('amount'))/ 100 - fee);
				}
			});

			$('#save-btn').click(function(){
				if($(this).hasClass('grey'))return;
				var $withdrawChoice = $('.bill_box.selected li.on');
				if(!$withdrawChoice.size()) {
					showTips("请选择提现金额！");
					return ;
				}
				var amount = parseInt($withdrawChoice.data('amount'));
				if(amount > userInfo.balance) {
					showTips("您的余额不足！");
					return ;
				}
				var type = parseInt($("div.way p.on").data('type'));
				if(isNaN(type)) {
					showTips("请选择提现方式！");
					return false;
				}
				if(type=="1" && userInfo.wx_bank_name =="") {
					showTips("微信账号没有绑定，请先绑定后再提现！");
					return false;
				}
				if(type=="2" && userInfo.alipay_account =="") {
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
				showLoading();
				if(typeof _MiJS != 'undefined' && _MiJS.os.android){
					MiJSBridge.call('ajax', {type: 'POST', url: "/api/v1/enchashment/save", data: 'type=' + type + '&amount=' + amount }, onWithdrawSuccess);
				}else{
					$.ajax({
						   type: "POST",
						   url: "/api/v1/enchashment/save",
						   data: {type:type, amount:amount},
						   dataType:'json',
						   success:onWithdrawSuccess
					});
				}
			}

			function onWithdrawSuccess(r){
				hideLoading();
				$("#withdraw-dialog").dialog("hide");
				var message = r.code != 0 ? r.message : '提现提交成功，一个工作日到账！';
				MiJSBridge.call('alert', {title: message}, function(){ r.code == 0 && window.location.reload();});
			}

			$("div.way p").eq(0).click();
		}

		 function doReceiveTask(tid){
		 	showLoading();
			$.ajax({
			   type: "POST",
			   url: "/api/v1/ah/ha",
			   data: {task: tid},
			   dataType:'json',
			   success: function(r){
			   		hideLoading();
					if(r.code != 0) {
						showTips(r.message);
					} else {
						$(".ui-dialog").dialog("hide");
						showDialog('接收任务成功，按任务要求完成试玩即可获得奖励', '确定', function(){openUrl('/web/task/' + tid); });
					}
			   },
			   error: function(r) {
					hideLoading();
					showTips('出错了，请稍后再试吧！');
			   }
			});
		}
    }


    $.initTaskDetail = function(){
		var $detail = $('#detail-panel');
		var propid = $detail.attr('prop-id'), bdid = $detail.attr('bd-id');

		if(window.MiJSBridge){ //window.MiJSBridge对象不为空，可以直接调用
			 onMiJSBridgeReady();
		}else{
			 document.addEventListener('MiJSBridgeReady', onMiJSBridgeReady);
		}

		function onMiJSBridgeReady(){
			$('#detail-panel').on('click', '.launch-app', function(){ //打开应用
				showTips('正在打开应用...');
				var agreement = propid;
				if(!currentApp.agreement || currentApp.agreement.indexOf('NES_') >= 0 || currentApp.need_open){
					agreement = '';
				}
				openApp(agreement, bdid);
			}).on('click', '.copy-keyword', function(){ //点击复制
				MiJSBridge.call('copy', {message: currentApp.keyword}, function(result){
					showTips('复制成功，自动跳转AppStore', function(){
						openApp('https://itunes.apple.com/WebObjects/MZStore.woa/wa/search?media=software&country=CN&mt=8&term=');
					});
				});
			}).on('click', '.install-app', function(){ //直接下载
				openApp($(this).data(url) || currentApp.download_url);
			}).on('click', '.check-status', function(){ //直接下载
				checkTaskStatus(true);
			}).on('click', '.abort-task', function(){
				MiJSBridge.call('confirm', {title: (currentApp.installed? '系统检测到你已下载应用，如果现在放弃，可能导致以后无法再次完成本任务。' : '') + '确定放弃任务吗？'}, function(result){
					result.index && doAbortTask();
				});
			});

			countDownTimer();
			checkTaskTimer();
		}


		function countDownTimer(){
			var min = parseInt(currentApp.remain_time) || 0;
			$('#remain-time').html( min + ' 分钟');
			if(min > 0 && !currentApp.finished){
				setTimeout(function(){
					currentApp.remain_time = min - 1;
					countDownTimer();
				}, 60 * 1000);
			}
		}

		function checkTaskTimer(){
			checkTaskStatus();
			if(currentApp.finished){
				return;
			}else if(currentApp.installed){
				setTimeout(checkTaskTimer, 20 * 1000);
			}else {
				setTimeout(checkTaskTimer, 5000);
			}
		}


		function checkTaskStatus(forceCheck){
			if(currentApp.finished) {
				clearInterval(checkTaskInterval);
				if(!forceCheck) return;
			}
			//先检查是否已经安装，安装过才请求后台
			if(currentApp.installed){
				$('#detail-panel .install-app').hide();
				$('#detail-panel .manual-copy').hide();
				$('#detail-panel .copy-keyword').hide();
				$('#detail-panel .launch-app').show();

				forceCheck && showLoading();
				$.getJSON('/api/v1/task/check/' + currentApp.tid, function(data){
					forceCheck && hideLoading();
					if(data.status == 0){
						currentApp.finished = 1;
						showDialog('恭喜，任务“' + currentApp.keyword + '”已完成', '确定', function(){
							MiJSBridge.call('close');
						});
					}else if(forceCheck){
						showDialog(data.desc);
					}
				});
			}else {
				MiJSBridge.call('checkApp', {protocol: propid, bundle : bdid}, function(ret){
					if(ret.installed) {
						currentApp.installed = true;
						checkTaskStatus();
					}else if(forceCheck){
						showDialog('任务进行中');
					}
				});
			}
		}

		function doAbortTask(){
			showLoading();
			$.ajax({
				url: '/api/v1/task/abort/' + currentApp.tid,
				type: 'POST',
				dataType: 'json',
				success : function(data){
					hideLoading();
					if(data.code == 0){
						showDialog('放弃任务成功', '确定', function(){ MiJSBridge.call('close'); });
					}else {
						showDialog(data.message);
					}
				},
				error : function(error){
					hideLoading();
					showTips('网络错误，请稍后重试');
				}
			});
		}

    }


	function openUrl(url){
		if(typeof MiJSBridge=="object") {
			MiJSBridge.call("open", {url: url, showItem:true});
		} else {
			window.location.href= url;
		}
	}

	function showTips(message, callback) {
		var el=Zepto.tips({
			content:message,
			stayTime:2000,
			type:"info"
		})
		el.on("tips:hide",function(){
			callback && callback();
			console.log("tips hide");
		})
	}

	function openApp(aid, bid){
		MiJSBridge.call('launchApp', {protocol: aid || '', bundle: bid || ''});
	}

	function showDialog(message, title, callback){
		MiJSBridge.call('alert', {title: message}, function(){ callback && callback(); });
	}


	function showLoading(msg){
		$$loading = Zepto.loading({content:msg || '加载中'});
		clearTimeout(window.$$loadingTimeout);
		$$loadingTimeout = setTimeout(function(){window.$$loading && $$loading.loading('hide');}, 10000);
	}

	function hideLoading(){
		window.$$loading && $$loading.loading('hide');
	}

	if(page == 'list'){
		$.initTaskList();
	}else if(page == 'detail'){
		$.initTaskDetail();
	}
});