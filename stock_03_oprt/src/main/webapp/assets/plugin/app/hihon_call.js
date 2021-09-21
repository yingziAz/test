﻿var $btnCallout;//拨出按钮
var $btnHandup;//挂断按钮
var $btnMute;//闭音按钮
var $btnHold;//保留按钮
var ctx = '';
var listeningFlg = false;

//初始化
function init(){

	$btnCallout = $("#calloutBtn");
	$btnHandup = $("#handupBtn");
	$btnMute = $("#muteBtn");
	$btnHold = $("#holdBtn");
	
	var lAudioDeviceID = 0;
	$btnCallout.bind("click", function(){onclickCallOut(lAudioDeviceID);});
	$btnHandup.bind("click", function(){onclickOnHand(lAudioDeviceID);});
	$btnMute.bind("click", function(){onclickSwitchMute(lAudioDeviceID);});
	$btnHold.bind("click", function(){onclickSwitchHold(lAudioDeviceID);});
	UsbPhone.Init();
	//0->挂机,1->摘机,2->失败；
	if(UsbPhone.GetPhoneState(lAudioDeviceID) == 0) {	//挂机状态
		$("#phoneStatus").val(0);
		
		$btnHandup.attr("disabled", true);
		$btnMute.attr("disabled", true);
		$btnHold.attr("disabled", true);
	} else if(UsbPhone.GetPhoneState(lAudioDeviceID) == 1) {	//摘机状态
		$("#phoneStatus").val(1);

		$btnCallout.attr("disabled", true);
	} else {
		$btnHandup.attr("disabled", true);
		$btnMute.attr("disabled", true);
		$btnHold.attr("disabled", true);
	}
}

var inComingFlg = false;
var itv;
var ringArray = new Array();
// 周期行判断是否有电话进入
function handleInComingFlg(){
	var times = 0;
	var len = 0;
	itv = setInterval(function(){ 
		if(len == ringArray.length){
			times ++;
			if(times > 9){
				clearInterval(itv);
				ringArray.splice(0,ringArray.length);
				inComingFlg = false;
				$('#inComingHandleBtns').html('<span class="btn"><i class="icon-call-end"></i> 已挂断 </span>');
			}
		}
		if(len != ringArray.length){
			times = 0;
		}
		len = ringArray.length;
	}, 500);
}

//贝恩呼叫
/**
1.OnIncomingPhone事件
函数原型：void OnIncomingPhone(long lAudioDeviceID, LPCTSTR IncomingNum);
功能说明：指定的终端设备收到新来电；
参数说明：lAudioDeviceID->坐席号；
       IncomingNum->收到的来电号码；
       普通的号码长度>1；如果长度是1，号码是2，那代表“出局”，号码是1，那代表“保密”。
*/
function OnIncomingPhone(lAudioDeviceID,IncomingNum){
	////console.log("------OnIncomingPhone--->"+lAudioDeviceID+"------>"+IncomingNum);
	//防止同一通电话反复弹出
	
	if(inComingFlg){
		return;
	}
	
	inComingFlg = true;
	
	handleInComingFlg();
	
	var lAudioDeviceID = 0;
	if(ctx === undefined){
		ctx="";
	}
	//验证是否为健康助理的用户的手机号
	$.ajax({
		type: "post",
		url: ctx+"/base/haCallRecord/indentify",
		data: {"phoneNum":IncomingNum},
		dataType: "json",
		success: function(result){
			if(result.data.status==0){
				var data = result.data.data;
				
				showIncomingPhoneInfo(IncomingNum, data);
				$.ajax({
					type: "post",
					url: ctx+"/base/haCallRecord/create",
					data: {"customerId":data.id, "callInitiator":"C"},
					dataType: "json",
					success: function(result){
						if(result.data.status == 0){
							var data = result.data.data;
							$("#callRecordId").val(data.callRecordId);
						}else{
							showBnInfo("通话日志记录异常！");
						}
					}
				});
			}else{
			}
		}
	});
}

/** 点击号码直接拨打 */
//cardId , 卡片ID
function clickPhoneNumCallOut(customerId,cardId){
	
	var lAudioDeviceID = 0;
	//号码默认前缀+0
//	phoneNum = "0" + phoneNum;
	//通话记录生成
	$.ajax({
		type: "post",
		url: ctx+"/base/haCallRecord/create",
		data: {"customerId":customerId, "callInitiator":"H","cardId":cardId},
		dataType: "json",
		success: function(result){
			if(result.data.status == 0){
				var data = result.data.data;
				var phoneNum = data.mobile;
				if(UsbPhone.Dial(lAudioDeviceID, phoneNum) != 0) {
					showBnInfo("呼出成功：" + "号码:" + phoneNum );
				} else {
					showBnInfo("呼叫失败：" + "号码:" + phoneNum );
				}
				$("#callRecordId").val(data.callRecordId);
			}else{
				showBnInfo("通话日志记录异常！");
			}
		}
	});
}

