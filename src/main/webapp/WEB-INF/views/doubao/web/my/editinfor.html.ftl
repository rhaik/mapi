<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<link rel="stylesheet" type="text/css" href="${util.static}css/webuploader.css" />
	<style>
		.webuploader-pick {padding:0; margin:0; background:#fff;}
		.webuploader-pick {display:block;}
	</style>
<div class="bg_f">
	<a class="edit_infor edit_header" id="edit_header">
		<span>头像</span>
		<p><i><img src="${user.headImg}" /></i><em><img src="${util.static}images/img/right_ico.png"></em></p>
	</a>
	<a class="edit_infor">
		<span>用户ID</span>
		<p>${user.user_identity}</p>
	</a>
	<a class="edit_infor edit_name">
		<span>昵称</span>
		<p><b>${user.name}</b><em><img src="${util.static}images/img/right_ico.png"></em></p>
	</a>
	<a class="edit_infor edit_mobile">
		<span>电话</span>
		<p>${user.mobile}<em><img src="${util.static}images/img/right_ico.png"></em></p>
	</a>
</div>
<!-- 修改信息 -->
<div class="modify dn" id="edit_name">
	<p><input type="text" placeholder="输入昵称" name="name"/></p>
	<a>确定</a>
</div>

<script src="${util.jquery}"></script>
<script src="${util.static}js/duobao.js"></script>
<script src="//cdn.bootcss.com/webuploader/0.1.1/webuploader.html5only.min.js"></script>
<script type="text/javascript" src="${util.static}js/upload.js"></script>
<script type="text/javascript">
	$(function() {
		$("div.bg_f").on("click","a.edit_mobile",function() {
			$('body').attr('data-refresh', 1);
			if(typeof MiJSBridge=="object") {
				MiJSBridge.call('bindMobile');
			}else{
				toUrl('${util.ctx}/ios/my/to_bind_mobile.html#withdraw');
			}
		}).on("click","a.edit_name",function() {
			$("#edit_name").removeClass("dn");
			$(this).parent().addClass("dn");
			$("#edit_name input[name=name]").val($(this).find("b").text()).focus();
			$("header.header > span").text("用户昵称");
			//添加返回按钮事件
			$("header.header a.back").one("click",function(){
				$("#edit_name").addClass("dn");
				$("div.bg_f").removeClass("dn");
				$("header.header > span").text("编辑信息");
				return false;
			})
		});
		
		$("div.modify").on("click","a",function() {
			$.trim($("input[name=name]").val()) ? updateInfo({name:$("input[name=name]").val()}) : myAlert("请输入用户昵称");
		})
		
		function updateInfo(data) {
			myPost('${util.ctx}/doubao/api/my/editinfor', data, function(r){
				if(r.code==0) {
					myAlert("修改成功", function(){ location.reload();});
				} else {
					myAlert(r.message);
				}
		    });
		}
		var options = {
			auto:true,
			max: 1,
			data : {token : '${qiniuToken}'},
			pickedCallback : function(file, src){
				$.showTips('图片上传中...', true);
			},
			uploadedCallback : function(file, resp){
				if (resp && resp.key){
					var url = 'http://cdn.erbicun.cn/' + resp.key;
					$("#edit_header").attr('data-src', url);
				}
			},
			uploadError : function(file){
				$.hideTips();
				myAlert('头像上传失败，请稍后重试');
			},
			uploadFinished : function(){
				$.hideTips();
				$("#edit_header").data("src") ? updateInfo({image: $("#edit_header").data("src")}) : myAlert('头像上传失败，请稍后重试');
			}	
		};
		uploader = $.webUpload('a.edit_header', 'https://up.qbox.me/',options);
	});
</script>
<#include "/common/footer.ftl">
