<#import "/lib/util.ftl" as util>
<#list usertasks as task>
		<#if task.displayDate>
		<li class="ui-list-date" style="margin-left:0px;" data-date="${task.currentDate}">
			<span>${task.userTask.starttime?string("yyyy年MM月dd日")}</span>
		</li>
		</#if>
	    <li <#if !task.displayDate>class="ui-border-b"</#if>>
	        <div class="ui-list-thumb">
	            <span style="background-image:url('${task.app.icon!''}');border-radius:10px;"></span>
	        </div>
	        <div class="ui-list-info">
	            <h4>${task.appTask.name}</h4> 
	        </div>
	        <span class="status-btn">${util.fen2yuan(task.appTask.amount!0)} 元</span>
	    </li>
	</#list>