/**
开始录音

19.StartRecordFile方法
函数原型：short StartRecordFile(long lAudioDeviceID, LPCTSTR strFileName, short nType);
功能说明：在指定的终端设备上开始录音操作；
参数说明：lAudioDeviceID->坐席号；
    strFileName->录音文件名，可以是文件名,也可以是完整的路径,如:"sound.wav"或"C:\\record\\sound.wav"。
                 当没有指定路径时,录音文件保存在当前目录下；
    nType->录音类型：0:本地录音；1:通话录音；2:留言录音；
返回值：成功则返回1，否则返回0；
*/
function clickRecordFile(id){
	if(!listeningFlg){
		var lAudioDeviceID = 0;
		var strFileName = "C:\\SF_TEDERIC\\records\\"+id+".wav";
		var nType = 1;
		if(UsbPhone.StartRecordFile(lAudioDeviceID, strFileName,nType) != 0){
//			console.log("开始录音");
			listeningFlg = true;
		}
	}
	
}

/**
      结束录音

20.StopRecord方法
函数原型：short StopRecord(long lAudioDeviceID);
功能说明：在指定的终端设备上停止录音；
参数说明：lAudioDeviceID->坐席号；
返回值：成功则返回1，否则返回0；
*/
function clickStopRecord(){
	var lAudioDeviceID = 0;
	if(UsbPhone.StopRecord(lAudioDeviceID) != 0){
		if($("#callRecordId").val() != ""){
			if(listeningFlg){
				var objShell;
				if(objShell==undefined){
					objShell=new ActiveXObject("WScript.Shell");
				}
				var iReturnCode=objShell.Run("C:/SF_TEDERIC/script/upload.bat",0,false);
				console.log(iReturnCode);
				listeningFlg=false;
			}
		}
	}
}

/** 点击摘机（接听） */
function onclickCallIn(lAudioDeviceID){
	showBnInfo('');
	if($("#phoneStatus").val() != 0){
		showBnInfo("设备状态异常！");
		return;
	}
	UsbPhone.Init();
	UsbPhone.OffHand(lAudioDeviceID);
}

/** 输入号码拨打电话 */
function onclickCallOut(lAudioDeviceID){
	showBnInfo('');
	var callOutNum = $("#phoneNum").val();
	if(callOutNum.length < 1){
		showBnInfo("号码错误！");
		return;
	}
	if($("#phoneStatus").val() != 0){
		showBnInfo("设备状态异常！");
		return;
	}
	console.log(111111);
	$btnCallout.attr("disabled", true);
	//通话记录生成
	$.ajax({
		type: "post",
		url: ctx+"/system/callLog/create",
		data: {"phoneNum":callOutNum, "callType":"O"},
		dataType: "json",
		success: function(result){
			if(result.state=="ok"){
				var data = result.data;
				if(data > 0){
					UsbPhone.Init();
					if(UsbPhone.Dial(lAudioDeviceID, callOutNum) != 0) {
						$btnHandup.attr("disabled", false);
						$btnMute.attr("disabled", false);
						$btnHold.attr("disabled", false);
						showBnInfo("呼出成功：" + "号码:" + callOutNum );
					} else {
						showBnInfo("呼叫失败：" + "号码:" + callOutNum );
					}
				}
			}else{
				showBnInfo("通话日志记录异常！");
			}
		}
	});
}

/** 挂断电话 */
function onclickOnHand(lAudioDeviceID){
	UsbPhone.Init();
	if(UsbPhone.GetPhoneState(lAudioDeviceID)==0){
		//1-执行接听，0.5秒后执行挂机
		var res = UsbPhone.OffHand(lAudioDeviceID);
		if(res != 0){
			setTimeout(function(){
				var onhandRes = UsbPhone.OnHand(lAudioDeviceID);
				showBnInfo("挂机成功!");
//				showBnInfo("挂机成功：" + "坐席:" +lAudioDeviceID);
//				clickStopRecord();
			},500);
		}
	}else{
		if(UsbPhone.OnHand(lAudioDeviceID) != 0){
			$btnCallout.attr("disabled", false);
			$btnHandup.attr("disabled", true);
			$btnMute.attr("disabled", true);
			$btnHold.attr("disabled", true);
			showBnInfo("挂机成功！");
//			showBnInfo("挂机成功：" + "坐席:" +lAudioDeviceID);
		} else {
			showBnInfo("挂机失败！");
		}  
	}
	
	
}

/** 闭音状态切换 */
function onclickSwitchMute(lAudioDeviceID){
	var muteStatus = $("#muteStatus").val();
	var bOn;
	if(muteStatus == 0){
		bOn = 1;
		$btnMute.text("关闭闭音");
	}else{
		bOn = 0;
		$btnMute.text("开启闭音");
	}
	UsbPhone.Init();
	var res = UsbPhone.Mute(lAudioDeviceID, bOn);
	if(res != 0){
		$("#muteStatus").val(bOn);
		showBnInfo("闭音" + (bOn==1?"开启":"关闭"));
	}
}

