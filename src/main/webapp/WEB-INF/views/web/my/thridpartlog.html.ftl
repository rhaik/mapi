	<#import "/lib/util.ftl" as util>
<#include "/common/scroll_header.ftl">
<style>
	.ui-list-action{color:#666666;}
	.ui-list-action>span{color:#ff8003;}
	.ui-list-date{line-height:26px;background-color:#f0eff5;} 
	.ui-list-info>h4 {margin:12px 0px;}
	.ui-list-date span{font-size:14px;margin-left:14px;color:#777777}
</style>
	<ul class="ui-list ui-border-tb" id="fpmxList" data-role="listview">
	<#list thridpartlog as log>
		<#if log.displayDate>
		<li class="ui-list-date" style="margin-left:0px;" data-date="${log.createtime?string("yyyy年MM月dd日")}">
			<span>${log.createtime?string("yyyy年MM月dd日")}</span>
		</li>
		</#if>
	    <li <#if !log.displayDate>class="ui-border-t"</#if>>
	        <div class="ui-list-info">
	            <h4>${log.ad_name!''}</h4> 
	        </div>
	        <div class="ui-list-action"><span>${log.points!0}</span> ${integralType!''}</div>
	    </li>
	</#list>    
	</ul>
<script>
$(function(){
	$(document).bind("pageinit", function() {
		isInit=0;
		serverURL = "${util.ctx}<#if fromSafari??>/ios/wxapi/<#else>/api/v1</#if>/user/thridpartlog?source=${source!0}"; //服务器地址
		if(location.search)
			serverURL = serverURL+location.search;
		startNum = 0;			//当前页
		count = ${totalPage!0}; //总页数
		if(count <=1 ) $('#pullUp').hide();
		console.log("开始数：" + startNum + ',总页数:' + count);
		//回调处理
		callbackReviceData = function(datas){
			if(!datas|| datas.data.length == 0){
				console.info("not data");
				return ;
			}
			var result = '';
		    for (var i in datas.data) {
		    	var existDate = datas.data[i].currentDate == $('li[data-date]:last').attr('data-date');
		    	var className = 'class="ui-border-t"';
		    	if(!existDate && datas.data[i].displayDate) {
			    	result += '<li class="ui-list-date" style="margin-left:0px;" data-date="' + datas.data[i].currentDate + '">'
					+ '<span>' + datas.data[i].currentDate + '</span>'
					+ '</li>';
					
					className = '';
				}
		        result += '<li '+ className +'>' 
	        				+ '<div class="ui-list-info">' 
	        					+ '<h4>'+ datas.data[i].name+'</h4>'
						      +'</div>'
						      +'<div class="ui-list-action"><span>'+ datas.data[i].points + '</span> 金币</div>'
						+'</li>';
		    }
		    return result;
		}
	});
 });
 </script>
<#include "/common/scroll_footer.ftl">