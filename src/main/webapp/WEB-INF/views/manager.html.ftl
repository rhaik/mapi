<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Movie Restful Service</title>
	</head>
	<body>
		<center>
			free memory: ${freeMemory!""}<br>
			total memory: ${totalMemory!""}<br>
			<form name="groovy" action="ms" method="post">
				<input type="hidden" name="msid" value="${msid}" />
				<table width="60%">
					<tr><td align="left">1、groovy</td></tr>
					<tr><td align="left">${groovyOutput!""}</td></tr>
					<tr><td align="left"><textarea name="groovyInput" cols="80" rows="10">${groovyInput!""}</textarea></td></tr>
					<tr><td align="left"><input type="submit" value="groovy" /></td></tr>
					<tr><td>实例：</td></tr>
					<tr><td>1、获取Spring Bean：AppContext.getBean('userService')</td></tr>
				</table>
			</form>
		</center>
	</body>
</html>