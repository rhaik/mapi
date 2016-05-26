<#import "/lib/util.ftl" as util>
<#include "/common/headerNews.ftl">
<style type="text/css">
	body{background:#fff}
	.main{background:#fff; padding:1em 0.75em;}
	.title{font-size:1.5em; color:#000; overflow:hidden; overflow:hidden; text-overflow:ellipsis; display:-webkit-box; -webkit-line-clamp: 2; -webkit-box-orient:vertical;}
	.date{font-size:1em; color:#8a8888; margin-top:1em;}
	.date a{color:#5b81b0; padding-left:1em;}
	.care{margin:0.9em 0;}
	.income{font-size:1em; color:#000; text-align:center;}
	.income i{font-style:normal; color:#ff0808;}
	.income em{font-style:normal; color:#0280c8;}
	.infor a{font-size:1em; color:#5b81b0; display:block; float:left;}
	.infor span{font-size:1em; color:#666; font-weight:normal; display:block; float:left; margin-left:2em;}
	.infor span img{width:1em; height:1em; display:inline-block; vertical-align:top; margin-right:0.3em;}
	.infor span:last-child{float:right;}
	#biz-link.btn {background: url(http://t2.qpic.cn/mblogpic/426c637092f06ffde172/2000) no-repeat center center; background-size: 100% 100%; border: none; height: 2.6em; padding: 0.63em; display: block; margin-top: 8px;}
	.btn{background: -webkit-gradient(linear,left top,left bottom,from(#95acc5),to(#7992af)); border:1px solid #7992af; -webkit-border-radius:2px; color:white; display:inline-block; height:2.6em; line-height:2.6em; padding:0 0.625em; -webkit-box-shadow:0 1px 0 white;}
	#biz-link.btn .logo{float:left; overflow:hidden;}
	#biz-link.btn .logo .circle{width:2.6em; height:2.6em; border-radius:2.6em; overflow:hidden; float:left;}
	#biz-link.btn #nickname{font-size:1.1em; color:#454545; float:left; margin-left:0.5em;}
	.arrow{float:right;}
	.pb{padding-bottom:7em;}
	.ad{position:fixed; bottom:0; left:0;}
	.content{margin:1em 0em;}
	.search{position:fixed; right:0.9em; top:0; width:11em;}
</style>
	<div class="main">
		<div class="title">${vo.transArticle.name!''}</div>
		<div class="date">${vo.transArticle.createtime?string('yyyy-MM-dd')}<a href="${util.ctx}/open/account/${wxAccountId!''}">${weixinName!'手机赚钱'}</a></div>

		<div class="content">
			${vo.transArticle.content!''}
		</div>
		<div class="infor clearfix">
			<a href="${util.ctx}/open/account/${wxAccountId!''}">阅读原文</a>
			<span>阅读 ${vo.transArticle.read_num!'1268'}</span>
			<span><img src="${util.static}images/weixin/zan.png" />${vo.transArticle.zan_num!'1268'}</span>
			<span id="report">举报</span>
		</div>
	</div>
	<#if !vo.expired><div class="search"><img src="${util.static}images/search.png" /></div></#if>
<script type="text/javascript">
	(function(){
		var imgList = document.getElementsByTagName('img');
		for(var i = 0, len= imgList.length; i < len; ++ i){
			var src = imgList[i].getAttribute('src');
			if(src && src.indexOf('mmbiz.qpic.cn/') > 0){
				imgList[i].style.display = 'none';
			}
		}
	})();
</script>
<script src="https://i.gtimg.cn/vipstyle/frozenjs/lib/zepto.min.js?_bid=304"></script>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script type="text/javascript">
	var idx = 1;
	$(function(){
		function showImg($img, index) {
			var frameid = 'frameimg_' + index;
			var wximgid = $img.attr('id');
			var imgid = 'img_' + index;
			var src = $img.attr('src');
			src = src.replace('tp=webp', '');
			src += (src.indexOf('?') > 0 ? '&' : '?') + 'imgid=' + imgid;
			window[imgid] = '<img id="'+ imgid +'" src="'+ src + '" style="width:100%;"/>' +
							'<script> ' +
							' window.onload = function() { ' +
							' var img = document.getElementById("' + imgid +'"), wximg = parent.document.getElementById("'+wximgid+'"), frm = parent.document.getElementById("'+frameid+'"); ' +
							' if(img.complete){ frm.style.height = document.body.scrollHeight + "px";} else{ img.onload = function(){frm.style.height =  document.body.scrollHeight  + "px";}; } ' +
							' } <' + '/script>';
			$img.after('<iframe id="'+frameid+'" src="javascript:parent.' + imgid + ';" frameBorder="0" scrolling="no" style="width:100%"></iframe>');
        }

		$('img').each(function(){
			var $this = $(this);
			var src = $this.attr('src');
			if(src && src.indexOf('mmbiz.qpic.cn/') > 0){
				$this.attr('id', 'wx_img_' + idx);
				showImg($this, idx);
				idx += 1;
			}
		});

		function getParams(src){
			var params = {};
			var pos = src.indexOf('?');
			var url = src;
			if(pos > 0){
				url = src.substring(0, pos);
				var query = src.substring(pos + 1);
				var queries = query.split('&');
				for(var i = 0, len = queries.length; i < len; ++ i){
					var items = queries[i].split('=');
					if(items.length == 2){
						params[items[0]] = items[1];
					}
				}
			}
			params['url'] = url;
			return params;
		}
	});

	wx.config({
	    debug: false,
	    appId: "${sharemap.appid!''}",
	    timestamp: ${sharemap.timestamp!0},
	    nonceStr: "${sharemap.nonceStr!''}",
	    signature: "${sharemap.signature!''}",
	    jsApiList: [
	      'onMenuShareTimeline',
	      'onMenuShareAppMessage'
	    ]
	});
	function doShare(){
		<#if !vo.expired>
		$.ajax({
			type: "POST",
			url: '/weixin/article/share_success',
			data: {'se_id':'${se_id!''}','d_id':'${_data!''}'},
			dataType:'json',
			success:function(ret){
				alert(ret.message);
			}
		});
		</#if>
	}
	var shareData = {
		title: '${util.jsonQuote(vo.transArticleTask.name)!""}',
		desc: '${util.jsonQuote(vo.transArticleTask.name)!""}',
		link: '${shareUrl!""}',
		imgUrl: '${vo.transArticle.defaultImg}',
		success: function (res) {
			doShare();
		},
		cancel: function (res) {},
		fail: function (res) {}
	};
	wx.ready(function () {
	    wx.onMenuShareAppMessage(shareData);
	    wx.onMenuShareTimeline(shareData);
	});
	wx.error(function(res){});

	$('#report').click(function(){
	    location.href = '${util.ctx}/open/report';
	});
	</script>
<#include "/common/footer.ftl">