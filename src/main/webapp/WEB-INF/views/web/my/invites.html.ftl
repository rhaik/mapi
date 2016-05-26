<#import "/lib/util.ftl" as util>
<#include "/common/scroll_header.ftl">
<style>
	.ui-list-thumb{width:45px;height:45px;margin: 10px 10px 10px 5px;}
	.ui-list-info h4{font-size: 16px;width: 120px;overflow: hidden;white-space: nowrap;}
	.ui-list-info span{width:20px;height:20px;background-size:100%;background-repeat:no-repeat;display:inline-block;}
	.ui-list-date{line-height:26px;background-color:#f0eff5;}
	.ui-list-date span{font-size:14px;margin-left:14px;color:#777777}
	
	.master{background:#f2f2f2; border-bottom:1px solid #dcdcdc; padding:20px 15px; position: relative; overflow:hidden;}
	.master_pic span{width:45px; height:45px; display:block; border-radius:5px; background:#000; float:left; margin-left:-50px;}
	.infor{width:100%; overflow:hidden;}
	.infor h4{float:left; font-size:17px; line-height:45px; color:#000; font-weight:normal; float:left; margin:0 5px;}
	.infor em{float:right; font-size:14px; line-height:45px; color:#000; font-style:normal;}
	.master span.corner{width:40px; height:40px; display:block; position:absolute; left:0; top:0; overflow:hidden;}
	.master_box{background:#fff; border-top:1px solid #dcdcdc; border-bottom:1px solid #dcdcdc;}
	.master_box .master{background:#fff; padding:10px 10px 10px 50px; margin-left:20px;}
	.master_box .master:last-child{border:none;}
	.master_box .master .master_pic span{margin-left:-50px; border-bottom:1px solid #dcdcdc;}
</style>
	<ul class="ui-list ui-border-tb" id="fpmxList" data-role="listview">
	<#if superiors?? && (superiors?size > 0)>
		<li class="ui-list ui-border-tb master" style="margin:0 auto;">
			<div class="ui-list-thumb" style="margin:0px 10px 0px 5px;">
	           <span style="background-image:url(${superiors.headImg!''});border-radius:5px;"></span>
	        </div>
	        <div class="ui-list-info">
	            <h4>${superiors.uniName!''}</h4>
	        </div>
	        <div class="ui-list-action">${createTime?string("MM-dd HH:mm")}</div>
			<span class="corner"><img width="100%" src="${util.static}images/img/master.png" /></span>
		</li>
	</#if>
	<#list userFriends as u>
		<#if u.displayDate>
		<li class="ui-list-date" style="margin-left:0px;" data-date="${u.invi_time?string("yyyy年MM月dd日")}">
			<span>${u.invi_time?string("yyyy年MM月dd日")}</span>
		</li>
		</#if>
		<li <#if !u.displayDate>class="ui-border-t"</#if>>
	    	<div class="ui-list-thumb">
	           <span style="background-image:url(${u.friend.headImg!''});border-radius:5px;"></span>
	        </div>
	        <div class="ui-list-info">
	            <h4>${u.friend.uniName!''}</h4>
	        </div>
	        <div class="ui-list-action">${u.invi_time?string('HH:mm')}</div>
	    </li>
	</#list>    
	</ul>
<script>
$(function(){
	$(document).bind("pageinit", function() {
		isInit=0;
		serverURL = "${util.ctx}/api/v1/user/invites"; //服务器地址
		if(location.search)
			serverURL = serverURL+location.search;
		startNum = 0;			//当前页
		count = ${totalPage!0}; //总页数
		if(count <=1 ) $('#pullUp').hide();
		console.log("开始数：" + startNum + ',总页数:' + count);
		
		var initData = $('.master').length ? $('.master')[0].outerHTML : '';
		//回调处理
		callbackReviceData = function(datas){
			var result = '';
			if(startNum <= 0) {
			 	result = initData;
			}
		    for (var i = 0; i < datas.data.length; i++) {
		    	var existDate = datas.data[i].currentDate == $('li[data-date]:last').attr('data-date');
		    	var className = 'class="ui-border-t"';
		    	if(!existDate && datas.data[i].displayDate) {
			    	result += '<li class="ui-list-date" style="margin-left:0px;" data-date="' + datas.data[i].currentDate + '">'
					+ '<span>' + datas.data[i].currentDate + '</span>'
					+ '</li>';
					
					className = '';
				}
		        result += '<li '+ className +'>' 
			        		+ '<div class="ui-list-thumb">' 
			        			+ '<span style="background-image:url('+ datas.data[i].avatar + ');border-radius:10px;"></span>' 
			        		+ '</div>'
	        				+ '<div class="ui-list-info">' 
	        					+ '<h4>'+ datas.data[i].name+'</h4>'
						      +'</div>'
						      +'<div class="ui-list-action">'+ datas.data[i].invi_time+'</div>'
						+'</li>';
		    }
		    return result;
		}
	});
 });
 </script>
<#include "/common/scroll_footer.ftl">