/** 保留状态切换 */
function onclickSwitchHold(lAudioDeviceID){
	var holdStatus = $("#holdStatus").val();
	var bOn;
	if(holdStatus == 0){
		bOn = 1;
		$btnHold.text("关闭保留");
	}else{
		bOn = 0;
		$btnHold.text("开启保留");
	}
	UsbPhone.Init();
	var res = UsbPhone.Hold(lAudioDeviceID, bOn);
	if(res != 0){
		$("#holdStatus").val(bOn);
		showBnInfo("保留" + (bOn==1?"开启":"关闭"));
	}
}

//消息显示
function showBnInfo(text){
	if(text != ''){
		messageBox(text)
	}
}

function showIncomingPhoneInfo(phoneNum, res){
	if(ctx === undefined){
		ctx = "";
	}
	//关闭弹窗
	layer.closeAll('page');
	var lAudioDeviceID = 0;
	var _name = res.customerName;	//来电人姓名
	var _avatar = '';//默认头像
	var _phoneNum = phoneNum;//来电号码
	if(res.alipayUserAvatar != '' && res.alipayUserAvatar != null){
		_avatar = res.alipayUserAvatar;
	} else if(res.pwAvatarUrl != '' && res.pwAvatarUrl != null){
		_avatar = res.pwAvatarUrl;
	} else if(res.userAvatar != '' && res.userAvatar != null) {
		_avatar = ctx + "/attach/download?attachId=" + res.userAvatar ;
	} else{
		_avatar = ctx + "/assets/img/user.png";
	}
	var _html = 
		'<div class="row m-b-lg" style="width:300px;">'+
        '	<div class="col-lg-4 text-center">'+
        '		<div class="m-b-sm">'+
        '			<img class="img-circle" src="'+_avatar+'" style="width: 64px;border-radius: 50% !important;margin-top:10px;margin-left:20px;">'+
        '		</div>'+
        '	</div>'+
        '	<div class="col-lg-6" style="left:20px;">'+
        '		<div style="font-size: 17px; margin-top: 10px;">'+_name+'</div>'+
        '		<div style="font-size: 17px; margin-top: 8px; margin-bottom: 20px;">'+_phoneNum+'</div>'+
        '	</div>'+
        '	<div class="col-lg-6">'+
        '		<button type="button" class="btn btn-primary btn-sm btn-block" onclick="onclickCallIn(' + lAudioDeviceID + ')" style="margin: 0px 10px;"> 接听</button>'+
        '	</div>'+
        '	<div class="col-lg-6">'+
        '		<button type="button" class="btn btn-danger btn-sm btn-block" onclick="onclickOnHand(' + lAudioDeviceID + ')" style="margin: 0px 10px;"> 挂断</button>'+
        '	</div>'+
        '</div>';
//    	console.log(_html);
	layer.open({
		type: 1,
		title: false,
		closeBtn: 2,
		shade: 0,
		area: '300px',	
		scrollbar: true,
		offset: 'rb', //右下角弹出
		anim: 2,
		content: _html,
		end: function(){
			
		}
	});
}

//-----------------------------------------------------------------------------------------------------------

/**
4.OnDeviceDetect事件
函数原型：void OnDeviceDetect事件(short bState, long lAudioDeviceID);
功能说明：指定的终端设备所连接的线路当前状态发生改变；
参数说明： lAudioDeviceID->坐席号；
          bState为1->设备插入，此时lAudioDeviceID是可操作的终端设备标识（大于等于0）；
          bState为0->设备拔出，此时lAudioDeviceID是被拔出的设备标识；

		  
function OnDeviceDetect(bState,lAudioDeviceID)
{
	var bsta;
	if(!bState){
		bsta = "设备插入";
		UsbPhone.Init();
		showBnInfo('设备初始化成功');
	}else{
		bsta = "设备拔出";
	}
	showBnInfo('坐席号:'+ lAudioDeviceID + "状态:" + bsta);
	
	//alert('设备状态:'+ bState + '工号:' + lAudioDeviceID);
}
		*/  
		  
/**		
function OnDeviceDetect(bState,lAudioDeviceID)
{
	if(bState == 1){
		showBnInfo("设备已连接");
		console.log("状态:" + bState + '坐席号:'+ lAudioDeviceID);
		showBnInfo("状态:" + bState + '坐席号:'+ lAudioDeviceID);
	}else if(bState == 0){
		var bState = 0;
		OnDeviceDetect(bState,lAudioDeviceID)
		showBnInfo('设备已断开');
		showBnInfo("状态:" + bState + '坐席号:'+ lAudioDeviceID);
	}
	//alert('设备状态:'+ bState + '工号:' + lAudioDeviceID);
}
*/ 

