!function($){
    $.myAlert = function(msg, callback) {
        if(typeof MiJSBridge=="object") {
             MiJSBridge.call('alert', {title: msg}, function(){ typeof callback == 'function' && callback(); });
        } else {
            alert(msg);
            if (typeof callback == 'function'){
                setTimeout(function(){callback();}, 200);
            }
        }
    }

    $.myConfirm = function(msg, callback){
        if(typeof MiJSBridge=="object") {
            MiJSBridge.call('confirm', {message: msg}, function(result){ result.index && typeof callback == 'function' && callback(); });
        }else {
            confirm(msg) && typeof callback == 'function' && callback();
        }
    }

    /**
     * 打开url，自动识别safari或者应用内，如果是静态资源，在safari里面自动添加iframe页面
     */
    $.openUrl = function(url, showRefresh) {
        if(!url) return;
        if(typeof MiJSBridge=="object") {
            if(url.indexOf('://') > 0 && url.indexOf('http') != 0 && url.indexOf('?action=page&page=') > 0){
                return $.myAlert('功能开发中，敬请期待');
            }

            var pos = url.indexOf('?action=url&url=');
            if(url.indexOf('://') > 0 && url.indexOf('http') != 0 && pos > 0){
                url = url.substring(pos + 16);
            }
            if(!showRefresh && (url.indexOf('showRefresh=1') > 0 || url.indexOf('showRefresh=true') > 0) ){
                showRefresh = true;
            }
            MiJSBridge.call("open", {url: url, showItem: showRefresh? true: false});
        } else {
            if(url.indexOf('/static/html/') == 0 && url.indexOf('/static/html/safari_frm.html') < 0){
                url = '/static/html/safari_frm.html?pg=' + encodeURIComponent(url);
            }
            window.location.href= url;
        }
    };

    /**
     * 打开指定的应用
     */
    $.openApp = function(scheme, bundle){
        if(typeof MiJSBridge=="object") {
            MiJSBridge.call('launchApp', {protocol: scheme, bundle:bundle});
        }else {
            location.href = scheme;
        }
    };

    $.myClose = function() {
        if(typeof MiJSBridge=="object") {
            MiJSBridge.call('close');
        } else {
            history.back();
        }
    }

    $.myPost = function(url, params, success){
        if(typeof _MiJS != 'undefined' && _MiJS.os.android){
            MiJSBridge.call('ajax', {type: 'POST', url: url, data: (typeof(params) == 'string'? params : $.param(params))  }, success);
        }else{
            $.ajax({
                   type: "POST",
                   url: url,
                   data: params,
                   dataType:'json',
                   success:success
            });
        }
    }

    /**
     * cookie操作
     * 无参数：获取所有cookie；一个参数：读取对应的cookie；两个参数：设置cookie值，默认为1天；三个参数：设置cookie，并设置有效期（秒）
     */
    $.cookie = function(name, value, expire){
        if(!name){
            var cookies = {};
            var allCookies = document.cookie.split(';');
            for(var i = 0, len = allCookies.length; i < len; ++ i){
                var item = allCookies[i].trim().split('=');
                if (item.length == 2){
                    cookies[item[0]] = decodeURIComponent(item[1]);
                }
            }
            return cookies;
        }else if(value == undefined){ //read
            var cookie = document.cookie;
            var cname = name + '=';
            var pos = cookie.indexOf(cname);
            if(pos >= 0){
                var end = cookie.indexOf(';', pos);
                end < 0 && (end = cookie.length);
                value = document.cookie.substring(pos + cname.length, end);
            }
            return value? decodeURIComponent(value) : undefined;
        }else { //set
            expire = parseInt(expire) || 86400; //1day in second
            var d = new Date();
            d.setTime(d.getTime() + (expire *1000));
            var expireStr = "expires="+d.toUTCString();
            document.cookie = name + "=" + encodeURIComponent(value) + "; " + expireStr + '; path=/';
        }
    };


    $(function(){

        //所有的a标签，如果指定了data-href,且未注册onclick事件，则使用 openUrl形式打开
        $('body').on('click', 'a,.has-link', function(){
            var href = $(this).data('href');
            href && !$(this).attr('onclick') && $.openUrl(href, $(this).data('show-refresh'));
        }).on('click', '.open-app', function(){
            var scheme = $(this).data('scheme');
            scheme && !$(this).attr('onclick') && $.openApp(scheme, $(this).data('bundle')|| '' );
        });

        //支持页面重新显示后自动刷新，需要设置$('body').data('refresh', 1)
        window.onpageshow = function(event){
            event.persisted && onPageRefresh();
        };

        if(window.MiJSBridge){
             onMiJSBridgeReady();
        }else{
             document.addEventListener('MiJSBridgeReady', onMiJSBridgeReady);
        }

        function onMiJSBridgeReady(){
            window.onpageshow = null;
            MiJSBridge.on("onRefreshPage", onPageRefresh);
        }

        function onPageRefresh(){
            if(typeof $.onPageShowAgain == 'function'){
                $.onPageShowAgain(event);
            }else if($('body').data('refresh')){
                location.reload();
            }
        }
    });

}(window.$ || window.jQuery || window.Zepto);

