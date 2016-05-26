<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<div class="content_box">
	<div class="edit" id="address-select">
		<p><input type="text" placeholder="收货人姓名" name="name" value="${ua.name!''}"/></p>
		<p><input type="tel" placeholder="联系方式" name="mobile" value="${ua.mobile!''}"/></p>
		<p class="select" >
			<select class="province" id="province-select" data-value="${ua.province}" data-first-title="请选择省份"></select>
		</p>
		<p class="select">
			<select class="city" id="city-select" data-value="${ua.city}" data-first-title="请选择城市"></select>
		</p>
		<p class="select">
			<select class="area" id="area-select" data-value="${ua.area}" data-first-title="请选择区县"></select>
		</p>
		<div class="address_ipt"><textarea placeholder="详细地址" name="address" onpropertychange="this.style.height=this.scrollHeight+'px';" oninput="this.style.height=this.scrollHeight+'px';" style="overflow-y:hidden;">${ua.detailAddress!''}</textarea></div>
	</div>
	<div class="set clearfix">
		<span>设为常用收货地址</span>
		<em id="selected" class="<#if ua?? && ua.preferred==1>on</#if>"><a></a></em>
	</div>
	<div class="tishi">用户填写收货地址后，方可发货，最多可添加三个收货地址</div>
</div>

<!-- 底部按钮 -->
<div class="posi_btn">
	<a class="on" id="save">确定</a>
</div>
<script src="${util.jquery}"></script>
<script src="${util.static}js/duobao.js"></script>
<script src="${util.static}js/jquery.cxselect.js"></script>
<script>
$(function(){
	$(".set em").on('click',function(){
        if(!$(this).hasClass("on")){
        	$(this).addClass("on");
        }else{
        	$(this).removeClass("on")
        }
    });

    $('#address-select').cxSelect({
    	url : '${util.static}js/cityData.json.txt',
  		selects: ['province', 'city', 'area']
	});

	var addressId = ${ua.id!0};
	if(addressId > 0) {
		$('header a.right_btn').show().attr('href', 'javascript:;').click(function(){
			if(addressId >0 && confirm('确定删除该地址吗？') ) {
				myPost("${util.ctx}/doubao/api/my/deleteUserAddress", {addressId:addressId},function(r){
					if(r.code==0) {
						 myAlert("删除成功!");
						 setTimeout(function(){myClose();}, 200);
					} else {
						 myAlert(r.message);
					}
			    });
			}
		}).find('img').attr('src', '${util.static}images/duobao/del.png');
	} else {
		$('header a.right_btn').hide();
	}

	$('#save').click(function(){
		var addressId = ${ua.id!0};
		var name = $('input[name="name"]').val();
		var mobile = $('input[name="mobile"]').val();
		var province = $('#province-select option:selected').val() || '';
		var city = $('#city-select option:selected').val() || '';
		var area = $('#area-select option:selected').val() || '';
		var detailAddress = $('textarea[name="address"]').val();
		var preferred = $('#selected').hasClass("on") ? 1 : 0;
		if(name=="") {
			myAlert("收货人不能为空");
			return ;
		}
		if(mobile=="") {
			myAlert("联系方式不能为空");
			return ;
		}
		if(!province){
			return myAlert('请选择省份');
		}
		if(!city) {
			return myAlert('请选择城市');
		}
		if(!area && $('#area-select option').size()){
			return myAlert('请选择区县');
		}

		if(detailAddress=="") {
			myAlert("详细地址不能为空");
			return ;
		}
		myPost("${util.ctx}/doubao/api/my/saveUserAddress", {addressId:addressId,name:name,mobile:mobile,address:[province, city, area, detailAddress].join(' '),preferred:preferred}, function(r){
			if(r.code==0) {
				 myAlert("保存成功!");
				 setTimeout(function(){myClose();}, 200);
			} else {
				 myAlert(r.message);
			}
	    });
	});
});
</script>
<#include "/common/footer.ftl"/>