/**
5.OnPhoneRing事件
函数原型：void OnPhoneRing(long lAudioDeviceID, short bState);
功能说明：指定的终端设备所连接的线路当前铃声状态发生改变；
参数说明：lAudioDeviceID -> 坐席号；
       bState为 1     -> 响铃声，
       bState为 0     -> 无铃声；
*/
function OnPhoneRing(lAudioDeviceID,bState){
	//console.log("------OnPhoneRing--->"+lAudioDeviceID+"------>"+bState);
	//alert('坐席号:'+ lAudioDeviceID + '状态:' + bState );
	//var btnSetIdle = document.getElementById("btnSetIdle");
	ringArray.push(bState);
	//console.log("-----OnPhoneRing------->"+ringArray.length);
	if(bState != 0){
		//showBnInfo('正在响铃');
		//$btnSetIdle.removeAttr("disabled");
		//btnSetIdle.style.background="url(./image/Uc3_p1_27.jpg)";
		
		//$btnCallOut.attr("disabled", "disabled");
	}
}
/**
6.OnStopLiuyan事件
函数原型：void OnStopLiuyan(long lAudioDeviceID);
功能说明：指定的终端设备按“摘机”键停止留言录音；
参数说明：lAudioDeviceID->坐席号；
*/
function OnStopLiuyan(lAudioDeviceID){
	showBnInfo('指定的终端设备按“摘机”键停止留言录音:'+ lAudioDeviceID);
}
/**
7.OnMuteKey事件
OnMuteKey(long lAudioDeviceID, short bStatus);
功能说明：指定的终端设备按“静音”键；
参数说明：lAudioDeviceID->坐席号；
          bStatus为1->“静音”键开启，
          bStatus为0->“静音”键关闭；
*/
function OnMuteKey(lAudioDeviceID,bStatus){
	if(bStatus == 1){
		$btnMute.text("关闭闭音");
		$("#muteStatus").val(1);
	}else if(bStatus == 0){
		$btnMute.text("开启闭音");
		$("#muteStatus").val(0);
	}
}
/**
8.OnHoldKey事件
OnHoldKey(long lAudioDeviceID, short param);
功能说明：指定的终端设备按“保留”键；
参数说明：lAudioDeviceID->坐席号；
          param为1->“保留”键开启，
          param为0->“保留”键关闭
*/
function OnHoldKey(lAudioDeviceID,param){
	if(param==1){
		$btnMute.text("关闭保留");
		$("#holdStatus").val(1);
	}else if(param==0){
		$btnMute.text("开启保留");
		$("#holdStatus").val(0);
	}
}
/**
3.OnPhoneStateChange事件
函数原型：void OnPhoneStateChange(long lAudioDeviceID, short bState);
功能说明：指定的终端设备所连接的线路当前状态发生改变；
参数说明：lAudioDeviceID->坐席号；
       bState = 2->终端在保留的状态下按免提键退出了保留，
              = 1->改变后的状态为摘机，
              = 0->改变后的状态为挂机；
*/
function OnPhoneStateChange(lAudioDeviceID,bState){
	var sta;
	layer.closeAll('page');
	if(bState==0){
//		showBnInfo("已挂断：" + "坐席:" +lAudioDeviceID);
		showBnInfo("通话结束！");
		var callRecordId = $("#callRecordId").val();
		console.log("------------->挂断电话，callRecordId："+callRecordId);
		if(callRecordId != ""){
			$.ajax({
				type: "post",
				url: ctx+"/base/haCallRecord/finishCall",
				data: {"id":callRecordId},
				dataType: "json",
				success: function(result){
					if(result.data.status==0){
						var id = result.data.data.id;
						clickStopRecord();
						if(id != ""){
							$("#callRecordId").val("");
						}
					}else{
						showBnInfo("通话日志记录异常！");
					}
				}
			});
		}
	}else if(bState==1){
		if(itv != undefined && itv > 0){
			clearInterval(itv);
			ringArray.splice(0,ringArray.length);
			inComingFlg = false;
			itv = -1;
		}
		
		$("#phoneStatus").val(1);
		showBnInfo("通话中!");

		var callRecordId = $("#callRecordId").val();
		console.log("------------->接通电话，callRecordId："+callRecordId);
		if(callRecordId != ""){
			$.ajax({
				type: "post",
				url: ctx+"/base/haCallRecord/inCall",
				data: {"id":callRecordId},
				dataType: "json",
				success: function(result){
					if(result.data.status==0){
						var id = result.data.data.id;
						clickRecordFile(id);
						if(id != ""){
							$("#callRecordId").val(id);
						}
					}else{
						showBnInfo("通话日志记录异常！");
					}
				}
			});
		}
	}else if(bState==2){
		showBnInfo("退出保留：" + "坐席:" +lAudioDeviceID);
	}
}


