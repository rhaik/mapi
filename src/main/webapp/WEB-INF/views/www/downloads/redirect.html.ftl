<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">

<script>
var timeout;
var appurl="bigmoney://?action=setShare&shareId=${uuid!''}"
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

    timeout = setTimeout(function() {
            document.location = "about:blank";
    }, 500);
    window.addEventListener('pagehide', preventPopup);
}
function isSafari9(){
	var u = navigator.userAgent;
	return u.indexOf('Safari') > -1 && u.indexOf('Version/9.') > -1;
}
if(isSafari9()){
	document.location = appurl;
}else{
	openApp();
}
</script>
<#include "/common/footer.ftl">