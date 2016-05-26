<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<style>
.way{padding:0.7em 0 0.3em 0.9em; font-size:0.9em; color:#666; line-height:1.5em;}
.reason{padding-left:0.9em; background:#fff;}
.reason a{font-size:1em; color:#000; border-bottom:0.5px solid #dcdcdc; display:block; padding:0.75em 0; position:relative; outline:none;}
.reason a em{width:1.4em; height:1.4em; background:url(${util.static}images/weixin/choose.png) no-repeat; background-size:100%; display:block; position:absolute; right:0.9em; top:0.6em; display:none;}
.next{margin:1.2em 0.9em;}
.next a{width:100%; height:2.5em; line-height:2.5em; display:block; background:#3CB60C; border-radius:0.3em; font-size:1em; color:#fff; text-align:center;}
</style>
<div class="report">
	<div class="way">请选择举报原因</div>
	<div class="reason">
		<a>欺诈<em></em></a>
		<a>色情<em></em></a>
		<a>政治谣言<em></em></a>
		<a>常识性谣言<em></em></a>
		<a>诱导分享<em></em></a>
		<a>恶意营销<em></em></a>
		<a>隐私信息收集<em></em></a>
		<a>抄袭公众号文章<em></em></a>
		<a>其他侵权类（冒名、诽谤、抄袭）<em></em></a>
		<a>违规声明原创<em></em></a>
	</div>
	<div class="next"><a id="reportNext" href="reportContent">下一步</a></div>
</div>
<script src="${util.zepto}"></script>
<script type="text/javascript">
var choosed = false;
$('.reason a').click(function(){
	$('.reason em').hide();
	$(this).find('em').show();
	choosed = true;
});
$('#reportNext').click(function(){
	if(!choosed){
		alert('请选择举报原因');
		return false;
	}
});
</script>
<#include "/common/footer.ftl"/>