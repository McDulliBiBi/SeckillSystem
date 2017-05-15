/**
 * Created by Administrator on 2017/5/14.
 */
//存放主要交互逻辑代码
    //要模块化,类似java的包
//seckil.detail
var seckill={
    //封装秒杀相关ajaxa的url
    URL:{
        now:function(){
            return '/seckill/time/now';
        },
        exposer:function(seckillId){
            return '/seckill/' + seckillId + '/exposer';
        },
        execution:function(seckillId, md5){
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }

    },
    validatePhone:function(phone){
        if(phone && phone.length==11 && !isNaN(phone)){ //isNaN非数字是true,是数字则false
            return true;
        }else{
            return false;
        }
    },
    handleSeckill:function(seckillId, node){
        //处理秒杀函数
        //获取秒杀地址，控制显示逻辑，执行秒杀
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function(result){
            //在回调函数中执行交互流程
            if(result && result['success']){
                var exposer = result['data'];
                if(exposer['exposed']){
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log('killUrl : '+ killUrl);
                    $('#killBtn').one('click', function(){
                        //不用click,因为click是一直在绑定点击事件。但是实际操作中，点击多次只需要进入一次方法就好
                        //one只绑定一次点击事件
                        //执行秒杀请求的操作
                        //1.禁用按钮
                        $(this).addClass('disabled');
                        //2.发送秒杀的请求,执行秒杀
                        $.post(killUrl, {}, function(result){
                            if(result /*&& result['success']*/){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateinfo'];
                                //3.显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();

                }else{
                    //未开启秒杀.客户端的计算机若运行时间过长，会与服务器的计算机的事件有偏差。
                    //所以，出现这种情况时，获取exposer中的时间，重新调用倒计时函数，显示倒计时
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新计算计时逻辑
                    seckill.countdown(seckillId, now, start, end);
                }

            }else{
                console.log(result);
            }
        });

    },
    countdown:function(seckillId, nowTime, startTime, endTime){
        var seckillBox = $('#seckill-box');
        //时间判断
        if(nowTime > endTime ){
            //秒杀结束
            seckillBox.html('秒杀结束');
        }else if (nowTime < startTime){
            //秒杀未开始,计时开始,计时事件绑定
            var killTime = new Date(startTime+1000); //防止计时时时间偏移
            seckillBox.countdown( killTime,function(event){
                var format = event.strftime('秒杀倒计时： %D天 %H时 %M分 %S秒');
                seckillBox.html(format);

            }).on('finish.countdown', function(){
                //时间完成后回调事件
                //获取秒杀地址，控制显示逻辑，执行秒杀
                seckill.handleSeckill(seckillId, seckillBox);
            });
        }else{
            //秒杀开始
            seckill.handleSeckill(seckillId, seckillBox);

        }

    },
    //详情页秒杀逻辑
    detail:{
        //详情页初始化
        init:function(params){
            //手机验证登陆，计时交互
            //规划交互流程
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            //验证手机号
            if(!seckill.validatePhone(killPhone)){
                //绑定手机号，跳出弹出层，
                //控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show:true, //显示弹出层
                    backdrop:'static',//禁止位置关闭
                    keyboard:false //关闭键盘事件
                });
                $('#killPhoneBtn').click(function(){
                    var inputPhone = $('#killPhoneKey').val();
                    console.log(inputPhone);
                    if(seckill.validatePhone(inputPhone)){
                        //电话写入cookie
                        $.cookie('killPhone', inputPhone, {expires:7, path:'/seckill'});//phone只在、seckill这个path下生效
                        //验证通过，则刷新页面
                       window.location.reload();
                    }else{
                        //验证不通过
                        //取到killPhoneMessage模态框，先隐藏，然后显示，show(300)表示在300秒内显示，延时显示，会有一个动态的效果
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(300);
                    }
                });
            }
            //已经登录
            //计时交互
            var seckillId = params['seckillId'];
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            $.get(seckill.URL.now() , {}, function(result){
                if(result && result['success']){
                    var timeNow = result['data'];
                    //时间判断，根据系统时间
                    seckill.countdown(seckillId,timeNow,startTime,endTime );
                }else{
                    console.log(result);
                }
            });
        }
    }
}