/**
 注册接口事件
*/
function registerUsbPhoneEvents()
{
    var buffer = new Array();
    buffer.push("function UsbPhone::OnIncomingPhone(lAudioDeviceID,IncomingNum)");
	buffer.push("{");
	buffer.push("   OnIncomingPhone(lAudioDeviceID,IncomingNum);");
	buffer.push("};");
	buffer.push("function UsbPhone::OnDeviceDetect(bState,lAudioDeviceID)");
	buffer.push("{");
	buffer.push("   OnDeviceDetect(bState,lAudioDeviceID);");
	buffer.push("};");
	buffer.push("function UsbPhone::OnPhoneRing(lAudioDeviceID,bState)");
	buffer.push("{");
	buffer.push("   OnPhoneRing(lAudioDeviceID,bState);");
	buffer.push("};");
	buffer.push("function UsbPhone::OnStopLiuyan(lAudioDeviceID)");
	buffer.push("{");
	buffer.push("   OnStopLiuyan(lAudioDeviceID);");
	buffer.push("};");
	buffer.push("function UsbPhone::OnMuteKey(lAudioDeviceID,bStatus)");
	buffer.push("{");
	buffer.push("   OnMuteKey(lAudioDeviceID,bStatus);");
	buffer.push("};");
	buffer.push("function UsbPhone::OnHoldKey(lAudioDeviceID,param)");
	buffer.push("{");
	buffer.push("   OnHoldKey(lAudioDeviceID,param);");
	buffer.push("};");
	buffer.push("function UsbPhone::OnPhoneStateChange(lAudioDeviceID,bState)");
	buffer.push("{");
	buffer.push("   OnPhoneStateChange(lAudioDeviceID,bState);");
	buffer.push("};");
    eval(buffer.join(""));
}

/**
* myocx
  呼出
*/
/*function clickCallOut(){
	
	
	var lAudioDeviceID = 0;
	var OutNum = $.trim($("#callee").val());
	UsbPhone.Init();
	if(UsbPhone.Dial(lAudioDeviceID, OutNum) != 0 ){
		$btnCallOut.attr("disabled", "disabled");
		$btnSetIdle.removeAttr("disabled");
		$btnTalkRecIO.removeAttr("disabled");
		$btnCloseSound.removeAttr("disabled");
		$btnKeep.removeAttr("disabled");
		$btnLeave.removeAttr("disabled");
		document.getElementById("btnSetIdle").style.background="url(./image/Uc3_p1_21.jpg)";
		showBnInfo("呼出成功:" + "坐席:" +lAudioDeviceID + "号码:" + OutNum );
	}else
	{
		showBnInfo("呼叫失败:" + "坐席:" +lAudioDeviceID + "号码:" + OutNum );
	}
}*/
/**
 * 点击设置闪断时间       
 */
 function clickFlashTime(){
	var lAudioDeviceID = 0;
	var options = $("#selectFlashTime option:selected");  //获取选中的项
	var nFlashTime = options.val();
	var ntime = options.text();
	//alert(options.val());   //拿到选中项的值
	//alert(options.text());   //拿到选中项的文本
	//alert(nFlashTime);
	 //SetFlashTime(long lAudioDeviceID, short nFlashTime)
	 UsbPhone.Init();
	 if(UsbPhone.SetFlashTime(lAudioDeviceID,nFlashTime) == 1){
		showBnInfo("闪断时间设置为" + ntime); 
	 }else{
		 showBnInfo("闪断时间设置失败");
	 }
 }

/**
*点击挂机按钮
*/
function clickHangup()
{
	var lAudioDeviceID = 0;
	UsbPhone.Init();
    if(UsbPhone.OnHand(lAudioDeviceID) != 0)
	{
    	
	    $btnHangup.attr("disabled", "disabled");
		$btnCallOut.removeAttr("disabled");
		$btnSetIdle.attr("disabled", "disabled");
		$btnVOIPIO.removeAttr("disabled");
		showBnInfo("挂机成功=>" + "坐席:" +lAudioDeviceID);
		
    }
	else
	{
	    showBnInfo("挂机失败=>" + "坐席:" +lAudioDeviceID);
    }       
}
/**
 * 点击获取话机当前状态
 */
 function clickPhoneState()
 {
	var lAudioDeviceID = 0;
	if(UsbPhone.GetPhoneState(lAudioDeviceID) == 0)
	{
		showBnInfo("挂机状态");
	}
	else if(UsbPhone.GetPhoneState(lAudioDeviceID) == 1)
	{
		showBnInfo("摘机状态");
	}
	else
	{
		showBnInfo("失败");
	}
 }
 
 /**
6.OnStopLiuyan事件
函数原型：void OnStopLiuyan(long lAudioDeviceID);
功能说明：指定的终端设备按“摘机”键停止留言录音；
参数说明：lAudioDeviceID->坐席号；
*/
function OnStopLiuyan(lAudioDeviceID)
{
	//alert('指定的终端设备按“摘机”键停止留言录音:'+ lAudioDeviceID);
	showBnInfo('停止留言录音:'+ lAudioDeviceID);
}
 
 /**
 摘机
 */
