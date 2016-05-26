<#import "/lib/util.ftl" as util>
<#include "/common/scroll_header.ftl">
<style>
	.ui-list-info>span{line-height:30px;font-size:14px;margin:20px 0px;}
	.ui-list-date{line-height:26px;background-color:#f0eff5;}
	.ui-list-date span{font-size:14px;margin-left:14px;color:#777777;}
	.ui-list-info>span{font-size:16px;}
	.ui-list-right{position:relative;font-size:12px;margin:10px 0px;margin-right:36px;line-height: 16px;text-align: right;}
	.ui-list-right .amount{text-align:right;color:#666666}
	.status-5{color:green;}
	.status-4,.status-6{color:red;}
	.status-1,.status-2,.status-3{color:blue;}
</style>
<ul class="ui-list ui-border-tb" id="fpmxList" data-role="listview">
	<#list userEnchashments as u>
		<#if u.displayDate>
			<li class="ui-list-date" style="margin-left:0px;" data-date="${u.userEnchashment.mention_time?string("yyyy年MM月dd日")}">
				<span>${u.userEnchashment.mention_time?string("yyyy年MM月dd日")}</span>
			</li>
		</#if>
		<li class="ui-form-item-link <#if !u.displayDate>ui-border-t</#if>" onclick="userEnchashmentDetail(${u.userEnchashment.id});">
			<div class="ui-list-info">
				<span>${u.userEnchashment.typeText}</span>
			</div>
			<div class="ui-list-right">
				<p class="amount">${util.fen2yuanS(u.userEnchashment.amount!0)}元</p>
				<span class="status-${u.userEnchashment.status}">${u.userEnchashment.statusText}</span>
			</div>
		</li>
	</#list>    
</ul>
<script>
$(function(){
 	userEnchashmentDetail = function(id){
 		var url = '${util.ctx}/web/my/enchashments/' + id;
 		if(typeof MiJSBridge=="object") {
 			MiJSBridge.call("open", {url: url});
 		} else {
 			window.location.href= url;
 		}
 	}
 	//回调处理
	$(document).bind("pageinit", function() {
		isInit=0;
		serverURL = "${util.ctx}/api/v1/enchashment/list"; //服务器地址
		startNum = 0;//当前页
		count = ${totalPage!0}; //总页数
		if(count <=1 )	$('#pullUp').hide();
		callbackReviceData = function(datas){
			var result = '';
		    for (var i = 0; i < datas.data.length; i++) {
		    	var existDate = datas.data[i].mention_time == $('li[data-date]:last').attr('data-date');
		    	var className = 'ui-border-t';
		    	if(!existDate && datas.data[i].displayDate) {
			    	result += '<li class="ui-list-date" style="margin-left:0px;" data-date="' + datas.data[i].mention_time + '">'
					+ '<span>' + datas.data[i].mention_time + '</span>'
					+ '</li>';
					
					className = '';
				}
		        result += '<li class="ui-form-item-link '+ className +'" onclick="userEnchashmentDetail('+ datas.data[i].id +')">' 
		        	+ '<div class="ui-list-info">' 
		        		+'<span>'+datas.data[i].typeText+'</span>'
        			+ '</div>' 
        			+ '<div class="ui-list-right">'
						+'<p class="amount">' + datas.data[i].amount + '元</p>'
						+'<span class="status-'+datas.data[i].status+'">'+datas.data[i].statusText+'</span>'
					+'</div>';
		    }
		    return result;
		}
	});
 });
 </script>
<#include "/common/scroll_footer.ftl">