<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
	<style type="text/css">
		body{background:#f0eff5;font-family:Helvetica Neue,Helvetica,Arial,sans-serif;font-size:14px;color:#000;margin:0;padding:0;}
		.test h1{font-size:16px; color:#000; text-align:center; font-weight:normal; padding:15px 0;}
		.ipt_text{background:#fff; border-top:1px solid #dcdcdc; border-bottom:1px solid #dcdcdc; padding-left:10px;}
		.ipt_text input{font-size:16px; color:#999; border:none; outline:none; line-height:45px;} 
		.ipt_text input::-webkit-input-placeholder {color:#CFCFCF;}
		.ipt_text span{background:#ff8003; width:100px; display:block; font-size:12px; color:#fff; border-radius:3px; float:right; margin:8px 10px 0 0; line-height:29px; text-align:center;}
		.ipt_text .ipt_num{ border-bottom:1px solid #dcdcdc;}
		.mobile_phone{margin:10px; background:#ff8003; line-height:45px; text-align:center; border-radius:5px;}
		.mobile_phone a{font-size:16px; color:#fff; display:block;}
	</style>
<body> 
<div class="test">
	<h1>当前手机号码:${mobile!''}</h1>
	<div class="ipt_text">
		<div class="ipt_num">
			<input type="text" placeholder="请输入手机号" id="mobile_num"/>
			<span id="btn">获取语音验证码</span>
		</div>
		<div class="ipt_yam"><input type="text" placeholder="请输入验证码" /></div>
	</div>
	<div class="mobile_phone">
		<a id="validate">验证后绑定手机</a>
	</div>
</div>
<#if ua.inAppView>
	<#assign apiPrefix="/api/v1/user" />
<#elseif fromSafari??/>
	<#assign apiPrefix="/ios/wxapi" />
<#else/>
	<#assign apiPrefix="/weixin/api" />
</#if>
<script src="${util.jquery}"></script>
<script type="text/javascript"> 
	function IsMobilePhone(num) {
  	  var isMobilePhone = /^1[3|4|5|8|7][0-9]\d{8}$/;
   	 if (isMobilePhone.test(num)){
       	 return true;
       }else{
        return false;
     }
  }
	var countdown=60;
	var t ;
	function time(val) {
        if (countdown == 0) {
        	val.style.background="#ff8003";
            val.removeAttribute("disabled");            
            val.innerHTML="免费获取验证码";
            countdown = 60;
        } else { 
        	val.style.background="#ddd";
            val.setAttribute("disabled", true);
            val.innerHTML="重新发送(" + countdown + ")";
            countdown--;
           t= setTimeout(function() {
                time(val)
            },
            1000)
        }
    }
    
    function stop(val){
    	clearTimeout(t);
    	val.style.background="#ff8003";
    	val.innerHTML="免费获取验证码";
    }
    
	$("#btn").click(function(){
		var thatt = this;
		if($(this).attr('disabled'))return;

		mobile = $(".ipt_num input").val();
		if(!IsMobilePhone(mobile)){
			alert("请输入正确的手机号");
			return ;
		}

		$.ajax({type: "POST",url :"${apiPrefix}/get_mobile_code?",data:{mobile:mobile},dataType:'json',timeout: 30000,
				success: function(r){
					if(r.message=="ok"){
						alert("请注意接听来电");
					}else{
					 	stop(thatt);
					 	alert(r.message);
					}
				},
				error: function(r){
					stop(thatt);
					console.info(r);
					alert("拨打电话出错");
				}
			});
		time(this);
	});
	
	$(".mobile_phone a").click(function(){
		mobile = $(".ipt_num input").val();
		if(!IsMobilePhone(mobile)){
			alert("请输入正确的手机号");
			return ;
		}
		code = $(".ipt_yam input").val();
		console.info(mobile+"->"+code);
		$.ajax({type: "POST",url :"${apiPrefix}/bind_mobile",data:{code:code,mobile:mobile},dataType:'json',timeout: 30000,
				success: function(r){
					if(r.message=="ok"){
						alert("绑定成功");
						if(location.href.indexOf('#withdraw') > 0){
							setTimeout(function(){history.back();}, 1000);
						}
					}else{
						alert(r.message);
					}
				},
				error: function(r){
					console.info(r);
					alert("绑定电话出错");
				}
			});
	});
</script> 
<#include "/common/footer.ftl">
<#include "/weixin/wx_share.ftl">
</body> 