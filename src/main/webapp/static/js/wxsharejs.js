function wx_share_out(shareTitle,imgUrl,descContent) {
	var appid = '';
	var lineLink = window.location.href;
	if(imgUrl=='' || imgUrl=='0' || imgUrl=='null') {
		var imgs = document.getElementsByTagName("img");
		if(imgs.length>0) {
			var urlm = /http:\/\//i;
			imgUrl = imgs[0].src;
			if(urlm.test(imgUrl)) {} else {
				imgUrl = 'http://'+window.location.host+imgUrl;
			}
		}
	}
	
	function shareFriend() {
		WeixinJSBridge.invoke('sendAppMessage',{
			"appid": appid,
			"img_url": imgUrl,
			"img_width": "200",
			"img_height": "200",
			"link": lineLink,
			"desc": descContent,
			"title": shareTitle
		}, function(res) {
		})	
	}
	function shareTimeline() {
		WeixinJSBridge.invoke('shareTimeline',{
			"img_url": imgUrl,
			"img_width": "200",
			"img_height": "200",
			"link": lineLink,
			"desc": descContent,
			"title": shareTitle
		}, function(res) {
		});
	}
	function shareWeibo() {
		WeixinJSBridge.invoke('shareWeibo',{
			"content": descContent,
			"url": lineLink,
		}, function(res) {
		});
	}
	document.addEventListener('WeixinJSBridgeReady', function onBridgeReady() {
			WeixinJSBridge.on('menu:share:appmessage', function(argv){
				shareFriend();
			});
			WeixinJSBridge.on('menu:share:timeline', function(argv){
				shareTimeline();
			});
			WeixinJSBridge.on('menu:share:weibo', function(argv){
				shareWeibo();
			});
	}, false);
}

window.onload=function(){
	var id = document.getElementById("loading");
    setTimeout(function(){document.body.removeChild(id)},1000);
    if($("#audio_btn").attr("url").indexOf("mp3")>1){
	  var url = $("#audio_btn").attr("url"); 
	  var auto = is_open=='on' ? 'autoplay' : '';
	  var html = '<audio loop  src="'+url+'" id="media" '+auto+' ></audio>';
	  setTimeout(function(){
		  $("#audio_btn").html(html);
		  $("#audio_btn").show().attr("class",is_open);
	 },500);
	  
	  $("#audio_btn").on('touchstart',function(){
		  var type = $("#audio_btn").attr("class");
		  var media = $("#media").get(0);
		  if(type=="on"){
		    media.pause(); 
			$("#audio_btn").attr("class","off");
		  }else{
			media.play();
			$("#audio_btn").attr("class","on"); 
	      }  
	  })
    }
	
} 

