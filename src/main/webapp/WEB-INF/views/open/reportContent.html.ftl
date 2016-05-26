<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<style>
.way{padding:0.7em 0 0.3em 0.9em;  font-size:0.9em; color:#666; line-height:1.5em;}
.reason{padding:0 0.9em; background:#fff; border-top:0.5px solid #ccc; border-bottom:0.5px solid #ccc;}
.describe{font-size:0.9em; height:6em; overflow:hidden; padding: 0.5em 0; outline:none; border:none; width:100%;}
.next{margin:1.2em 0.9em;}
.next a{width:100%; height:2.5em; line-height:2.5em; display:block; background:#3CB60C; border-radius:0.3em; font-size:1em; color:#fff; text-align:center;}
.prev a{background:#CCCDD7;}
</style>
<body>
<div class="report">
	<div class="way">举报描述</div>
	<div class="reason">
		<textarea class="describe" id="report-cont"></textarea>
	</div>
	<div class="next" id="doReport"><a>提交</a></div>
</div>
<script src="${util.zepto}"></script>
<script type="text/javascript">
$('#doReport').click(function(){
	if(!$('#report-cont').val()){
		alert('请输入举报描述');
		return false;
	}else {
		setTimeout(function(){alert('举报成功！');}, 200);
	}
});
</script>
<#include "/common/footer.ftl"/>