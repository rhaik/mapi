<#import "/lib/util.ftl" as util>
<#include "/common/headerNews.ftl">
<style type="text/css">
	body{background:#f0eff5;font-family:Helvetica Neue,Helvetica,Arial,sans-serif;font-size:14px;color:#000;margin:0;padding:0;}
	.bill_box em{font-size: 12px; line-height: 16px; color: #000; display: none; font-style: normal;}
	.bill_list{padding: 0 15px; height: 78px; background: #fff; margin-top: 20px; overflow: hidden;}
	.bill_num{width: 100%; line-height: 25px; margin-top:20px; font-size: 17px; color: #000; border: none; outline: none;}
	.bill_num::-webkit-input-placeholder{color: #ff8003;}
	.yzm{margin: 20px 0 0 15px; overflow: hidden;}
	.bill_yzm{width:63%; height: 17px; line-height: 17px; padding: 20px 0; float: left; font-size: 17px; color: #000; border: none; outline: none; text-indent: 15px;}
	.bill_yzm::-webkit-input-placeholder{color: #aaa;}
	.yzm_pic{width: 27%; height: 57px; float: left; display: block; background: #fff; margin-left: 4%; overflow: hidden;}
	.yzm_pic em{width: 100%; height: 40px; display: block;}
	.yzm_pic a{font-size: 12px; color: #ff8003; text-align: center; display: block;}
	.amount{ margin: 20px 15px 0; overflow: hidden; padding-left: 0;}
	.amount li{width: 30%; height: 65px; border: 1px solid #ddd; display: block; float: left; background: #fff; margin-right: 3.4%; margin-top: 15px; overflow: hidden;}
	.amount li a{display: block; height: 65px; line-height: 65px; text-align: center; font-size: 18px; color: #aaa;}
	.amount li span{display: none; font-size: 12px; color: #aaa; text-align: center;}
	.amount li:nth-child(3n){margin-right: 0;}
	.bill_btn{margin: 35px 15px;}
	.bill_btn a{display: block; background: #fdaf62; line-height: 45px; text-align: center; border-radius: 5px; font-size: 17px; color: #fff;}
	.bill_on em{display: block;}
	.bill_on .amount li.on{border: 1px solid #ff8003;}
	.bill_on .amount li.on a,.bill_on .amount li.on span{color: #ff8003;}
	.bill_on .amount li a{height: 25px; line-height: 25px; margin-top: 12px;}
	.bill_on .amount li span{display: block;}
	.bill_on .bill_btn a{background: #ff8003;}
	.bill_on .amount li.on.select{background: #ff8003;}
	.bill_on .amount li.on.select a,.bill_on .amount li.on.select span{color:#fff;}
</style>
<div class="bill_box bill_on"> 
	<div class="bill_list">
		<input type="hidden" name="mobile_area" value="${mobilearea}" />
		<input class="bill_num" value="${mobile}"  name="mobile" type="tel" maxlength="13" onkeydown="if(value.length==3||value.length==8){value+=' '}" onfocus="value=''" placeholder="请输入手机号" pattern="[0-9 ]" onkeyup="this.value=this.value.replace(/[^0-9 ]/g,'')" onafterpaste="this.value=this.value.replace(/[^0-9 ]/g,'')"/>
		<em id="area">${areachannel}</em>
	</div>
	<div class="yzm">
		<input class="bill_yzm" name="code" type="text" placeholder="请输入验证码" />
		<span class="yzm_pic">
			<em style="overflow:hidden;"><img id="captchas" width="100%" height="40px;" src="${util.ctx}/web/recharge/captchas" onclick="refreshCode();" /></em><!-- 这里放图 -->
			<a href="javascript:refreshCode();">看不清，换一张</a>
		</span>
	</div>
	<ul class="amount">
		<#list recharge as r>
		<li data-id="${r.id}" <#if r.pay_amount<=balance>class="on" onclick="selectValue(this)"</#if>  data-price="${r.payAmountYuan!0}">
			<a>${r.value!0}元</a>
			<span>优惠价：${r.payAmountYuan!0}</span>
		</li>
		</#list>
	</ul>
	<div class="bill_btn"><a>立即充值</a></div>
</div>
<script src="${util.jquery}"></script>
<script>
function refreshCode() {
 	document.getElementById("captchas").src = "${util.ctx}/web/recharge/captchas?r=" + Math.random();
}
function IsMobilePhone(num) {
  	 var isMobilePhone = /^1[3|4|5|8|7][0-9]\d{8}$/;
   	 if (isMobilePhone.test(num)){
     	return true;
     }else{
        return false;
     }
}
$(function(){
	selectValue = function(o){
		var payAmount = parseInt($(o).attr('data-price'));
		if(${balance!0} >= (payAmount * 100)) {
			$(".amount>li").removeClass("select");
			$(o).addClass("select");
  		} else {
  			showTips("您当前充值余额不足!");
  		}
	};
	function IsMobilePhone(num) {
  	  	var isMobilePhone = /^1[3|4|5|8|7][0-9]\d{8}$/;
   	 	if (isMobilePhone.test(num)){
	       	 return true;
       	}else{
	        return false;
     	}
  	}
  	$('.bill_num').bind('change',function(){
  		var mobile = $(this).val().replace(/ /g,"");
  		var value = '';
  		if(mobile.length >= 8) {
  			value = mobile.substr(0,3) + ' ' + mobile.substr(3,4) + ' ' + mobile.substr(7,4)
  		} else if($(this).val().length > 3) {
  			value = mobile.substr(0,3) + ' ' + mobile.substr(3,10);
  		}
  		$(this).val(value);
  	}).bind('focus',function(){
  		$(".amount>li").removeClass("on").removeClass("select");
  	});
  	$('.bill_num').change();
  	$('.bill_num').on('input',function() {
  		if($(this).val().length == 13) {
  			this.blur();
  			var mobile = $(this).val().replace(/ /g,"");
  			if(IsMobilePhone(mobile)) {
	  			 $.ajax({type: "GET",url :"${util.ctx}/api/v1/recharge/queryarea",data:{mobile:mobile},dataType:'json',timeout: 30000,
					success: function(r){
						if(r.code=="0") {
							var html = "";
							$('#area').html(r.areachannel);
							if(r.data.length > 0) {
								var click="";
							  	for (var i = 0; i < r.data.length; i++) {
							  		if(r.data[i].pay_amount<=${balance!0}) {
							  			click = 'class="on" onclick="selectValue(this)"';
							  		} else click="";
							  		html += '<li data-id="'+ r.data[i].id +'" '+click+' data-price="'+ r.data[i].payamount +'"><a>'+ r.data[i].value +'元</a><span>优惠价：'+ r.data[i].payamount +'</span></li>';
							  	}
							}
							$('.amount').html(html);
						}
					},
					error: function(r){
						 $('#area').html("");
					}
				}); 
			} else {
				$(".amount>li").removeClass("on").removeClass("select");
				$('#area').html("请输入正确的手机号码！");
			}
		} else {
			$(".amount>li").removeClass("on").removeClass("select");
		}
	}); 
	$('.bill_btn').click(function() {
		var mobile = $('input[name="mobile"]').val().trim();
		var mobile = mobile.replace(/ /g,"");
		var code = $('input[name="code"]').val().trim();
		var type = $(".amount>.select").attr("data-id");
		var mobile_area = $('input[name="mobile_area"]').val();
		if(mobile == '' || mobile.length!=11) {
			$('#area').html("请输入正确的手机号码!");
			return false;
		}
		if(code == "" || code.length!=5) {
			showTips('请输入正确的验证码!');
			return false;
		}
		if(!type) {
			showTips('请选择充值面值');
			return false;
		}
		$('input[name="code"]').val('');
		$.ajax({type: "POST",url :"${util.ctx}/api/v1/recharge/save",data:{mobile:mobile,code:code,type:type,mobile_area:mobile_area},dataType:'json',timeout: 30000,
			success: function(r) {
				refreshCode();
				showTips(r.message); 
			},
			error: function(r){
				 showTips("充值失败!");
				 refreshCode(); 
			}
		}); 
	});
	function showTips(message) {
		if(typeof MiJSBridge=="object") {
			 MiJSBridge.call('alert', {title: message}, function(){});
		} else {
			alert(message);
		}
	}
});
</script>
<#include "/common/footer.ftl">