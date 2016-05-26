$(function(){

    //获取滚动消息
    var msgList = [], index = 0;
    function getMessage(){
        $.get('/ios/api/message/latest?r='+ Math.random(), function(result){
            result = JSON.parse(result);
            msgList = result.data;
            if(msgList.length){
                 //init marquee
                  $('#keyFrames').empty();
                 for(var i=0; i < msgList.length; ++i){
                    var content = msgList[i].content;
                    if( content.length * 14 > $('#msg-panel').width()){
                        $('#keyFrames').append('@-webkit-keyframes marquee' + i + ' { 0%   { text-indent: 5em }  100% { text-indent: -' + (content.length - 5) + 'em } }');
                    }
                 }
                 displayMessage();
            }else{
                 $('#msg-panel').css('-webkit-animation', '').css('text-indent', '0em').attr('data-type',1).text('暂无消息');
            }
        });

        setTimeout(getMessage, 30000);
    }

    //滚动展示消息
    function displayMessage(){
        if(msgList.length){
            var i = index % msgList.length;
            index += 1;
            var msg = msgList[i];
            var content = msg.content;
            var time = (content.length * 0.5 + 5);
            $('#msg-panel').css('-webkit-animation', '');
            //$('#keyFrames').text('@-webkit-keyframes marquee { 0%   { text-indent: 5em }  100% { text-indent: -' + (content.length - 5) + 'em } }');

            $('#msg-panel').css('text-indent', '0em').attr('data-type', msg.type||1).text(content);
            if(content.length * 14 > $('#msg-panel').width()){
                $('#msg-panel').css('text-indent', '5em').css('-webkit-animation', ' marquee' + i + ' ' + time + 's linear ');
            }else {
                msgList.length > 1 && setTimeout(displayMessage, 5000);
            }
        }else {
            $('#msg-panel').text('暂无消息');
        }
    }

    //获取首页信息，是否在菜单上展示new
    function getHomeInfo(){
        $.get('/ios/api/homeinfo', function(result){
            var data = JSON.parse(result);
            var appMenu = $('.menu20');
            var artMenu = $('.menu22');
            var checkinMenu = $('.menu21');

            data.doingNum ? appMenu.append('<em>' + data.doingNum + '</em>') : (data.newApp && appMenu.append('<em>new</em>'));
            data.doingNum ? $('.menu_task').append('<span>' + data.doingNum + '</span>') : (data.newApp && $('.menu_task').append('<span>new</span>'));
            data.newArticle && artMenu.append('<em>new</em>');
            data.hasCheckin || checkinMenu.append('<em>new</em>');
            $('.menu28').append('<em>new</em>');
        });
    }

    $('.menu22').click(function(){
        $.cookie('last_article_time', parseInt(new Date().getTime() / 1000), 30 * 86400);
    });

    $('#tishi-area').click(function(){
        $.openUrl('/ios/message.html?type=' + ($('#msg-panel').data('type') || 1));
    });

    $('#msg-panel').on('webkitAnimationEnd', function(){
        displayMessage();
    });

    //关闭推广活动弹窗
    $('#activity-dialog .close_btn, #activity-enter').click(function(){
        $('#activity-dialog').hide();
        $.cookie('hide4Activity', 1, 2 * 3600);
    });

    //关闭一元夺宝的弹窗
    $('#duobao-dialog .duobao_btn,#duobao-dialog .duobao_close').click(function(){
        $('#duobao-dialog').hide();
        $.cookie('hideDuobao', 1, 86400);
        $(this).hasClass('duobao_btn') && $.openUrl('/doubao/index.html');
    });

    //新人红包
    $("#first-btn").click(function(){
        $("#nubie-dialog .first_box").hide();
        $("#nubie-dialog .first_reward").show();
        $('#user-balance').text(user_balance);
        $('#today-income').text('今日收入：'+today_income);
        $('#total-income').text('累计收入：'+total_income);
    });
    $("#begin").click(function(){
        $("#nubie-dialog").hide();
    });


    //收徒活动弹窗
    $('#shoutu-dialog').on('click', '.colse_box', function(){
        $('#shoutu-dialog').hide();
        $.cookie('hd_shoutu', 1, 3600);
    }).on('click', '.goto-rank', function(){
        $('#shoutu-dialog').hide();
        $.cookie('hd_shoutu', 1, 3600);
        $.openUrl('/ios/activity/invite.html');
    }).on('click', '.goto-share', function(){
        $('#shoutu-dialog').hide();
        $.cookie('hd_shoutu', 1, 3600);
        $.openUrl('/ios/share.html');
    });


    function initKefu(){
        $('header .back').replaceWith('<a class="kefu"><img width="100%" src="/static/images/img/kefu.png"/>客服</a>');
        $('header').on('click', '.kefu', function(){
            $.openApp('mqqwpa://im/chat?chat_type=wpa&uin=2815883901&version=1&src_type=web&web_src=');
        });
    }


    //新用户输入邀请码
    var isDoInvited = 0;
    var inputInviteCount = 0;
    var message = '';
    $('#no_invite_code,#has_invite_code').click(function(){
        var tm = parseInt($("#invite_mentor_id").attr('tm'));
        var now = new Date().getTime();
        if(tm > 0 && (now - tm) < 1000){
            return ;
        }
        $("#invite_mentor_id").attr('tm', now);
        if(isDoInvited > 0){
            alert(message);
            return ;
        }
        var invite = $("#invite_code_inp").val().trim();
        if( $(this).attr('id')=='has_invite_code'){
            if(!invite){
                alert('亲,请输入邀请码!');
                return;
            }else if(inputInviteCount >= 5){
                alert('你输入的次数过多,建议你走没有邀请码');
                return ;
            }
        }
        var hasInvitre = $(this).attr('id')=='has_invite_code'?1:2;
        doInvite(invite,hasInvitre);
    });


    $("#invite_mentor_id b").click(function(){
        $(".bg").hide();
    });

    $('#shoutu-repair-dialog').on('click', '#sure-btn', function(){
        if($('#shoutu-repair-dialog').data('type')) {
            $('#shoutu-repair-dialog').hide();
        }else {
            $('#invite_mentor_id').show().prev('div').hide();
        }
    });

    //验证用户输入的邀请码
    function doInvite(invite,hasInvitre){
        var url = '/ios/api/invite/code';
        if($('#shoutu-repair-dialog').size()){
            url = '/ios/api/invite/re_input_code';
            hasInvitre == 2 && $.myConfirm('您确定没有师傅？', function(){
                 $.cookie('no_invitor', 1);
                 $('#shoutu-repair-dialog').hide();
                 return;
            });
        }

        $.post(url, {invite_code:invite,hasInvitre:hasInvitre}, function(result){
            var data = JSON.parse(result);
            if(data ){
                message = data.message;
                if(data.code == 0){
                    //message = '恭喜您,获得<i class="red">'+data.data.amount+'</i>元师徒奖励';
                    //isDoInvited ++ ;
                    $('#invite_mentor_id').parent().addClass('dn');
                    $('#first_reward_top_id').removeClass('dn');
                    $('#reward_message').html(data.data.amount+'<i>元</i>');
                    today_income=data.data.amount;
                    total_income=today_income;
                    user_balance=total_income;
                }
                if(data.code == -2){
                    isDoInvited ++ ;
                }
                inputInviteCount++;
                $('#invite_mentor_id b').removeClass('dn');
                $("#invite_mentor_text").html(message);
            }
        });
    }

    getHomeInfo();
    getMessage();


    //弹出新用户弹窗
    $('#nubie-dialog').show();

    //弹出收徒的弹窗（与新用户弹窗不会并存）
    $.cookie('hd_shoutu') || $('#shoutu-dialog').show();

    initKefu();
    
 });

function onWSReady(){
    //ready
}

function onWSRuntimeError(error){
    if(error > 0){
        showDialog('系统检测到钥匙运行异常，请重新启动钥匙，从钥匙里点击“返回秒赚”', '启动钥匙', function(){
            $.openUrl('/www/downloads/safari');
            return false;
        });
    }
}
