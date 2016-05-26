<#import "/lib/util.ftl" as util>
<#include "/common/headerNews.ftl">
<style>
	<style type="text/css">
		body{background:#fff; font-family:"华文细黑";}
		.prompt{font-size:1em; color:#000; text-align:center; line-height:1.6em; margin:3em 0 1em;}
		.con_icon{width:9.375em; height:9.375em; margin:0 auto; background:url(${util.static}images/download_02.png) no-repeat; background-size:100%;}
		.text{font-size:0.8em; color:#000; text-align:center; margin:1.5em auto;}
		.dn{display: none;}
		.tips{background:url(${util.static}images/download_01.png) no-repeat;background-size:100%;margin:20px 30px;height:165px;border-bottom: 1px solid #dcdcdc;}
		.tips h4{padding-top:21px;font-size:16px;}
		.download{height:20px;}
		.ios8{display: block;}
		.ios9{display: none;}
	</style>
</style>
<script>
	function checkIphoneVer(){
		var agent = navigator.userAgent;
		var reg = "CPU (iPhone|iPad) OS 9(\\w+) like Mac OS X\\)";
		var patt=new RegExp(reg);
		//ios9
		if(patt.test(agent)){
			window.location="${util.static}html/Install9.html";
		}
	}
	checkIphoneVer();
	
</script>
<div class="wrapper" >
	<div id="content">
		<div class="prompt">如果您已下载秒赚大钱</br>请直接点击下面图标打开</div>
		<div class="con_icon" onclick="doDown()"><a href="${util.ctx}/www/downloads/ysurl" id="download" style="display:none;"></a></div>
		<div class="text">如果没有出现安装按钮，请直接点击上面图片安装</div>
	</div>
	<img name="ios8" class="ios8" src="${util.static}images/download/img8.jpg" />
</div>
<script>
	function doDown(){
		document.location="${util.ctx}/www/downloads/ysurl";
	}
	
	var timeout;
	var appurl="bmyaoshi://"
	function preventPopup() {
	    clearTimeout(timeout);
	    timeout = null;
	    window.removeEventListener('pagehide', preventPopup);
	}
	function openApp() {
	    $('<iframe />')
	    .attr('src', appurl)
	    .attr('style', 'display:none;')
	    .appendTo('body');
	}
	function isSafari9(){
		var u = navigator.userAgent;
		return u.indexOf('Safari') > -1 && u.indexOf('Version/9.') > -1;
	}
	function isIos(){
		var u = navigator.userAgent;
		return u.indexOf('iPhone') > -1 || u.indexOf('iPad') > -1;
	}

	if(isIos()){
		/* setTimeout(function(){
			openApp();
		}, 500); */
		timeout = setTimeout(function() {
			if(isSafari9()){
				document.location="${util.ctx}/www/downloads/ysurl";
			}else{
		    	var download = document.getElementById('download');
				if(typeof download == "object") {
					download.click();
				}
			}
    	}, 1000);
    	window.addEventListener('pagehide', preventPopup);
	}else{
		alert("对不起，该app只支持苹果手机下载！");
	}
</script>
<#include "/common/footer.ftl">