<#import "/lib/util.ftl" as util />
<#include "/common/header.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}/css/common.css">
<link rel="stylesheet" type="text/css" href="${util.static}/css/style.css">
<div>
	<img src="${url!''}" width='100%' id="share-img">
</div>
<div class="first_btn" style="display:none;" id="save-btn">
	<a>点击保存图片</a>
</div>
<script src="${util.zepto}"></script>
<script src="${util.basejs}"></script>
<script>
if(window.MiJSBridge){
     onMiJSBridgeReady();
}else{
     document.addEventListener('MiJSBridgeReady', onMiJSBridgeReady);
}

function onMiJSBridgeReady(){
	$('#share-img').click(function(){
		MiJSBridge.call("viewImage", {images: [$('#share-img').attr('src')]});
	});
	if(typeof _MiJS != 'undefined' && _MiJS.os.android){
		MiJSBridge.call("viewImage", {images: [$('#share-img').attr('src')]});
	}else {
		$('#save-btn').click(function(){
			MiJSBridge.call("saveImage", {image:$('#share-img').attr('src')}, function(ret){
				if(ret.status == 'ok' ){
					$.myAlert('图片保存成功，请在“照片”中打开，分享给好友或朋友圈');
				}else {
					$.myAlert('图片保存失败(' + ret.err_msg + ')');
				}
			});
		}).show();
    }
}
</script>
<#include "/common/footer.ftl">