var Msize = $(".m-page").size(), 	
	page_n			= 1,			
	initP			= null,			
	moveP			= null,			
	firstP			= null,			
	newM			= null,			
	p_b				= null,			
	indexP			= null, 		
	move			= null,			
	start			= true, 		
	startM			= null,			
	position		= null,			
	DNmove			= false,		
	mapS			= null,			
	canmove			= false,		
	
	textNode		= [],			
	winHeight       = $(window).height(),
	textInt			= 1;			
	

	

	var v_h	= null;		
	
	function init_pageH(){
		var fn_h = function() {
			if(document.compatMode == "BackCompat")
				var Node = document.body;
			else
				var Node = document.documentElement;
			 return Math.max(Node.scrollHeight,Node.clientHeight);
		}
		var page_h = fn_h();
		var m_h = $(".m-page").height();
		page_h >= m_h ? v_h = page_h : v_h = m_h ;
		
		
		$(".m-page").height(v_h); 	
		$(".p-index").height(v_h);
		
	};
	init_pageH();
	function changeOpen(e){
		$(".m-page").on('mousedown touchstart',page_touchstart);
		$(".m-page").on('mousemove touchmove',page_touchmove);
		$(".m-page").on('mouseup touchend mouseout',page_touchend);

	};
	
	
	function changeClose(e){
		$(".m-page").off('mousedown touchstart');
		$(".m-page").off('mousemove touchmove');
		$(".m-page").off('mouseup touchend mouseout');

	};
	
	
	changeOpen();
	
	
	function page_touchstart(e){
		if (e.type == "touchstart") {
			initP = window.event.touches[0].pageY;
		} else {
			initP = e.y || e.pageY;
			mousedown = true;
		}
		firstP = initP;	
	};
	
	
	function V_start(val){
		initP = val;
		mousedown = true;
		firstP = initP;		
	};
	
	
	function page_touchmove(e){
		e.preventDefault();
		e.stopPropagation();	
        var imgs = $(".m-img").length;

		
		if(start||startM){
			startM = true;
			if (e.type == "touchmove") {
				moveP = window.event.touches[0].pageY;
			} else { 
				if(mousedown) moveP = e.y || e.pageY;
			}
			page_n == 1 ? indexP = false : indexP = true ;	
		}
		
		
		if(moveP&&startM&&imgs>1){
			
			
			if(!p_b){
				p_b = true;
				position = moveP - initP > 0 ? true : false;	
				if(position){
				
					if(indexP){								
						newM = page_n - 1 ;
						$(".m-page").eq(newM-1).addClass("active").css("top",-v_h);
						move = true ;
					}else{
						if(canmove){
							move = true;
							newM = Msize;
							$(".m-page").eq(newM-1).addClass("active").css("top",-v_h);
						}
						else move = false;
					}
							
				}else{
				
					if(page_n != Msize){
						if(!indexP) $('.audio_txt').addClass('close');
						newM = page_n + 1 ;
					}else{
						newM = 1 ;
					}
					$(".m-page").eq(newM-1).addClass("active").css("top",v_h);
					move = true ;
				} 
			}
			
			if(!DNmove){
				if(move){	
					start = false;
					var topV = parseInt($(".m-page").eq(newM-1).css("top"));
					$(".m-page").eq(newM-1).css({'top':topV+moveP-initP});	
					
				    if(topV+moveP-initP>0){
					   var bn1 = winHeight-(topV+moveP-initP);
					   var bn2 = ((winHeight-bn1/4)/winHeight);
                       $(".m-page").eq(newM-2).attr("style","-webkit-transform:translate(0px,-"+bn1/4+"px) scale("+bn2+")");
				    }else{
					   var bn3 = winHeight+(topV+moveP-initP);
					   var bn4 = ((winHeight-bn3/4)/winHeight);
					   if(Msize!=newM){
                         $(".m-page").eq(newM).attr("style","-webkit-transform:translate(0px,"+bn3/4+"px) scale("+bn4+")");
					   }else{
						 $(".m-page").eq(0).attr("style","-webkit-transform:translate(0px,"+bn3/4+"px) scale("+bn4+")");  	
					   }  
				    }
					initP = moveP;
				}else{
					moveP = null;	
				}
			}else{
				console.log('2')
				moveP = null;	
			}
		}
	};
	function page_touchend(e){	
		
		startM =null;
		p_b = false;
		var move_p;	
		position ? move_p = moveP - firstP > 100 : move_p = firstP - moveP > 100 ;
		if(move){
			
			if( move_p && Math.abs(moveP) >5 ){	
				$(".m-page").eq(newM-1).animate({'top':0},300,"easeOutSine",function(){
					success();
					$(".m-page").attr("style","");
				})
			
			}else if (Math.abs(moveP) >=5){	
				position ? $(".m-page").eq(newM-1).animate({'top':-v_h},100,"easeOutSine") : $(".m-page").eq(newM-1).animate({'top':v_h},100,"easeOutSine");
				$(".m-page").attr("style","");
				$(".m-page").eq(newM-1).removeClass("active");
				start = true;
				$(".m-page").attr("style","");
			}
		}
		initP		= null,			
		moveP		= null,			
		firstP		= null,			
		mousedown	= null;			
	};
	function success(){
		
		$(".m-page").eq(page_n-1).removeClass("show active").addClass("hide");
		$(".m-page").eq(newM-1).removeClass("active hide").addClass("show");
		
		
		
		page_n = newM;
		start = true;
		
		
		if(page_n == Msize) {
			canmove = true;
			$('.u-arrow').hide();
		}else{
			$('.u-arrow').show();
		}
		
	}

	$(function(){
		var bd = $(document.body);
		window.addEventListener('onorientationchange' in window ? 'orientationchange' : 'resize', _orientationchange, false);
		function _orientationchange() {
			scrollTo(0, 1);
			switch(window.orientation){
				case 0:		
					bd.addClass("landscape").removeClass("portrait");
					init_pageH();					
					break;
				case 180:	
					bd.addClass("landscape").removeClass("portrait");	
					init_pageH();
					break;
				case -90: 	
					init_pageH();
					break;
				case 90: 	
					init_pageH();
					bd.addClass("portrait").removeClass("landscape");
					break;
			}
		}
		$(window).on('load',_orientationchange);
	});

	var input_focus = false;
	function initPage(){
		
		$(".m-page").addClass("hide").eq(page_n-1).addClass("show").removeClass("hide");
		
		$(document.body).find("img").on("mousedown",function(e){
			e.preventDefault();
		})	
		
		if(RegExp("iPhone").test(navigator.userAgent)||RegExp("iPod").test(navigator.userAgent)||RegExp("iPad").test(navigator.userAgent)) $('.m-page').css('height','101%');
	}(initPage());
