$(document).ready(function(){
	
	//验证浏览器版本
	checkBrowerVersion();
	
	$('#formLogin').validate({
		errorClass: "help-block text-left animation-slideDown",
		errorElement: "span",
		errorPlacement: function(e, a) {
			a.parents(".form-group").append(e)
		},
		highlight: function(e) {
			$(e).closest(".form-group").removeClass("has-success has-error").addClass("has-error"),
			$(e).closest(".help-block").remove()
		},
		success: function(e) {
			e.closest(".form-group").removeClass("has-success has-error"),
			e.closest(".help-block").remove()
		},
        rules: {
        	userName: {
                required: true,
            },
            userPwd: {
                required: true
            },
            securityCode: {
                required: true
            }
        },
		messages: {
			userName: {
				required:"请输入用户名",
			},
			userPwd: {
				required:"请输入密码",
			},
			securityCode: {
				required:"请输入验证码",
			}
		},
        invalidHandler: function (event, validator) { //验证前错误提示             
        },
        submitHandler: function (form) {  //验证完成后操作
			var od=$('#formLogin').data();
			var options = { 
				target:'#ajax_tips', //后台将把传递过来的值赋给该元素
				url:od.url, //提交给哪个执行
				type:'POST', 
				success: function(result){
					if(result.success){
						location.href=ctx+"/home";
					}else{
						$("#securityCode").val('');
						refushcode();	//重刷验证码
						resultErr(result);
					}
				}
			};
			$('#formLogin').ajaxSubmit(options); 
			return false;
        }
    });
});

//验证浏览器版本
function checkBrowerVersion(){
    var myos = appInfo();
    var ieversion=myos.version;
    //判断浏览器是IE
    if ( myos.msie ){
        if(ieversion<9.0){
            messageBox("推荐使用IE9及以上版本进行访问！请不要使用兼容模式。");
        }
    }
}

function appInfo(){
    var browser = {
            msie: false, firefox: false, opera: false, safari: false, 
            chrome: false, netscape: false, appname: 'unknown', version: 0
        },
        userAgent = window.navigator.userAgent.toLowerCase();
    if ( /(msie|firefox|opera|chrome|netscape)\D+(\d[\d.]*)/.test( userAgent ) ){
        browser[RegExp.$1] = true;
        browser.appname = RegExp.$1;
        browser.version = RegExp.$2;
    } else if ( /version\D+(\d[\d.]*).*safari/.test( userAgent ) ){ // safari
        browser.safari = true;
        browser.appname = 'safari';
        browser.version = RegExp.$2;
    }
    return browser;
}