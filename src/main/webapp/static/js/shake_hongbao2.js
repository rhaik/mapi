$(function(){
	var threshold = 10;
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
			if(times > 1){ //shake happen
				$(window).trigger($.Event('shaked'));
				times = 0;
			}
		};
		lastX = x;
		lastY = y;
	},false);
});
$(function(){
	var sysNum = 0, userNum = 0;
	var remainingNumTimer = null;
	var shaking = false; //默认弹窗状态，不能摇

	$(window).on('shakestart', function(){
	});

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
				if(sysNum > 0){
					grabHongbao(0);
				}else if(userNum > 0){
					grabHongbao(1);
				}else{
					onHongbaoGrabbed({});
				}
			}, 1000);
		}
	});

	$('#task-tishi').click(function(){
	    location.href = '/ios/tasks.html';
	});

	$('#goto-share,#hongbao-mine').click(function(){
		$('.get_shake_bg').hide();
		shaking = false;
		location.href = '/ios/share.html';
	});

	$('.get_shake_bg i,#hongbao-result .continue').click(function(){
		$(this).parents('.get_shake_bg').hide();
		if($(this).data('shaked')) shaking = false;
	});

	$('header a.right_btn').hide();

	$('#hongbao-content .open_shake').click(function(){
		var $this = $(this);
		$this.addClass('xuanzhuan');
		var cont = $('#hongbao-content');
		setTimeout(function(){
			cont.fadeOut(400);
			$this.removeClass('xuanzhuan');
			var type = cont.attr('type'), amount = cont.attr('amount');
			if(type && amount){
				$('#hongbao-amount').html(amount+ '<b>'+type+'</b>' );
				$('#hongbao-info').text('已存入您的' + type + '账户，注意查收');
				$('#hongbao-result').fadeIn();
				getRemainingNum();
			}
		}, 1200);
	});

	$('#activity-rule').click(function(){$('#hongbao-desc').show();});
	$('#share-menu li').click(function(){
		$('#share-menu').removeAttr('opened').hide();
		if($(this).hasClass('toShare')){
			location.href = '/ios/share.html';
		}else{
			$('#hongbao-desc').show();
		}
	});

	$('body').on('click', function(event){
		if($('#share-menu').attr('opened')){
			if($(event.target).is('#share-menu') || $(event.target).parents('#share-menu').size()){
				//clicked on menu
			}else {
				$('#share-menu').removeAttr('opened').hide();
				return false;
			}
		}
	});

	function playShake(){
		var audio = new Audio('/static/music/red-01.mp3');
		audio.play();
	}

	function playHongbao(){
		var audio = new Audio('/static/music/red-02.mp3');
		audio.play();
	}
	var isFirst = 0;
	function getRemainingNum(){
		clearTimeout(remainingNumTimer);
		$.ajax({
                type: "GET",
                url: '/ios/api/hongbao/number',
                dataType: 'json',
                success: function(resp) {
                	clearTimeout(remainingNumTimer);
                	sysNum = parseInt(resp.sysNum) || 0;
                	userNum = parseInt(resp.userNum) || 0;
					if(resp.sysNum > 0){
						$('#hongbao-mine').hide();
						$('#start-hint').hide();
						var numArray = sysNum.toString().split('');
						var numHtml = numArray.length < 5? $(new Array(5 - numArray.length)).map(function(){ return '<i>0</i>';}).get().join(' ')  : '';
						numHtml += $(numArray).map(function(){ return '<i>' + this + '</i>';}).get().join(' ') ;
						$('#remain-hongbao').show().html('<em>还剩</em>' + numHtml + '<em>个红包</em>')

						$('#lucky-users').show();
						$('#bottom-info').hide();
						remainingNumTimer = setTimeout(getRemainingNum, 10000);
					}else {
						$('#remain-hongbao').hide();
						remainingNumTimer = setTimeout(getRemainingNum, 30000);

						$('#hongbao-mine').show().text('您有' + userNum + '个红包可以摇一摇');
					}
					getHongbaoList();
                },
                error : function(){
                	remainingNumTimer = setTimeout(getRemainingNum, 10000);
                }
		});
	}
	function noHongbaoShow(){
		$('#hongbao-content i').show();
		var $content = $('#hongbao-content');
		$content.find('.noHongbao').show();
		$content.find('.hasHongbao').hide();
		$content.attr('data-amount', 0).attr('data-type', 0).show();
	}
	function grabHongbao(type){
		$.ajax({
			type: "POST",
			url: '/ios/api/activity/draw',
			dataType: 'json',
			success: onHongbaoGrabbed,
			error: function(){alert('网络不给力，请您稍后再试'); $('#shake-annim').removeClass('swing'); shaking = false;}
		});
	}

	function onHongbaoGrabbed(resp){
		$('#shake-annim').removeClass('swing');
		if(resp.code){
			$('#hongbao-content i').show();
			var $content = $('#hongbao-content');
			$content.find('.noHongbao').show().find('h3').text(resp.message);
			$content.find('.hasHongbao').hide();
			$content.attr('data-amount', 0).attr('data-type', 0).show();
		}else if(resp.data && resp.data.amount > 0){
			playHongbao();
			$('#hongbao-content i').hide();
			$('#hongbao-content').attr({'amount': resp.data.amount, 'type': resp.data.drawType}).find('.noHongbao').hide().end().find('.hasHongbao').show().end().show();
		}else {
//			$('#hongbao-content i').show();
//			var $content = $('#hongbao-content');
//			$content.find('.noHongbao').show().find('h3').text('啊哦，红包已经抢完了');
//			$content.find('.hasHongbao').hide();
//			$content.attr('data-amount', 0).attr('data-type', 0).show();
			noHongbaoShow();
		}
	}

	function getHongbaoList(){
		$.ajax({
			type: "GET",
			url: '/ios/api/hongbao/list',
			dataType: 'json',
			success: function(resp){
				if(resp.data){
					var html = $(resp.data).map(function(){
						return '<tr><td width="40%">' + this.uniName + '</td><td width="30%">摇到' + this.hongbao + '</td><td width="30%">' + this.time+ '</td></tr>';
					}).get().join(' ');
					$('#hongbao-list').html(html);
				}
			}
		});
	}

	function setCookie(cname, cvalue, exdays) {
		var d = new Date();
		d.setTime(d.getTime() + (exdays*24*60*60*1000));
		var expires = "expires="+d.toUTCString();
		document.cookie = cname + "=" + cvalue + "; " + expires + '; path=/';
	}

	getRemainingNum();

	if(document.cookie.indexOf('hide_hongbao_desc') < 0){
		shaking = true;
		$('#hongbao-desc').show();
		setCookie('hide_hongbao_desc', 1, 1);
	}

});