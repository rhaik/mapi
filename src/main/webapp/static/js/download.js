$(function(){
	$('.bg2').hide();
	$('.step1').bind('click',function(){
		$(this).hide();
		$(".step1_finish").removeClass("dn");
		$('.bg2').show();
    	$(".last_btn").hide();
		setTimeout(function () {
	        $('.bg2').hide();
	    }, 10000);
	})
	
	$('.main b').on('click',function(){
    	$('.bg2').hide();
    })
    $('.step2').click(function(){
    	$(".main_tit").hide();
    	$(".main_finish").show();
    	$(".last_btn").show();
    	$('.bg2').show();
    })
    function isSafari9(){
		var u = navigator.userAgent;
		return u.indexOf('Safari') > -1 && u.indexOf('Version/9.') > -1;
	}
	function isIos(){
		var u = navigator.userAgent;
		return u.indexOf('iPhone') > -1 || u.indexOf('iPad') > -1;
	}
})