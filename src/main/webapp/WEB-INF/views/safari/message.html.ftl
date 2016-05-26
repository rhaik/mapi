<#import "/lib/util.ftl" as util />
<#include "/common/scroll_header.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
<style type="text/css">
    body{background:#f0eff5;}
    .nav{ position:fixed; <#if showHeader>margin-top:44px;</#if> height:44px; width:100%; line-height:44px;}
    .nav li{width:25%; float:left; background:#fff; text-align:center; font-size:0.94em; color:#000;}
    .nav li.on{background:#0080fd; color:#fff;}
    .content{margin-top:0.625em; background:#fff; padding:0.83em;}
    .content h1{font-size:1.2em; color:#000; font-weight:normal;}
    .content span{font-size:0.875em; color:#666; display:block;}
    .content p{font-size:0.875em; color:#666; margin-top:0.83em; line-height:1.25em;}
    #wrapper {margin-top:88px;}
    #pullUp {margin-top:0px;}
</style>
<div class="main_box">
    <ul class="main"  id="fpmxList" data-role="listview">
    </ul>
</div>
</div>
<script type="text/javascript">
    function setCookie(cname, cvalue, exdays) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays*24*60*60*1000));
        var expires = "expires="+d.toUTCString();
        document.cookie = cname + "=" + cvalue + "; " + expires + '; path=/';
    }
    $(function(){
        var $tab = $('<ul class="nav clearfix"><li data-type="1">限时任务</li><li data-type="4">转发任务</li><li data-type="2">学徒奖励</li><li data-type="3">系统通知</li></ul>');
        $('header').size()? $('header').after($tab):($('#fpmxListPage').prepend($tab), $tab.next('div').css('margin-top', '44px'));
        $('.nav li').click(function(){
            $(this).addClass("on").siblings().removeClass("on");
            initMessageList($(this).data('type'))
        });


        function initMessageList(type){
            isInit=0;
            lastId = 0;
            maxMessageId = 0;
            minMessageId = 0;
            hasMore = true;
            serverURL = '${util.ctx}/ios/api/message/list?type=' + type; //服务器地址

            //回调处理
            callbackReviceData = function(datas){
                var result = '';
                for (var i = 0; i < datas.data.length; i++) {
                    var id = datas.data[i].id;

                    if(id >= minMessageId && id <= maxMessageId) continue;

                    result += composeMessage(datas.data[i], type);
                    if(id > maxMessageId) maxMessageId = id;
                    if(minMessageId <= 0 || id < minMessageId) minMessageId = id;
                }

                if(maxMessageId){
                    setCookie('msg' + type + '_last_id', maxMessageId, 90);
                }
                return result;
            }
            reinitScroll();
        }

        function composeAppMessage(msg){
            switch (msg.status) {
                case 10:
                    return {title: "任务开始", content:'您刚刚开启了' + msg.app_name + '任务，在1小时内完成可获得' + msg.amount + '元奖励！'};
                case 11:
                    return {title: "APP搜索下载成功", content: msg.app_name + '已经下载成功，请打开试用'};
                case 12:
                    return {title: "APP试用完成，等待审核", content: msg.app_name + '已试用完成，请等待奖励发放！'};
                case 13:
                    return {title: "审核通过，奖励已到账", content: msg.app_name + '试用奖励' + msg.amount + '元已经发放，请查询您的收入'};
                case 14:
                    return {title: "审核不通过", content: msg.app_name + '试用未通过审核，如有疑问可咨询客服'};
                case 15:
                    return {title: "任务即将过期", content: '您参加的' + msg.app_name + '试用任务，还有几分钟就要过期了，请尽快完成，以便顺利拿到' + msg.amount + '元奖励！'};
                default:
                    return {title: "任务开始", content:''};
            }
        }

        function composeArticleMessage(msg){
             switch (msg.type) {
                case 10:
                    return {title: '任务开始，请按要求完成任务', content: "您刚刚开启了“" + msg.task_name + "”任务，在任务结束前按要求完成阅读人数即获得相应奖励！"};
                case 13:
                    return {title:'任务结束，奖励已到账', content: '“' + msg.task_name + '”任务已结束，转发奖励" + msg.amount + "元已经发放, 请查询您的收入！'};
                default:
                    return {title:'任务开始', content:''};
             }
        }

        function composeMessage(msg, type){
            var title = '', content = '';
            if(type == 1){
                var result = composeAppMessage(msg);
                title = result.title;
                content = result.content;
            }else if(type == 4){
                var result = composeArticleMessage(msg);
                title = result.title;
                content = result.content;
            }else {
                title = msg.title || msg.content;
                content = msg.content || '';
            }

            return ' <li class="content">' +
                    '<h1>' + title + '</h1>' +
                    '<span>' + (msg.time || msg.create_time) + '</span>' +
                    (content && content != title ? ('<p>' + content + '</p>') : '') +
                    '</li>';
        }

         $(document).bind("pageshow", function() {
            var type = 1;
            var pos = location.href.indexOf('type=');
            if(pos > 0){
                var type = parseInt(location.href.substring(pos + 5)) || 1;
            }
            $('.nav li').each(function(){
                $(this).data('type') == type && $(this).click();
            });
         });
    });
</script>
<#include "/common/scroll_footer.ftl">
