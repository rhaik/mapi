<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Push Service</title>
		<style>
			.form-control {padding:10px;}
		</style>
	</head>
	<body>
	<div style="margin:20px 30px">
		<#if error??><p style="color:red">${error}</p></#if>
		<p>PUSH 个数：${pushSize!0}</p>
		<form name="form" action="/ms/push" method="post">
			<input type="hidden" name="msid" value="${msid}" />
			<div class="form-control">
			1、<label>选择终端：</label><select name="clientType">
						<option value="2" <#if clientType==2>selected="selected"</#if>>IOS</option>
						<option value="1" <#if clientType==1>selected="selected"</#if>>Android</option>
					</select>
			</div>
			<div class="form-control">
			2、<label>APPID:</label><input type="text" name="appId" value="${appId!0}" style="width:200px" placeholder="输入app 的id"/>
			</div>
			
			<div class="form-control">
			3、<label>起始:</label><input type="text" name="start" value="${start!0}" style="width:100px" placeholder="起始索引"/>
			<label>个数:</label><input type="text" name="size" value="${size!10}"" style="width:100px" placeholder="个数"/>
			</div>
			
			<div class="form-control">
			<textarea name="content" cols="80" rows="10" placeholder="发送内容">${content!''}</textarea>
			</div>
			<div class="form-control">
			<input type="submit" value="提交" />
			
			</div>
		</form>
	</div>
	
	</body>
</html>