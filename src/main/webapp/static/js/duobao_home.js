$(function(){
	var lotteryTimeout, announcingTimeout;
	var announcedTimeout = {};
	var swiper = new Swiper('.swiper-container', {
		pagination: '.swiper-pagination',
		paginationClickable: true,
		loop : true,
		autoplay: 2500,
		autoplayDisableOnInteraction: false
	});

	$('.swiper-container').on('click', '.swiper-slide', function(){
		var href = $(this).data('href');
		href && toUrl(href);
	});
	$('.nav_list').click(function(){
		var href = $(this).data('href');
		href && toUrl(href);
	});
	$('#lottery-popup .close').click(function(){
		var lotteryTime = parseInt($('#lottery-popup').data('time')) || new Date().getTime()
		$.cookie('last_lottery_time', lotteryTime, 180 * 24 * 3600);
		$('#lottery-popup').addClass('dn');
	});

	$('#lottery-notice').click(function(){
		if($(this).data('actid')){
			toUrl('/doubao/product/' + $(this).data('actid') + '.html');
		}
	});

	$.onPageShowAgain = function(event){
		!$.cookie('refreshedDuobaoHome') && location.reload();
	};
	function getLatestLottery(){
		clearTimeout(lotteryTimeout);
		$.ajax({
			type: "GET",
			url: '/doubao/api/common/latestLottery',
			dataType: 'json',
			data: { r : Math.random()},
			success: function(resp) {
				if(resp.lottery){
					var lottery = resp.lottery;
					$('#lottery-notice').attr('data-actid', lottery.id).show().find('#lottery-msg').html('恭喜<i>' + lottery.uniName + '</i>' + lottery.lottery_time + '获得' + lottery.product_name);
					$('#lottery-msg').hide().fadeIn();
					checkMyLottery(lottery);
				}
			}
		});
		lotteryTimeout = setTimeout(getLatestLottery, 10000);
	}

	function getLatestAnnouncing(){
		clearTimeout(announcingTimeout);
		$.ajax({
			type: "GET",
			url: '/doubao/api/common/annoucingList',
			dataType: 'json',
			data: { r : Math.random()},
			success: function(resp) {
				var len = $('#announce-list').find('.goods').not('.leastRemain').size();
				if(resp.data && resp.data.length){
					for(var i = 0, size = resp.data.length; i < size && len < 2; ++ i) {
						var announce = resp.data[i];
						if(!$('#announce-pid-' + announce.id).size()){
							len += 1;
							$('#announce-list').find('.goods').size() == 2 && $('#announce-list').find('.goods.leastRemain').last().remove();
							doAddAnnouncing(announce);
						}
					}
					$('#announce-list').slideDown();
				}else if(!len){
					$('#announce-list').hide();
				}
				clearTimeout(announcingTimeout);
				len = $('#announce-list').find('.goods').not('.leastRemain').size();
				len < 2 && (announcingTimeout = setTimeout(getLatestAnnouncing, 20000));
			}
		});

		announcingTimeout = setTimeout(getLatestAnnouncing, 20000);
	}

	function doAddAnnouncing(announce){
		var html = handlebars['announce-template'](announce);
		$('#announce-list').append(html);
		$('#announce-pid-' + announce.id).find('.countdown').countDown(function(){
			$('#announce-pid-' + announce.id).find('div.times').replaceWith('<div class="lottery"><i>等待开奖结果</i><b></b></div>');
			$('#announce-pid-' + announce.id).find('div.text_tishi').text('正在揭晓');
			announcedTimeout['announce' + announce.id] = setTimeout(function(){ checkAnnounce(announce.id);}, 1000);
		});
	}

	function checkAnnounce(pid){
		$.ajax({
		   type: "GET",
		   url: '/doubao/api/common/announced/' + pid,
		   dataType:'json',
		   success:function(r){
		   		if(r.code==0 && r.product) {
					$('#announce-pid-' + pid).find('div.lottery').html('<i>中奖人：' + r.product.uniName + '</i>');
					$('#announce-pid-' + pid).find('div.text_tishi').text('揭晓成功');

					$('#announce-pid-' + pid).attr('data-lottery', 1);
					clearTimeout(announcedTimeout['announce' + pid]);
					setTimeout(function(){ $('#announce-pid-' + pid).remove(); getLatestAnnouncing();}, 8000);
		   		}
		   }
		});
		!$('#announce-pid-' + pid).data('lottery') && (announcedTimeout['announce' + pid] = setTimeout(function(){ checkAnnounce(pid);}, 20000));
	}


	function checkMyLottery(lottery){
        if(lottery.is_mine){
            var lastLotteryTime = parseInt($.cookie('last_lottery_time')) || 0;
            var lotteryTime = parseInt(lottery.lottery_timestamp);
            $('#lottery-popup').attr('data-time', lotteryTime);

            if((lastLotteryTime > 0 && lotteryTime > lastLotteryTime) || (lastLotteryTime == 0 && (lotteryTime - new Date().getTime()) < 3600 * 1000) ){
                $('#lottery-popup').find('p').text('您参与的第' + lottery.product_number + '期“' + lottery.product_name + '”');
                $('#lottery-popup').removeClass('dn').find('a').off('click').on('click',function(){
                    $.cookie('last_lottery_time', lotteryTime, 180 * 24 * 3600);
                    $('#lottery-popup').addClass('dn');
                    toUrl('/doubao/my/lotterydetail/pa-' + lottery.id + '.html');
                });
            }
        }
	}

	getLatestLottery();
	getLatestAnnouncing();


	$.cookie('hd_db_note') || $.showDialog('公告', '亲~秒赚大钱为保证商品的时效性，对每次夺宝设定了三个月的有效期，5月17日为iPhone 6s Plus（一期）满三个月，系统自动设置为过期，并已返还所有参与用户的夺宝币。对此秒赚大钱表示抱歉，我们会陆续更新更多更好的商品，希望大家积极参与。', {sureCallback: function(){
		$.hideDialog();
		$.cookie('hd_db_note', 1, 7 * 86400);
	}});

	$.cookie('refreshedDuobaoHome', 1, 15);
});