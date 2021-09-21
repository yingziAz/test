var Login = function(){
	//记住我
	var cookie = document.cookie;
	var checkFlg = false;

	$("#rememberMe").click(function(){
		if(!checkFlg){
			$("#rememberMe").attr("checked", true);
		}else{
			$("#rememberMe").attr("checked", false);
		}
		checkFlg = !checkFlg;
	});

	//初始化获取cookie
	function getCookie(){
		if($.cookie("rememberMe") == "true"){
			$("#rememberMe").attr("checked", true);
			$("#userName").val($.cookie("userName"));
		}
	}
	

	function setCookie(){
		if(checkFlg){
			var userName = $("#userName").val();
			$.cookie("rememberMe", "true", {expires: 30});
			$.cookie("userName", userName, {expires: 30});
		}else{
			$.cookie("rememberMe", "false", {expires: -1});
			$.cookie("userName", '', {expires: -1});
		}
	}

	//提交信息验证
	function globalConfigValid() {
		$('#dataForm').formValidation({
			framework : "bootstrap4",
			button : {
				selector : '#btnSubmit',
				disabled : 'disabled'
			},
			icon : null,
			fields : {
				"userName" : {
					validators : {
						notEmpty : {
							message : '请输入用户名'
						}
					}
				},
				"userPwd" : {
					validators : {
						notEmpty : {
							message : '请输入密码'
						}
					}
				},
				"securityCode" : {
					validators : {
						notEmpty : {
							message : '请输入验证码'
						}
					}
				}
			},
			err : {
				clazz : 'invalid-feedback'
			},
			control : {
				valid : 'is-valid',
				invalid : 'is-invalid'
			},
			row : {
				invalid : 'has-danger'
			},
			success:{
				
			}
		}).on('success.form.fv',function(e){
	 		e.preventDefault();
	 		var $form = $(e.target),
	 			fv = $(e.target).data('formValidation');
	 		setCookie();
	 		$.ajax({
	 			url :ctx+"/login/doLogin",
	 			type : 'POST',
	 			data : $form.serialize(),
	 			dataType : "json",
	 			success: function(result){
	 				if(result.state == "ok"){
	 					setTimeout(function(){
	 						location.href = ctx+"/home";
						},500);
					}else{
						var alertId = document.getElementById('alertId');
						alertId.style.display = "block";
						$('.alert-danger > span').html(result.msg);
						refushcode();
						//加入token问题
						if(result.formToken){
							$("input[name ='formToken"+$("input[name ='formTokenSuffix']").val()+"']").val(result.formToken);
						}
					}
	 			}
	 		});
		}).on('err.form.fv',function(){
			var alertId = document.getElementById('alertId');
			alertId.style.display = "block";
			$('.alert-danger > span').html("请输入用户名，密码，验证码。");
		})
	}

	return {
		init:function(){
			getCookie();
			globalConfigValid();
		}
	}
}();

jQuery(document).ready(function() {
    Login.init();
    Site.run();
});