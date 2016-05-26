<#import "/lib/util.ftl" as util>
<#include "/common/scroll_header.ftl">
<style>
	.ui-tab{background-color:#fff;}
	.ui-list{margin-bottom:0px;}
	.ui-tab-nav li{font-size:16px;}
	.ui-tab-nav li.current {color: #fff;border:1px #0080fd solid;background-color:#0080fd}
	.ui-list>.ui-form-item-link{position: relative;display: -webkit-box; }
	.ui-form-item-link{margin-left:20px;}
	.ui-list-info>h4 {color:#000;width: 120px;overflow: hidden;white-space: nowrap;line-height: 24px;}
	.ui-list-info span{width:20px;height:20px;background-size:100%;background-repeat:no-repeat;display:inline-block;}
	.ui-list-date{line-height:26px;background-color:#f0eff5;}
	.ui-list-date span{margin-left:14px;color:#777777;font-size:14px;}
	.ui-list-thumb{width:45px;height:45px;margin: 10px 10px 10px 0;}
	.ui-list-action {margin-right:16px;color:#666666}
	.nodata{margin:0 auto;text-align:center;margin:16px 0px;font-size:16px;}
	<#if showHeader>
		.ui-tab-nav {top:44px;}
		#wrapper {margin-top:90px;}
	<#else>
		.ui-content {margin-top:45px;}
	</#if>
</style>
<div class="main"  id="fpmxList" data-role="listview">
<#if userIncome?? && (userIncome?size >0)>
<div class="ui-list ui-border-tb">
<#list userIncome as user>
	<#if user.displayDate>
		<div class="ui-list-date ui-border-tb" style="margin-left:0px;" data-date="${user.userIncomeLog.operator_time?string("yyyy年MM月dd日")}">
		<span>${user.userIncomeLog.operator_time?string("yyyy年MM月dd日")}</span>
	</div>
	</#if>
	<div class="ui-form-item-link<#if !user.displayDate> ui-border-tb</#if>" onclick="incomeDetail(${user.userIncomeLog.from_user})">
		<div class="ui-list-thumb">
			<span style="background-image:url(${user.fromUser.headImg!''});border-radius:10px;"></span>
		</div>
		<div class="ui-list-info">
			<h4>${user.fromUser.uniName!''}</h4>
			<div class="ui-list-action">￥${user.userIncomeLog.amountYuan}</div>
		</div>
	</div>
	</#list>
</div>
<#else>
	<div class="nodata">无数据记录</div>
</#if>
</div>

<script>
<#if showHeader>
	$('#wrapper').before('<ul class="ui-tab-nav ui-border-b">' +
        '<li <#if (income.share_level1_total>0)>class="current"</#if> id="first">徒弟收入(${income.shareLevel1TotalYuan!0}元)</li>' +
        '<li <#if (income.share_level1_total<=0)>class="current"</#if> id="second">徒孙收入(${income.shareLevel2TotalYuan!0}元)</li>' +
    	'</ul>');
	<#else>
	$('#fpmxListPage').prepend('<ul class="ui-tab-nav ui-border-b" data-role="header">' +
        '<li <#if (income.share_level1_total>0)>class="current"</#if> id="first">徒弟收入(${income.shareLevel1TotalYuan!0}元)</li>' +
        '<li <#if (income.share_level1_total<=0)>class="current"</#if> id="second">徒孙收入(${income.shareLevel2TotalYuan!0}元)</li>' +
    	'</ul>');
	</#if>
$(function(){
	var firstData = $('#fpmxList').html(),secondData ="";
	$("body").bind('swiperight', function() {
		if($('#first').attr('class') == "current" ) return ;
		$('.ui-tab-nav>li').removeClass("current").first().addClass("current");
	  	first();
	  	 
	}).bind('swipeleft', function() {
		if($('#second').attr('class') == "current" ) return ;
	  	$('.ui-tab-nav>li').removeClass("current").last().addClass("current");
	   	second();
	});
	$('.ui-tab-nav>li').click(function(){
		if($(this).attr('class') == "current" ) return ;
		$('.ui-tab-nav>li').removeClass("current");
		$(this).addClass("current");
		$(this).attr('id')== "first" ? first() : second();
	});
	function first() {
	 	serverURL = "${util.ctx}<#if fromSafari??>/ios/wxapi<#else>/weixin/api</#if>/inviteincome?friend_level=1"; //服务器地址
	  	isDisplayLoad = false;
	  	
	  	secondData = $('#fpmxList').html();
	  	$('#fpmxList').html(firstData);
	}
	function second() {
	 	serverURL = "${util.ctx}<#if fromSafari??>/ios/wxapi<#else>/weixin/api</#if>/inviteincome?friend_level=2"; //服务器地址
	   	isDisplayLoad = false;
	   
	    firstData = $('#fpmxList').html();
	   	if(secondData!="") {
	   		$('#fpmxList').html(secondData);
	   	} else {
	   		showLoading();
	   		$('#pullUp').hide();
	   		isDisplayLoad = true;
	  		pullDownAction();
  		}
	}
	incomeDetail = function(id){
		var url = '${util.ctx}<#if fromSafari??>/ios<#else>/weixin</#if>/my/inviteincome/' + id;
 		if(typeof MiJSBridge=="object") {
 			MiJSBridge.call("open", {url: url});
 		} else {
 			window.location.href= url;
 		}
	}
    isDisplayLoad = false;
	isInit=0;
	serverURL = "${util.ctx}<#if fromSafari??>/ios/wxapi<#else>/weixin/api</#if>/inviteincome?friend_level=1"; //服务器地址
	startNum = 0;			//当前页
	count = ${totalPage!0}; //总页数
	if(count <=1 ) $('#pullUp').hide();
	console.log("开始数：" + startNum + ',总页数:' + count);
	
	//回调处理
	callbackReviceData = function(datas){
		var result = '<div class="ui-list ui-border-tb">';
		if(datas.data.length > 0) {
		    for (var i = 0; i < datas.data.length; i++) {
		    	var existDate = datas.data[i].currentDate == $('li[data-date]:last').attr('data-date');
		    	var className = ' ui-border-tb';
		    	if(!existDate && datas.data[i].displayDate) {
			    	result += '<div class="ui-list-date ui-border-tb" style="margin-left:0px;" data-date="' + datas.data[i].currentDate + '">'
					+ '<span>' + datas.data[i].currentDate + '</span>'
					+ '</div>';
					
					className = '';
				}
		        result += '<div class="ui-form-item-link'+ className +'" onclick="incomeDetail('+datas.data[i].from_user+')">' 
		        		 	+'<div class="ui-list-thumb">'
					         +'<span style="background-image:url(' + datas.data[i].headImg + ');border-radius:10px;"></span>'
					        +'</div>'
	        				+ '<div class="ui-list-info">' 
	        					+ '<h4>'+ datas.data[i].name+'</h4>'
	        					+ '<div class="ui-list-action">￥' + datas.data[i].amount + '</div>'
						      +'</div>'
						+'</div>';
		    }
		    result += '</div>';
	    } else {
	    	result = '<div style="margin:0 auto;text-align:center;margin:16px 0px;font-size:16px;">无数据记录</div>';
	    }
	    return result;
	}
	var share_level1_total  = ${income.share_level1_total!0};
	if(share_level1_total > 0) {
		$('#first').click();
	} else {
		$('#second').click();
	}
	
});
</script>
<#include "/weixin/wx_share.ftl">
<#include "/common/scroll_footer.ftl">