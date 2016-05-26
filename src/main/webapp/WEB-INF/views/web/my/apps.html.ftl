	<#import "/lib/util.ftl" as util>
<#include "/common/scroll_header.ftl">
<style>
	.ui-list-action{color:#666666;}
	.ui-list-date{line-height:26px;background-color:#f0eff5;}
	.ui-list-thumb{width:45px;height:45px;margin:10px 10px 10px 0px;}
	.ui-list-info>h4 {font-size:16px;}
	.ui-list-date span{font-size:14px;margin-left:14px;color:#777777}
</style>
	<ul class="ui-list ui-border-tb" id="fpmxList" data-role="listview">
	<#list usertasks as task>
		<#if task.displayDate>
		<li class="ui-list-date" style="margin-left:0px;" data-date="${task.currentDate}">
			<span>${task.userTask.starttime?string("yyyy年MM月dd日")}</span>
		</li>
		</#if>
	    <li <#if !task.displayDate>class="ui-border-t"</#if>>
	        <div class="ui-list-thumb">
	            <span style="background-image:url('${task.app.icon!''}');border-radius:10px;"></span>
	        </div>
	        <div class="ui-list-info">
	            <h4>${task.appTask.name}</h4> 
	        </div>
	        <div class="ui-list-action">${util.fen2yuanS(task.earned_amount!0)} 元</div>
	    </li>
	</#list>    
	</ul>
<script>
$(function(){
	$(document).bind("pageinit", function() {
		isInit=0;
		serverURL = "${util.ctx}/api/v1/task/logs"; //服务器地址
		if(location.search)
			serverURL = serverURL+location.search;
		startNum = 0;			//当前页
		count = ${totalPage!0}; //总页数
		if(count <=1 ) $('#pullUp').hide();
		console.log("开始数：" + startNum + ',总页数:' + count);
		//回调处理
		callbackReviceData = function(datas){
			var result = '';
		    for (var i = 0; i < datas.data.length; i++) {
		    	var existDate = datas.data[i].currentDate == $('li[data-date]:last').attr('data-date');
		    	var className = 'class="ui-border-t"';
		    	if(!existDate && datas.data[i].displayDate) {
			    	result += '<li class="ui-list-date" style="margin-left:0px;" data-date="' + datas.data[i].currentDate + '">'
					+ '<span>' + datas.data[i].date + '</span>'
					+ '</li>';
					
					className = '';
				}
		        result += '<li '+ className +'>' 
			        		+ '<div class="ui-list-thumb">' 
			        			+ '<span style="background-image:url('+ datas.data[i].icon + ');border-radius:10px;"></span>' 
			        		+ '</div>'
	        				+ '<div class="ui-list-info">' 
	        					+ '<h4>'+ datas.data[i].name+'</h4>'
						      +'</div>'
						      +'<div class="ui-list-action">'+ datas.data[i].amount + ' 元</div>'
						+'</li>';
		    }
		    return result;
		}
	});
 });
 </script>
<#include "/common/scroll_footer.ftl">