function clickOffHand()
{
	var SetIdle = document.getElementById('btnSetIdle');

	var dizhi = SetIdle.style.background;
	var VOIP = dizhi.substring(4,dizhi.length-1);
	//alert(VOIP);
	var lAudioDeviceID = 0;
	
	if(VOIP == "./image/Uc3_p1_27.jpg"){
		UsbPhone.Init();
		var res = UsbPhone.OffHand(lAudioDeviceID);
		if(res != 0){
			
			//alert(' 进入摘机方法');
			/**
			$btnCallOut.attr("disabled", "disabled");//拨号
			$btnVOIPIO.attr("disabled", "disabled");// PC
			$btnTalkRecIO.removeAttr("disabled"); // 通话录音IO口
			$btnCloseSound.removeAttr("disabled");//闭音
			$btnKeep.removeAttr("disabled");//保留
			$btnLeave.removeAttr("disabled");//留言放音IO口*/
			SetIdle.style.background="url(./image/Uc3_p1_21.jpg)";
			//OnStopLiuyan(lAudioDeviceID);
			
			showBnInfo("摘机成功=>"  + "坐席:" +lAudioDeviceID);
			//alert('摘机完成');
			
		}
	//	else
	//	{
	//		showBnInfo("摘机失败");
	//	}
		
	}else if(VOIP == "./image/Uc3_p1_21.jpg"){
		UsbPhone.Init();
		var rese = UsbPhone.OnHand(lAudioDeviceID);
		if(rese != 0)
		{
			$btnCallOut.removeAttr("disabled");
			$btnVOIPIO.removeAttr("disabled");
			$btnSetIdle.attr("disabled", "disabled");
			$btnTalkRecIO.attr("disabled", "disabled"); // 通话录音IO口
			$btnCloseSound.attr("disabled", "disabled");//闭音
			$btnKeep.attr("disabled", "disabled");//保留
			$btnLeave.attr("disabled", "disabled");//留言放音IO口
			
			//document.getElementById("btnSetIdle").style.background="url(./image/Uc3_p1_27.jpg)";//摘机
			document.getElementById("btnTalkRecIO").style.background="url(./image/Uc3_p1_5.jpg)";// 通话录音io口
			
			document.getElementById("btnStartRecording").style.background="url(./image/Uc3_p1_14.jpg)";//开始录音
			document.getElementById("btnCloseSound").style.background="url(./image/Uc3_p1_15.jpg)";//闭音
			document.getElementById("btnKeep").style.background="url(./image/Uc3_p1_12.jpg)";// 保留
			document.getElementById("btnLeave").style.background="url(./image/Uc3_p1_10.jpg)";//留言放音
			
			SetIdle.style.background="url(./image/Uc3_p1_27.jpg)";
			showBnInfo("挂机成功=>" + "坐席:" +lAudioDeviceID);
		}
		else
		{
			showBnInfo("挂机失败=>" + "坐席:" +lAudioDeviceID);
		}   
	}
} 	
	
	
   
	/**
		if(UsbPhone.OffHand(lAudioDeviceID) != 0)
		{
			showBnInfo("摘机成功");
			OnStopLiuyan(lAudioDeviceID);
			$btnSetIdle.attr("disabled", "disabled");
			$btnHangup.removeAttr("disabled");
			
			$btnStartRecording.removeAttr("disabled");
			$btnVOIPIO.attr("disabled", "disabled");
			//$btnTalkRecIO.
		}
		else
		{
			showBnInfo("摘机失败");
		}
		*/


