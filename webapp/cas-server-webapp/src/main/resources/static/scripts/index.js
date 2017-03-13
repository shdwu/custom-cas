$(function () {
    // 图片轮播
    var index = 0,
        timer;
    var imgWidth = $('#banner .imagebox>div').eq(0).width();
    var imgNum = $('#banner .imagebox>div').length;
    var bannerWidth = $('#banner').width();
    $('.imagebox .lbdiv').css({'width': bannerWidth, 'height': 500});

    $('#banner .imagebox').css({
        'width': imgNum * bannerWidth
    });

    for (var i = 0; i < imgNum; i++) {
        $('<div></div>').appendTo($('.pages'));
    }
    $('.pages>div').on('mouseenter', function () {
        index = $('.pages>div').index(this);
        // console.log(index);
        move();
    }).eq(0).trigger('mouseenter');

    $('.container').hover(function () {
        clearInterval(timer);
    }, function () {
        timer = setInterval(function () {
            move();
        }, 5000);
    }).trigger('mouseleave');

    function move() {
        var x = -1 * index * bannerWidth;
        $('.imagebox').css({'left': x});
        $('.pages>div').css({
            'backgroundColor': '#fff',
            'border': '1px solid #9b9b9b'
        }).eq(index).css({'backgroundColor': '#0099f7', 'border': '1px solid #0099f7'});
        index++;
        if (index >= imgNum) {
            index = 0;
        }
    }

    // 输入框获取、失去焦点事件
    // $('.username').on('focus', function () {
    //     $(this).css({ 'backgroundColor': '#f0f8fc' }).siblings('i').css({ 'backgroundImage': 'url(../assets/images/username_active.png)' }).parents('.uni-item').css({ 'outline': '1px solid #50a0ce', 'backgroundColor': '#f0f8fc' });
    // });
    // $('.username').on('blur', function () {
    //     $(this).css({ 'backgroundColor': '#ffffff' }).siblings('i').css({ 'backgroundImage': 'url(../assets/images/username_inactive.png)' }).parents('.uni-item').css({ 'outline': '1px solid #cccccc', 'backgroundColor': '#ffffff' });
    // });
    //
    // $('.pwd').on('focus', function () {
    //     $(this).css({ 'backgroundColor': '#f0f8fc' }).siblings('i').css({ 'backgroundImage': 'url(../assets/images/pwd_active.png)' }).parents('.uni-item').css({ 'outline': '1px solid #50a0ce', 'backgroundColor': '#f0f8fc' });
    // });
    // $('.pwd').on('blur', function () {
    //     $(this).css({ 'backgroundColor': '#ffffff' }).siblings('i').css({ 'backgroundImage': 'url(../assets/images/pwd_inactive.png)' }).parents('.uni-item').css({ 'outline': '1px solid #cccccc', 'backgroundColor': '#ffffff' });
    // });

    // 输入框获取、失去焦点事件
    $('.username').on('focus', function () {
        $(this).css({ 'backgroundColor': '#f0f8fc',}).parents('.uni-item').css({'outline': '1px solid #50a0ce' });
    });
    $('.username').on('blur', function () {
        $(this).css({ 'backgroundColor': '#ffffff'}).parents('.uni-item').css({'outline': '1px solid #cccccc'});
    });

    $('.pwd').on('focus', function () {
        $(this).css({ 'backgroundColor': '#f0f8fc',}).parents('.uni-item').css({'outline': '1px solid #50a0ce' });
    });
    $('.pwd').on('blur', function () {
        $(this).css({ 'backgroundColor': '#ffffff'}).parents('.uni-item').css({'outline': '1px solid #cccccc'});
    });
    $('.vncode').on('focus', function () {
        $(this).parents('.vcode-area').css({'outline': '1px solid #50a0ce'});
    });
    $('.vncode').on('blur', function () {
        $(this).parents('.vcode-area').css({'outline': '1px solid #cccccc'});
    });

    // 发送按钮 用户名或密码错误
    $('#sendValidateCode').on('click', function () {
        var count = 30;
        var timer = null;
        var that = this;


        if (checkUPInput()) {

            var formData = new FormData();
            formData.append('username', $('#username').val());
            formData.append('password', $('#password').val());
            formData.append('execution', $('#execution').val());
            formData.append('geolocation', $('#geolocation').val());
            formData.append('_eventId', 'sendVlidateCode');
            $.ajax({
                url: '#',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (data) {
                    if(data){
                        var data_json = eval("(" + data + ")")
                        $('.error-notice').html(data_json.errorMessage);
                        clearInterval(timer);
                        $(that).attr('disabled', false).html('发送验证码').css({'color': '#0084cf'});
                    }
                }
            });
            // 输入合法后调用发送短信方法，前端倒计时，后端发短信
            // 以下为前端代码
            $(this).attr('disabled', true).html(count + '秒后重新发送').css({'color': '#ccc'});
            timer = setInterval(function () {
                count--;
                if (count === 0) {
                    clearInterval(timer);
                    $(that).attr('disabled', false).html('发送验证码').css({'color': '#0084cf'});
                } else {
                    $(that).html(count + '秒后重新发送');
                }
            }, 1000);
        }


    });

    // 登录按钮
    $('#login').on('click', function () {
        // 检查输入
        if (checkUPInput() && checkVCode()) {
            // 登录方法
            $('#fm1').submit();
        }
    });

    /**
     * 检查用户输入是否合法
     * @method checkUPInput
     * @return {Boolean}     合法true,否则false
     */
    function checkUPInput() {
        if ($('.username').val().length === 0) {
            $('.error-notice').html('请输入用户名');
            $('.username').focus();
            return false;
        } else if ($('.pwd').val().length === 0) {
            $('.error-notice').html('请输入密码');
            $('.pwd').focus();
            return false;
        } else if (/[~'!@#$%</>{}^&*()-+=:]/.test($('.username').val())) {
            $('.error-notice').html('用户名不得包含非法字符');
            $('.username').focus();
            return false;
        } else {
            $('.error-notice').html('');
            return true;
        }
    }

    /**
     * 检查4位验证码
     * @method checkVCode
     * @return {Boolean}   合法true,否则false
     */
    function checkVCode() {
        if (!/^\d{4}$/.test($('.vncode').val())) {
            $('.error-notice').html('请输入4位数字验证码');
            $('.vncode').focus();
            return false;
        } else {
            $('.error-notice').html('');
            return true;
        }
    }
});
