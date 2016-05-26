<#import "/lib/util.ftl" as util />
<#include "/common/header.ftl" />
<style>
	body{background:#fff;}
	.head_top{padding:15px 0.94em; overflow:hidden;}
	.head_top_pic{width:4.5em; height:4.5em; display:block; float:left; border-radius:0.5em; overflow:hidden; margin-right:0.5em;}
	.head_text h1{font-size:1.1em; font-weight:normal; color:#000; padding-top:0.3em;}
	.head_text p{font-size:0.9em; line-height:1em; color:#999; padding-top:1.2em;}
	.head_text p em{width:1em; height:1em; display:inline-block; margin-right:0.3em; vertical-align:top;}
	.title{text-align:center; color:#ff8003; float:right; margin-top:-3em;}
	.head_top_pic img {width:100%;}
	.title em{display:inline-block; font-size:0.875em; font-style:normal;}
	.title b{font-weight:normal; font-size:2.25em;}
	.title i{font-size:0.875em; font-style:normal;}
	.step{padding:1em 0.94em; border-top:1px solid #dcdcdc;}
	.step h1{font-size:1.1em; color:#666; font-weight:normal;}
	.step p{font-size:1em; color:#9a9a9a; line-height:1.8em;}
	.step p i{font-style:normal; color:#ff8003;}
	.icon em{font-size:0.94em; color:#444; text-align:center; display:block; font-style:normal;}
	.icon i{width:0.8em; display:block; margin:0 auto;}
	.icon a{width: 70%;height:4.375rem; line-height:4.375rem; margin:0.2em auto; display:block; border:1px dashed #999;  font-size:1.8rem; color:#444; text-align:center; border-radius:0.2em;-webkit-user-select:initial}
	.btn{width:100%; margin-top:1em; position:fixed; bottom:0; left:0; background:#fff;}
	.btn a{width:90%; height:3.75rem; line-height:3.75rem; background:#ff8003; text-align:center; font-size:1.1rem; display:block; border-radius:0.3em; margin:1.5em 5%;color: #fff;}
	.ui-dialog .ui-dialog-bd{padding-bottom:10px;}
	.ui-dialog .ui-dialog-cnt {text-align:center;}
	.ui-dialog-bd i{color:#ff8003;padding:0px 2px;}
	.ui-dialog-bd h3{font-size:18px;}
	.ui-dialog-bd h2{font-size:12px; margin-top:10px; color:#F52929;}
	.ui-btn-wrap{padding:0px 0px 14px 0px; margin-top:10px;}
	.info-tips {line-height:24px;font-size:14px;text-align:left;margin:16px 0px;}
	#copy-area {text-align: center; margin:-20px 0;-webkit-touch-callout: none;}
	#keyword {display: inline-block; color: #272727; background-color: #fff; font-size: 1.8em; border: 1px dashed #aaa; padding:10px 20px 5px 20px; margin:20px;-webkit-user-select:initial;}
	#keyword span { display:block; border:1px solid #fff; -webkit-user-select: element;}
	.ui-btn { width:100%;font-size:18px;height:40px;background-color:#ff8003;background-image:none;}
	.keyword_btn{margin:0 25px 20px; height:45px; line-height:45px; overflow:hidden;}
	.keyword_btn a{width:100%; background:#ff4e00; border-radius:3px; font-size:16px; color:#fff; display:block; text-align:center;}
	.keyword_btn a.keyword_btn_sma{width:47%; background:#ffa827; font-size:16px; color:#fff; text-align:center; overflow:hidden;}
	.keyword_btn a.keyword_btn_sma.fl{float:left;}
	.keyword_btn a.keyword_btn_sma.fr{float:right;}
</style>
<div class="wrap" id="detail-panel" prop-id="${task.app.agreement!''}"  bd-id="${task.app.bundle_id!''}">
	<div class="head_top clearfix">
		<div class="head_top_pic fl"><img src="${task.app.icon}" /></div>
		<div class="head_text fl">
			<h1>${task.appTask.keywords}</h1>
			<p><em><img width="100%"  src="/static/images/task_11.png" /></em>${task.app.download_size}M&nbsp;&nbsp;&nbsp;&nbsp;<em><img width="100%" src="/static/images/task_12.png" /></em>剩余时间: <span id="remain-time">${task.expireTime} 分钟</span></p>
		</div>
		<div class="title"><em>+</em><b>${util.fen2yuan((task.appTask.amount!0)+(task.appTask.amount!0)*(shareRate!0))}</b><i>元</i></div>
	</div>
	<div class="step">
		<h1>任务步骤</h1>
		<#if task.appTask.require_type == 3>
			<p>1、点击“下载安装”按钮，从苹果AppStore免费下载安装该应用</p>
			<p><i>2、${task.appTask.description}</i></p>
		<#else/>
			<p>1、点击“复制关键词”按钮，复制并自动跳转AppStore；</p>
			<p>2、在AppStore中粘贴并搜索；</p>
			<p>3、找到图标对应的应用(在第<i>${task.appTask.current_rank}</i>个左右）</p>
			<p><i>4、${task.appTask.description}</i></p>
		</#if>
	</div>
	<#if task.appTask.require_type == 3>
		<div class="keyword_btn install-app"><a href="javascript:;">下载安装</a></div>
	<#else/>
		<div class="keyword_btn copy-keyword"><a href="javascript:;">复制关键词：${task.appTask.keywords}</a></div>
		<div class="keyword_btn launch-app" style="display:none;"><a href="javascript:;">立即打开试玩</a></div>
	</#if>
	<div class="keyword_btn ">
		<a href="javascript:;" class="keyword_btn_sma fl abort-task">放弃任务</a>
		<a href="javascript:;" class="keyword_btn_sma fr check-status">检查完成状态</a>
	</div>
</div>
<div class="ui-dialog" id="accept-dialog">
	<div class="ui-dialog-cnt">
		<div class="ui-dialog-bd">
			<h1>参与任务赢取<i class="reward-amount"></i>元奖励</h1>
			<h2 style="display:none;"></h2>
			<div class="ui-btn-wrap">
				<button class="ui-btn ui-btn-danger">立即参加</button>
			</div>
		</div>
	</div>
</div>
<div class="ui-dialog" id="ui-dialog">
	<div class="ui-dialog-cnt">
		<div class="ui-dialog-bd">
			<h1 id="ui-dialog-title"></h1>
			<div class="ui-btn-wrap">
				<button id="ui-dialog-btn" class="ui-btn ui-btn-danger"></button>
			</div>
		</div>
	</div>
</div>
<script>
var currentApp = {
	"tid" :  "${task.appTask.encodedId}",
	"keyword": "${util.jsonQuote(task.appTask.keywords)}",
	"rank" : ${task.appTask.current_rank},
	"download_size" : ${task.app.download_size},
	"amount" : ${task.appTask.amount},
	"icon" : "${task.app.icon}",
	"agreement" : "${task.app.agreement}",
	"remain_time" : ${task.expireTime},
	"paid" : ${task.app.payWay?string("1", "0")},
	"require_type" : ${task.appTask.require_type},
	<#if task.app.process_name?starts_with("NES_")>"need_open" : 1,</#if>
	<#if task.appTask.directDownload>"download_url" : "${util.jsonQuote(task.app.description)}",</#if>
	"installed" : '${task.userTask.download}'
};
var page = "detail";
</script>
<script src="${util.static}frozenjs/1.0.1/frozen.js"></script>
<script src="${util.static}/js/app_xianshi.js"></script>
<#include "/common/footer.ftl">