/**
6.Flash方法
函数原型：short Flash(long lAudioDeviceID, short nFlashTime);
功能说明：控制指定的终端设备进行拍叉操作并准备下一次通话。
参数说明：lAudioDeviceID->终端设备标识；
          nFlashTime->Flash操作的时间长度,取值为0--100ms,1--180ms,2--300ms,3--600ms,4--1000ms之间。
返回值： 成功则返回1, 否则返回0；
*/
function clickFlash(){
	var lAudioDeviceID = 0;
	var nFlashTime = 3;
	UsbPhone.Init();
	if(UsbPhone.Flash(lAudioDeviceID,nFlashTime)!=0){
		showBnInfo("闪断成功");
	}else{
		showBnInfo("闪断失败");
	}
}
/**

  特色铃声                             
7.RingSet方法
函数原型：short RingSet(long lAudioDeviceID, short nRing);
功能说明：控制指定终端设备进行特色铃声的设定，检测到设备连接时要使用该方法；
参数说明：lAudioDeviceID->坐席号；
          nRing->1:打开特色铃声；
                 0:关闭特色铃声；
返回值：成功则返回1，否则返回0；
*/
function clickRingSet(){
	
	var Bill = document.getElementById('btnBill');
	//alert(dd.style.background);
	var dizhi = Bill.style.background;
	var VOIP = dizhi.substring(4,dizhi.length-1);
	//alert(dizhi.substring(5,dizhi.length-2)); ivalue.style.background="url(./image/Uc3_p1_1.jpg)";
	
	var lAudioDeviceID = 0;
	var ivalue = $btnBill.val();
	if(VOIP == "./image/Uc3_p1_9.jpg"){
		var nRing = 1;
		UsbPhone.Init();
		var res = UsbPhone.RingSet(lAudioDeviceID,nRing);
		if(res != 0){
			showBnInfo("打开特色铃声");
			//$btnBill.val("关闭特色铃声");
			Bill.style.background="url(./image/Uc3_p1_8.jpg)";
		}
	}else if(VOIP == "./image/Uc3_p1_8.jpg"){
		var nRing = 0;
		UsbPhone.Init();
		var res = UsbPhone.RingSet(lAudioDeviceID,nRing);
		if(res != 0){
			showBnInfo("关闭特色铃声");
			//$btnBill.val("打开特色铃声");
			Bill.style.background="url(./image/Uc3_p1_9.jpg)";
		}
	}

}
/**
9.SetTalkRecIO方法
函数原型：short SetTalkRecIO(long lAudioDeviceID, short nStatus);
功能说明：控制指定终端设备进行通话录音的io口设定，仅在摘机状态改变，方有用；
参数说明：lAudioDeviceID->坐席号；
          nStatus->1:打开通话录音io口；
                   0:关闭通话录音io口；
返回值：成功则返回1，否则返回0；
*/
function clickTalkRecIO()
{
	var Recording = document.getElementById('btnTalkRecIO');
	//alert(dd.style.background);
	var dizhi = Recording.style.background;
	var VOIP = dizhi.substring(4,dizhi.length-1);
	//alert(dizhi.substring(5,dizhi.length-2));
	
	
	
	var ivalue = $btnTalkRecIO.val();
	var lAudioDeviceID = 0;
	if(VOIP == "./image/Uc3_p1_5.jpg"){
		var nStatus = 1;
		UsbPhone.Init();
		var res = UsbPhone.SetTalkRecIO(lAudioDeviceID,nStatus);
		if(res != 0){
			showBnInfo("打开通话录音io口");
			$btnStartRecording.removeAttr("disabled");
			//$btnTalkRecIO.val("关闭通话录音IO口");
			Recording.style.background="url(./image/Uc3_p1_3.jpg)";
		}
	}else if(VOIP == "./image/Uc3_p1_3.jpg"){
		var nStatus = 0;
		UsbPhone.Init();
		var res = UsbPhone.SetTalkRecIO(lAudioDeviceID,nStatus);
		if(res != 0){
			showBnInfo("关闭通话录音io口");
			//$btnStartRecording.attr("disabled","disabled");
			//$btnTalkRecIO.val("打开通话录音IO口");
			Recording.style.background="url(./image/Uc3_p1_5.jpg)";
		}
	}
	
}


/**
    设置出局码
18.SetOutcode方法
函数原型：short SetOutcode(long lAudioDeviceID, LPCTSTR outcode);
功能说明：在指定的终端设备上设置出局码，检测到设备连接时要使用该方法；
参数说明：lAudioDeviceID->坐席号；
          outcode->出局码，最多 3位，没有出局码就是空字符串；
返回值：成功则返回1，否则返回0；
*/
function clickOutcode(){

	var lAudioDeviceID = 0;
	var outcode = $("#outcode").val();  //获取选中的项
	//alert(outcode);
	UsbPhone.Init();
	if(UsbPhone.SetOutcode(lAudioDeviceID,outcode) != 0){
		showBnInfo("设置出局码成功,出局码:" + outcode);
	}else {
		showBnInfo("设置出局码失败");
	}
}

