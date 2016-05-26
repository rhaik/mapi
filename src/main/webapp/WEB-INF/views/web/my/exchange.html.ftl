<#import "/lib/util.ftl" as util>
<#include "/common/scroll_header.ftl">
<style>
	body, .ui-content:after{background-color:#f0eff5;}
	.ui-list{margin-top:10px;}
	.ui-list-info h4{margin:10px 0px;}
</style>
<ul class="ui-list ui-border-tb" id="fpmxList" data-role="listview">
<#list exchangeLog as l>
	<li class="ui-border-t">
        <div class="ui-list-info">
            <h4>${l.createtime?string("yyyy-MM-dd HH:mm:ss")}</h4>
        </div>
        <div class="ui-list-action">${util.fen2yuanS(l.rmb!0)}元</div>
    </li>
</#list>   
</ul>
<script>
$(function(){
	$(document).bind("pageinit", function() {
	isInit=0;
		serverURL = "${util.ctx}<#if fromSafari??>/ios/wxapi/<#else>/api/v1</#if>/user/exchangelog?source=${source!0}"; //服务器地址
		if(location.search)
			serverURL = serverURL+location.search;
		startNum = 0;			//当前页
		count = ${totalPage!0}; //总页数
		if(count <=1 ) $('#pullUp').hide();
		console.log("开始数：" + startNum + ',总页数:' + count);
		//回调处理
		callbackReviceData = function(datas){
			var result = '';
			if(!datas|| datas.data.length == 0){
				console.info("not data");
				return ;
			}
		    for (var i in datas.data) {
		        result += '<li class="ui-border-t">'
					    +'    <div class="ui-list-info">'
					    +'        <h4>'+datas.data[i].createtime+'</h4>'
					    +'    </div>'
					    +'    <div class="ui-list-action">'+datas.data[i].rmb+'元</div>'
					    +'</li>';
		    }
		    return result;
		}
	});
 });
 </script>
<#include "/common/scroll_footer.ftl">