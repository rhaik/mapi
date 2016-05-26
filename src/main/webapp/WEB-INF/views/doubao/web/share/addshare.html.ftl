<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<link rel="stylesheet" type="text/css" href="${util.static}css/webuploader.css" />
<#if isShowUpload>
<style>
	body{background:#fff;}
	.webuploader-pick {padding:0; margin:0; background:#fff;}
</style>
</#if>
<div style="padding:1px"></div>
<!-- 添加晒单 -->
<div class="add_text">
	<textarea placeholder="您在一刻的想法..." name="title" id="text-area"></textarea>
</div>

<#if isShowUpload>
<div class="add_img">
	<ul class="clearfix" id="img-list">
		<li id="add-image"><img class="addImg" src="${util.static}images/duobao/add_img.png" /></li>
	</ul>
</div>
</#if>
<#if ua.inAppView>
<div class="bottom_btn">
	<a href="javascript:;" id="submit-btn">提交</a>
</div>
</#if>

<div id="upload-image" class="dn"></div>

<div class="popup_bg dn">
	<div class="add_img_list">
		<div class="add_list">
			<p>拍照</p>
			<p>从手机相册选择</p>
		</div>
		<div class="add_list">
			<p>取消</p>
		</div>
	</div>
</div>
<script src="${util.jquery}"></script>
<script src="${util.static}js/duobao.js"></script>
<script src="//cdn.bootcss.com/webuploader/0.1.1/webuploader.html5only.min.js"></script>
<script type="text/javascript" src="${util.static}js/upload.js"></script>
<script type="text/javascript">
$(function(){
	var current = 0,shareImage = [];
	var orderProductId = ${orderProductId};
	var title = '';
	var times = 0;
	var error = 0;

<#if isShowUpload>
	uploader = $.webUpload('#add-image', 'https://up.qbox.me/', {
		max: 3,
		data : {token : '${qiniuToken}', xUid : '${user.user_identity}', xOid: '${orderProductId}' },
		pickedCallback : function(file, src){
			$('#add-image').before('<li><img src="' + src + '" id="up_img_' + file.id +'"  data-fd="' + file.id + '" /><em>×</em></li>');
			uploader.getFiles('queued').length >= 3 && $('#add-image').hide();
		},
		uploadedCallback : function(file, resp){
			//console.log('uploadedCallback:' + JSON.stringify(resp));
			if (resp && resp.key){
				var url = 'http://cdn.erbicun.cn/' + resp.key;
				$('#up_img_' + file.id).attr('data-src', url);
			}
		},
		uploadError : function(file){
			$.hideTips();
			error = 1;
			var imageList = getUploadedImages();
			if (imageList.length ) {
				myConfirm('部分图片上传失败，是否忽略失败的图片，继续晒单？', function(){doAddShare(imageList);});
			}else {
				myAlert('图片上传失败，请稍后重试');
			}
		},
		uploadFinished : function(){
			if(error) return;
			setTimeout(function(){
				var shareImage = getUploadedImages()
				if(!shareImage || !shareImage.length){
					return myAlert('请添加晒单图片');
				}else{
					doAddShare(shareImage);
				}
			}, 500)
		}
	});

	function getUploadedImages(){
		return $('#img-list img').not('.addImg').map(function(){ return $(this).data('src'); }).get();
	}

	$('#img-list').on('click', 'em', function(){
		var img = $(this).siblings('img');
		var fd = img.data('fd');
		if(fd){
			uploader.removeFile(fd, true);
			img.parent().remove();
			$('#add-image').show();
		}
	});
</#if>
<#if !ua.inAppView>
	$('header .back').show().addClass('back_text').html('取消');
	$('header .right_btn').show().addClass('send_text').html('发布').attr('href', 'javascript:;').click(function(){
		doSubmit();
	});
<#else>
	$('#submit-btn').click(function(){
		doSubmit();
	});
</#if>

	function doSubmit(){
		title = $('#text-area').val();
		if(!title){
			return myAlert('请输入晒单内容');
		}
		if(title.length > 200) {
			return myAlert('晒单内容超过200个字符，请删减');
		}

		<#if isShowUpload>
		if(!uploader.getFiles().length){
			return myAlert('请添加晒单图片');
		}

		if(error){
			uploader.retry();
			error = 0;
		}else{
		   uploader.upload();
	    }
		$.showTips('图片上传中...', true);
		<#else>
		doAddShare();
		</#if>
	}

	function doAddShare(shareImage) {
		$.hideTips();
		var imageStr = shareImage? shareImage.join(',') : '';
		$.showTips('正在发布晒单...');
		myPost('${util.ctx}/doubao/api/share/addshare', {orderProductId:${orderProductId}, title:title, images: imageStr}, function(r){
			$.hideTips();
			if(r.code==0) {
				myAlert('恭喜，晒单成功', myClose);
			} else {
				myAlert(r.message);
			}
	    });
	}

	var message = '${ret_message!""}';
	if(message) {
		myAlert(message, myClose);
	}
});
</script>
</body> 
</html> 