/**

13.Mute方法
函数原型：short Mute(long lAudioDeviceID, short bOn);
功能说明：在指定的终端设备上进行闭音操作；
参数说明：lAudioDeviceID->坐席号；
          bOn->1:开启闭音；
               0:关闭闭音；
返回值：成功则返回1，否则返回0；
*/
function clickCloseSound()
{
	var CloseSound = document.getElementById('btnCloseSound');
	//alert(dd.style.background);
	var dizhi = CloseSound.style.background;
	var VOIP = dizhi.substring(4,dizhi.length-1);
	
	
	
	var ivalue = $btnCloseSound.val();
	var lAudioDeviceID = 0;
	if(VOIP == "./image/Uc3_p1_15.jpg"){
		var bOn = 1;
		UsbPhone.Init();
		var res = UsbPhone.Mute(lAudioDeviceID,bOn);
		if(res != 0){
			showBnInfo("开启闭音");
			//$btnCloseSound.val("关闭闭音");
			//CloseSound.style.background="url(./image/Uc3_p1_16.jpg)";
		}
	}else if(VOIP == "./image/Uc3_p1_16.jpg"){
		var bOn = 0;
		UsbPhone.Init();
		var res = UsbPhone.Mute(lAudioDeviceID,bOn);
		if(res != 0){
			showBnInfo("关闭闭音");
			//$btnCloseSound.val("开启闭音");
			CloseSound.style.background="url(./image/Uc3_p1_15.jpg)";
		}
	}
}



 /**
   设置自动接听
16.SetAutoAnswer方法 
函数原型：short SetAutoAnswer(long lAudioDeviceID, short bOn);
功能说明：在指定的终端设备上设置自动接听，检测到设备连接时要使用该方法；
参数说明：lAudioDeviceID->坐席号；
          bOn->1:开启自动接听；
               0:关闭自动接听；
返回值：成功则返回1，否则返回0；
*/
function clickAutoAnswerOpen()
{
	//var Answer = document.getElementById('btnAnswer');
	//alert(dd.style.background);
	//var dizhi = Answer.style.background;
	//var VOIP = dizhi.substring(4,dizhi.length-1);
	//Keep.style.background="url(./image/Uc3_p1_11.jpg)";
	
	var lAudioDeviceID = 0;
	var ivalue = $btnAnswer.val();
	if(VOIP == "./image/Uc3_p1_4.jpg"){
		var bOn = 1;
		UsbPhone.Init();
		var res = UsbPhone.SetAutoAnswer(lAudioDeviceID, bOn);
		if(res != 0){
			showBnInfo("开启自动接听");
			//$btnAnswer.val("取消自动接听");
			Answer.style.background="url(./image/Uc3_p1_2.jpg)";
		}
	}else if(VOIP == "./image/Uc3_p1_2.jpg"){
		var bOn = 0;
		UsbPhone.Init();
		var res = UsbPhone.SetAutoAnswer(lAudioDeviceID, bOn);
		if(res != 0){
			showBnInfo("取消自动接听");
			//$btnAnswer.val("开启自动接听");
			Answer.style.background="url(./image/Uc3_p1_4.jpg)";
		}
	}
}

/*background:#FF0000;  background:#00FF00;   no-repeat left top  
 * addEventListener:监听Dom元素的事件 
 *   
 *  target：监听对象 
 *  type：监听函数类型，如click,mouseover 
 *  func：监听函数 
 */
function addEventHandler(target,type,func){
    if(target.addEventListener){
        //监听IE9，谷歌和火狐  
        target.addEventListener(type, func, false);  
    }else if(target.attachEvent){  
        target.attachEvent("on" + type, func);  
    }else{  
        target["on" + type] = func;
    }
}
/* 
 * removeEventHandler:移除Dom元素的事件 
 *   
 *  target：监听对象 
 *  type：监听函数类型，如click,mouseover 
 *  func：监听函数 
 */
function removeEventHandler(target, type, func) {  
    if (target.removeEventListener){  
        //监听IE9，谷歌和火狐  
        target.removeEventListener(type, func, false);  
    } else if (target.detachEvent){  
        target.detachEvent("on" + type, func);  
    }else {  
        delete target["on" + type];  
    }  
}
/**
var OnIncomingPhone = function(lAudioDeviceID, IncomingNum){
	alert("Incoming Phone Number : "+IncomingNum);
}
*/
var OnDeviceDetect = function(bState, lAudioDeviceID){
	if(!bState){
		showBnInfo("设备已拔出!");
	}else{
//		showBnInfo("设备已连接!");
	}
}

//解决 IE8 不支持console
window.console = window.console || (function () {
    var c = {}; c.log = c.warn = c.debug = c.info = c.error = c.time = c.dir = c.profile
    = c.clear = c.exception = c.trace = c.assert = function () { };
    return c;
})();


$(window).load(function(){
    if(window.ActiveXObject || "ActiveXObject" in window){
        $(".showIfIE").each(function(){
            $(this).show();
        });
        var hionOcx = document.getElementById("UsbPhone");
        try{
          //注册北恩话机事件
          registerUsbPhoneEvents();
          addEventHandler(hionOcx,"DeviceDetect",OnDeviceDetect);
          
          
          var hionDev = hionOcx.Init(); //设备初始化
          if(hionDev < 0){
            return false;
          }
          
          //初始化
          init();
        } catch(error) {
          console.error(error);
        }
    }else{
        $(".showIfIE").each(function(){
            $(this).hide();
        });
    }
});