(function(win){

    var CookieUtil = {
        cookie : function(name, value, expire){
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
            }else if(!value){ //read
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
        }
    };

    function EventBus(){
    	this.handlers = {};
    }

    EventBus.prototype = {
    	constructor: EventBus,
    	on: function(type, target) {
            "undefined" == typeof this.handlers[type] && (this.handlers[type] = []);
            this.handlers[type].push(target);
        },
        fire: function(event) {
            if ( (event.target || (event.target = this)) &&
            	this.handlers[event.type] instanceof Array ){
                for (var targets = this.handlers[event.type], i = 0, len = targets.length; i < len; i++){
                    targets[i](event);
                }
            }
        },
        off: function(type, target) {
            if (this.handlers[type] instanceof Array) {
                if (target) {
                	 for (var targets = this.handlers[type], i = 0, len = targets.length; i < len && targets[i] !== target; i++)
                    ;
               		n.splice(i, 1);
                }else{ //delete all handlers
                	this.handlers[type] = [];
                }
            }
        }
    };

    function WSBridge(ws){
    	this.ws = ws;
    	this.isOpen = false;
        this.seqNo = 1;
        this.isConnected = false;
        this.tryCount = 0;
    }

    WSBridge.prototype = new EventBus();
    WSBridge.prototype.connect = function(){
		if (!this.ws || (this.socket && this.isOpen) ) return;

		this.socket = new WebSocket(this.ws);

		var sock = this.socket, This = this;
		sock.onopen = function(t) {
        	This.isOpen = true;
            This.isConnected = true;

            This.fire({type: "open"});
        	console.log('WS连接成功');
    	};
    	sock.onmessage = function(msg){
    		var result = JSON.parse(msg.data);
    		This.fire({type: result.callbackId,  data: result});
    	};
    	sock.onclose = function(t) {
            This.isOpen = false;
            
            This.fire({type:'close'});
            console.log('WS链接被动关闭');
        };
    };	
    	
    WSBridge.prototype.disconnect = function(){
    	if (this.socket && this.isOpen) {
    		this.socket.onclose = function(){console.log('WS链接主动关闭');};
    		this.socket.close();
    		this.isOpen = false;
    	};
    };

    WSBridge.prototype.call = function(action, data, callback){
    	if (!this.socket || this.socket.readyState != 1){
    		console.log('错误：WS连接已关闭');
    		this.fire({type:'error'});
            return;
    	}

    	if (typeof data == 'function') {
    		action = null;
    		data = action;
    		callback = data;
    	};

    	var sock = this.socket, This = this;

    	if (callback) {
    		data.callbackId = 'ws_callback_' + (this.seqNo ++);
    		This.on(data.callbackId, function(evt){ callback(evt.data);});
    	};

    	if (action) {
    		data.action = action;
    	};
    	
        var msg = JSON.stringify(data);
    	sock.send(msg);

    	return This;
    };

    //logic begin
    function showDialog(title, btn, callback, showCancel){
        var $dialog = $('#ui-dialog');
        $('#ui-dialog-title').html(title || '');
        callback = callback || function(){};
        $('#ui-dialog-btn').off('click').on('click', function(){
            (callback() !== false) && $dialog.dialog('hide');
        }).html(btn || '确定');
        $('#ui-dialog-btn').siblings('button').remove();
        $('#ui-dialog-btn').parent().addClass('ui-btn-wrap').removeClass('ui-btn-cont');
        if(showCancel) { //show cancel button
            $('#ui-dialog-btn').parent().addClass('ui-btn-cont').removeClass('ui-btn-wrap');
            $('#ui-dialog-btn').before('<button class="ui-btn ui-btn-cancel">取消</button>');
            $('#ui-dialog-btn').siblings('button').click(function(){
                $('#ui-dialog-btn').siblings('button').remove();
                $dialog.dialog('hide');
            });
        }
        $dialog.dialog("show");
    }
    
    function onWSOpen(){
        MiWSBridge.on('error', checkWSConnection);
        MiWSBridge.call('checkEnv', {info: bm_config.tk}, function(data){
            if(data.code != 0){
                (typeof onWSRuntimeError == 'function' && onWSRuntimeError(data.code)) || onWSError();
            }else{
                MiWSBridge.version = data.version;
                CookieUtil.cookie('mi_ws_version', data.version, 30 * 86400);
                if(data.version && data.version.indexOf('1.') == 0){
                    showDialog('系统检测到您的钥匙版本太低，请升级后使用', '立即升级', function(){
                        location.href = '/www/downloads/ysurl';
                        return false;
                    });
                }else {
                    typeof onWSReady == 'function' && onWSReady();
                }
            }
        });
    }

    function onWSError(){
        if(typeof onWSRuntimeError == 'function'){
            onWSRuntimeError();
        }else {
            showDialog('系统检测到钥匙运行异常，请重新启动钥匙，从钥匙里点击“返回秒赚”', '启动钥匙', function(){
                location.href = '/www/downloads/safari';
                return false;
            });
        }
    }

    function openApp(url){
        location.href = url;
    }

    //延迟显示错误信息
    var checkTimer = null;
    function checkWSConnection(){
        clearTimeout(checkTimer);
        if (MiWSBridge.isOpen) {
            //connect ok
            MiWSBridge.tryCount = 0;
        }else if ( (!MiWSBridge.isConnected  && MiWSBridge.tryCount > 1) || MiWSBridge.tryCount > 5) {
            MiWSBridge.tryCount = 0;
            onWSError();
        }else{
            MiWSBridge.tryCount += 1;
            MiWSBridge.connect();
            checkTimer = setTimeout(checkWSConnection, 2000);
        }
    }


    function initWSBridge(){
        win.MiWSBridge = new WSBridge(bm_config.ws);
        MiWSBridge.on('close', checkWSConnection);
        MiWSBridge.on('open', onWSOpen);
        MiWSBridge.connect();
    }

    initWSBridge();

    
    win.EventBus = win.EventBus || EventBus;
    win.WSBridge = win.WSBridge || WSBridge;
    win.showDialog = win.showDialog || showDialog;
    win.openApp = win.openApp || openApp;
    win.CookieUtil = CookieUtil;

})(window);


