$(function(){
	$(document).ajaxStart(function(){
		layer.load(1);
	});
	$(document).ajaxStop(function(){
		layer.closeAll('loading');
	});

	//禁用backspace键的返回功能
	document.onkeydown = check;
}); 

//禁用backspace键的返回功能
function check(e) {
	  var code;
	  if (!e) var e = window.event;
	  if (e.keyCode) code = e.keyCode;
	  else if (e.which) code = e.which;
	  if (((event.keyCode == 8) && //BackSpace 
	  ((event.srcElement.type !="text"&& 
	  event.srcElement.type !="textarea"&& 
	  event.srcElement.type !="password") || 
	  event.srcElement.readOnly == true)) || 
	  ((event.ctrlKey) && ((event.keyCode == 78) || (event.keyCode == 82)) ) || //CtrlN,CtrlR 
	  (event.keyCode == 116) ) { //F5 
	  event.keyCode = 0; 
	  event.returnValue = false; 
	 }
	 return true;
}

layer.config({
	extend: 'extend/layer.ext.js'
});

var winWidth = 0;   
var winHeight = 0;   
//函数：获取尺寸   
function findDimensions() {   
	 //获取窗口宽度   
	 if (window.innerWidth)   
		   winWidth = window.innerWidth;   
	 else if ((document.body) && (document.body.clientWidth))   
		   winWidth = document.body.clientWidth;   
	 //获取窗口高度   
	 if (window.innerHeight)   
		   winHeight = window.innerHeight;   
	 else if ((document.body) && (document.body.clientHeight))   
		   winHeight = document.body.clientHeight;   
	  
	 //通过深入Document内部对body进行检测，获取窗口大小   
	 if (document.documentElement  && document.documentElement.clientHeight &&   
										  document.documentElement.clientWidth) {   
		 winHeight = document.documentElement.clientHeight;   
		 winWidth = document.documentElement.clientWidth;   
	 }
}

/**
 * 是否为空或空字符串
 */
function isNull(obj) {
	if(obj && obj != "") {
		return false;
	} else {
		return true;
	}
}

/**
 * 是否为空或空字符串或者未定义
 */
function isNullOrUndefined(obj) {
	if(obj==null || obj == "" || obj==undefined) {
		return true;
	} else {
		return false;
	}
}


/**
 * 错误处理
 */
function resultErr(result) {
	if (result.result == 1) { // 未登录
		messageBoxAdv("登录超时","用户未登录或超时，请重新登录！",{title:"登陆超时"});
		if (window != top){
			top.location.href = ctx+"/login"; 
		}else{
			location.href = "/";
		}
	} else { // 提示错误
		messageBoxAdv(result.errMsg,{title:"操作出错"});
	}
}

/**
 * 消息提示框
 */
function messageBox(msg) {
	layer.alert(msg, {
		title : "消息框",
		btn: ['确定'], //按钮
		time: 5000
	});
}
/**
 * 消息提示框，消息提示后，有后续动作执行
 */
function messageBoxCallback(msg,callback,delay) {
	if(!delay){
		delay = 1500;
	}
	layer.open({
		content: msg,
		success: function(layero, index){
		  setTimeout(function(){
			  callback()
		  },delay);
	  }
	});
}

/**
 * 高级消息提示框
 */
function messageBoxAdv(msg,option) {
	var defaultOption={
		title:"消息框",
		btn: ['确定'],
		time:5000
	};
	if(option && typeof(option)=='object'){
		defaultOption = $.extend(defaultOption,option)
	}
	layer.alert(msg, defaultOption);
}




/**
 * 执行带参数的回调函数
 * 
 * @param callback
 * @param paramsAgrs
 */
function execCallback(callback,paramsAgrs) {
	if(Object.prototype.toString.call(paramsAgrs) === '[object Array]') {
		if(paramsAgrs.length == 1) {
			callback(paramsAgrs[0]);
		} else if(paramsAgrs.length == 2) {
			callback(paramsAgrs[0],paramsAgrs[1]);
		} else if(paramsAgrs.length == 3) {
			callback(paramsAgrs[0],paramsAgrs[1],paramsAgrs[2]);
		} else if(paramsAgrs.length == 4) {
			callback(paramsAgrs[0],paramsAgrs[1],paramsAgrs[2],paramsAgrs[3]);
		} else if(paramsAgrs.length == 5) {
			callback(paramsAgrs[0],paramsAgrs[1],paramsAgrs[2],paramsAgrs[3],paramsAgrs[4]);
		} else if(paramsAgrs.length == 6) {
			callback(paramsAgrs[0],paramsAgrs[1],paramsAgrs[2],paramsAgrs[3],paramsAgrs[4],paramsAgrs[5]);
		}
	} else {
		callback();
	}
}
/**
 * 确认弹框
 */
function confirmBox(msg,callback,paramsAgrs) {
	if(!msg) msg = "";
	var	title = "确认框";
	layer.confirm(msg, {
		title : title,
		btn: ['确定','取消'], //按钮
		yes: function(index, layero){
			//调用回调函数
			execCallback(callback,paramsAgrs);
			layer.close(index); //如果设定了yes回调，需进行手工关闭
		}
	});
}

/**
 * prompt确认框
 */
function promptBox(title,callback,paramsAgrs,formType) {
	//formType:0-text,1-password,2-textarea
	if(!formType){formType = 2;}
	//prompt层
	layer.prompt({
		title: title,
		formType: formType,
	}, function(promptMsg,index){
		paramsAgrs.push(promptMsg);
		execCallback(callback,paramsAgrs);
		layer.close(index);
	});
}
/**
 * 页面弹出框 
 */
function dialogBox(tilt,url,width,height) {
	if(!width){width = "500px";}
	if(!height){height = "320px";}
	layer.open({
		type: 2,
		title: tilt,
		shadeClose: false,
		shade: false,
		maxmin: false, //开启最大化最小化按钮
		area: [width, height],
		content: url
	});
}

/**
 * 页面弹出框(自适应)
 */
function dialogBox4auto(title,url) {
	findDimensions();
	var width = (winWidth-120)+"px";
	var height = (winHeight-50)+"px";
	layer.open({
		type: 2,
		title: title,
		shadeClose: false,
		shade: false,
		maxmin: true, //开启最大化最小化按钮
		zIndex: 5999999999,
		area: [width, height],
		content: url
	});
}


function closeDialogBox(){
	var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
	parent.layer.close(index); //再执行关闭   
}

/**
 * 单张图片上传
 * @param attchId
 * @param attchDispId
 */
function uploadImage(attachField,attachImage){
	var target=ctx+"/attach/upload/image?attachField="+attachField+"&attachImage="+attachImage;
	dialogBox("图片上传",target,"650px","475px");
}
/**
 * 多张图片上传
 * @param attchId
 * @param attchDispId
 */
function uploadImages(attachField,attachImage){
	var target=ctx+"/attach/upload/images?attachField="+attachField+"&attachImage="+attachImage;
	dialogBox("图片上传",target,"650px","475px");
}

/**
 * 支付宝单张图片上传
 * @param attachField
 * @param attachImage
 */
function uploadImage4Alipay(attachField,attachImage,alipayId){
	var target=ctx+"/attach/upload/image4Alipay?attachField="+attachField+"&attachImage="+attachImage+"&alipayId="+alipayId;
	dialogBox("图片上传",target,"650px","475px");
}
/**
 * 图片上传完成操作
 * @param attchId
 * @param attchDispId
 */
function uploadImageComplete(attachField,attachImage,attachId,url) {
	$("#"+attachField).val(attachId);
	//$("#"+attachImage).src = url;
	//$("#"+attachImage).attr("src",url);
	$("#"+attachImage).html('<img alt="image" src="'+url+'" width="100%" height="100%"/>');
}

/**
 * 支付宝图片上传完成操作
 * @param attachField
 * @param attachImage
 * @param attachId
 * @param url
 */
function uploadImageComplete4Alipay(attachField,attachImage,alipayId,alipayIdVal,attachId,url) {
	$("#"+attachField).val(attachId);
	//$("#"+attachImage).src = url;
	//$("#"+attachImage).attr("src",url);
	$("#"+alipayId).val(alipayIdVal);
	$("#"+attachImage).html('<img alt="image" src="'+url+'" width="100%" height="100%"/>');
}
/**
 * 附件上传
 * @param attachIds
 * @param attachDivId
 */
function uploadFiles(attachIds,attachDivId) {
	var target=ctx+"/attach/upload/files?attachField="+attachIds+"&attachDivId="+attachDivId;
	dialogBox("附件上传",target,"650px","475px");
}

//判断是否为图片文件
function isImageType(fileType) {
	if(isNull(fileType)) { return false; }
	var lowerType = fileType.toLowerCase();
	//jgp、jpeg、tiff、raw、bmp、gif、png
	if("jpg"==lowerType || "jpeg"==lowerType || "tiff" == lowerType 
			|| "raw" == lowerType || "bmp" == lowerType 
			|| "gif" == lowerType || "png" == lowerType) {
		return true;
	}
	return false;
}
/**
 * 文件上传完成后返回url和filename
 * attachFileId 文件Id
 * attchFileName 文件名
 * attchFileSuffix 文件后缀名
 * @param url
 */
function uploadFileComplete(attachFieldId,attachDivId,attachFileId,attachFileName,attachFileSuffix) {
	var _attachFieldVal = $("#"+attachFieldId).val();
	if(_attachFieldVal) {
		_attachFieldVal += "," + attachFileId;
	} else {
		_attachFieldVal = attachFileId;
	}
	$("#"+attachFieldId).val(_attachFieldVal);
	dispAnAttachFile(attachFieldId,attachDivId,attachFileId,attachFileName,attachFileSuffix);
}

/**
 * 支付宝图片上传完成
 */
function uploadFileComplete4Alipay(attachFieldId,attachDivId,alipayId,attachFileId,attachFileName,attachFileSuffix,alipayIdVal) {
	var _attachFieldVal = $("#"+attachFieldId).val();
//	if(_attachFieldVal) {
//		_attachFieldVal += "," + attachFileId;
//	} else {
		_attachFieldVal = attachFileId;
//	}
	$("#"+attachFieldId).val(_attachFieldVal);
	$("#"+alipayId).val(alipayIdVal);
	dispAnAttachFile(attachFieldId,attachDivId,attachFileId,attachFileName,attachFileSuffix);
}

function dispAnAttachFile(attachFieldId,attachDivId,attachFileId, attachFileName,attchFileSuffix) {
	var downloadUrl = ctx+"/attach/download?attachId=" + attachFileId;
	var dispFilename = attachFileName;
   	if(typeof(readonly) == "undefined"){
   		readonly =false;
   	}
   	var disphtml = ''
   			//'<div class="file-box" id="attachDiv_'+attachFileId+'">'
   			//+'<a href="#">'
   			+'  <div class="file">'
   			+'    <span class="corner"></span>';
		if(!isImageType(attchFileSuffix)){
   		disphtml+=''
   			+'    <div class="icon"><i class="fa fa-bar-chart-o"></i></div>';
		}else{
   		disphtml+=''
   			+'    <div class="image"><img alt="image" class="img-responsive" src="'+downloadUrl+'"></div>';
		}
   		disphtml+=''
   			+'    <div class="file-name">'+dispFilename+'</div>'
   			+'    <div class="file-name text-center">'
   			+'      <a class="btn btn-white btn-bitbucket btn-sm" href="'+downloadUrl+'" target="_blank">'
   			+'        <i class="fa fa-download"></i> '
   			+'      </a>';
   		if(!readonly){
   		disphtml+=''
   			+'      <a class="btn btn-white btn-bitbucket btn-sm" onclick="_removeAttach(\''+attachFileId+'\',\''+dispFilename+'\',\''+attachFieldId+'\')">'
   			+'        <i class="fa fa-trash"></i> '
   			+'      </a>'
   			+'      <a class="btn btn-white btn-bitbucket btn-sm" onclick="_renameAttach(\''+attachFileId+'\')">'
   			+'        <i class="fa fa-edit"></i> '
   			+'      </a>';
   		}
   		disphtml+=''
   			+'    </div>'
   			+'  </div>';
   			//+'</a>';
   			//+'</div>';
	var objIdDiv = $("#attachDiv_" + attachFileId);
	if(objIdDiv.length > 0) { // 已经存在，替换之
		objIdDiv.html(disphtml);
	} else {
		var oldText = $("#" + attachDivId).html();
		$("#" + attachDivId).prepend('<div class="file-box" name="attachDivDisp" id="attachDiv_'+attachFileId+'">' + disphtml + "</div>");
		$("#noneImg").hide();
	}
}


//删除附件操作
function _removeAttach(attachId,attachFileName,attachFieldId){
	confirmBox("确定要删除附件\""+attachFileName+"\"吗？",doRemoveAttach,[attachId,attachFieldId]);
}

//执行删除操作
function doRemoveAttach(attachId,attachFieldId){
	$.ajax({
		type: "post",
		url: ctx+"/attach/maintain/remove",
		data: {"attachId":attachId},
		dataType: "json",
		success: function(result){
			if(result.success){
				messageBox("操作成功!");
				$("#attachDiv_" + attachId).remove();
				var attachs = $("#"+attachFieldId).val();
				attachs = attachs.replace(attachId,"");
				//处理附件逗号问题
				var newattaches ="";
				var attach = attachs.split(",");
				for (var i = 0; i<attach.length;i++) {
					if(!isNullOrUndefined(attach[i].trim())){
						if(!isNullOrUndefined(newattaches.trim())){
							newattaches += ",";
						}
						newattaches += attach[i];
					}
				}
				console.log(1111);
				console.log(newattaches);
				$("#"+attachFieldId).val(newattaches);
				if(newattaches==""){
					$("#attachFileDiv").html('<div class="file-box" id="noneImg">'
							+'<div class="file">'
							+' <div class="icon" style="height:155px;">'
							+'		<i class="fa fa-image" style="padding-top:30px;"></i>'
							+' </div>'
							+' </div>'
							+'</div>');
					$("#noneImg").show();
					console.log(2222);
				}
			}else{
				resultErr(result);
			}
		}
	});
}



////删除附件操作
//function _removeAttach(attachId,attachFileName){
//	confirmBox("确定要删除附件\""+attachFileName+"\"吗？",doRemoveAttach,[attachId]);
//}
//
////执行删除操作
//function doRemoveAttach(attachId){
//	$.ajax({
//		type: "post",
//		url: ctx+"/attach/maintain/remove",
//		data: {"attachId":attachId},
//		dataType: "json",
//		success: function(result){
//			 if(result.success){
//				 messageBox("操作成功!");
//				 $("#attachDiv_" + attachId).remove();
//			 }else{
//				 resultErr(result);
//			 }
//		}
//	});
//}

//重命名附件操作
function _renameAttach(attachId){
	var target=ctx+"/attach/maintain/rename?attachId="+attachId;
	dialogBox("图片上传",target,"420px","300px");
}

function imageView(attachId,attachName){
	findDimensions();
	var _width = winWidth - 160;
	var _height = winHeight-10;
	var target=ctx+"/attach/maintain/imageView?attachId="+attachId+"&winHeight="+_height;
	dialogBox("图片查看器",target,_width+"px",_height+"px");
}


/**
 * 为某一区域设置经纬度（百度地图）
 */
function baiduMapMarker(addrFieldId,lngField,latField){
	findDimensions();
	var _addr = '';
	if($("#"+addrFieldId).length>0){
		_addr = $("#"+addrFieldId).val();
	}
	var _width = winWidth - 100;
	var _height = winHeight - 10;
	
	var target=ctx+"/map/baidu/marker?addr="+_addr+"&lngField="+lngField+"&latField="+latField;
	dialogBox("设置区域经纬度信息",target,_width+"px",_height+"px");
}


function userTreePicker(fieldId,fieldDisp){
	findDimensions();
	var _height = winHeight - 10;
	var target=ctx+"/system/account/userTreePicker?fieldId="+fieldId+"&fieldDisp="+fieldDisp;
	dialogBox("接收人选择",target,"400px",_height+"px");
}

//行业类型选择器
function industryPicker(fieldId,fieldDisp){
	var target=ctx+"/base/industry/industryPicker?fieldId="+fieldId+"&fieldDisp="+fieldDisp+"&industryIds="+$("#"+fieldId).val();
	dialogBox("行业类型选择器",target,"560px","400px");
}

//date转String
function date2Str(date){
	 if(date == "" || date == null){
	   	 return "";
	 }
	 var y = date.getFullYear();
	 var m = date.getMonth() + 1;  
	 if(m<10) m = "0" + m;
	 var d = date.getDate();  
	 if(d<10) d = "0" + d;  
	 var redate = y + "-" + m + "-" + d; 
	 return redate;
}
//注册bootstraptable resize 事件
function registerBootstrapTableResizeEvent(){
	$(window).resize(function (){
		$dataTable.bootstrapTable('resetView');
	});
}
//重置表格高度
function resetBootstrapTableHeight(){
	$dataTable.on('post-body.bs.table', function () {
		setTimeout("doResetBootstrapTableHeight()",700);
	});
}
//计算列表页面的表格高度
function calcBootstrapTableHeight() {
	findDimensions();
	var _height=winHeight - $('.ibox-title').outerHeight(true)-101;
	if(_height< 550){
		return 550;
	}
	return _height;
}
//执行重置列表页面的高估的
function doResetBootstrapTableHeight() {
	$dataTable.bootstrapTable('resetView',{
		height: calcBootstrapTableHeight()
	});
}
 //注册bootstraptable resize 事件
function registerBootstrapTableResizeWithHeightEvent(){
	$(window).resize(function (){
		doResetBootstrapTableHeight();
	});
}

//关闭当前选项卡
function closeCurrentNavTab(){
	parent.closeNavtab($(window.frameElement).data("id"));
}

function toDay() {
	return date2Str(new Date());
}

//用于批量选择获取选择项
function getSelectedIds() {
	var boxes = $("[name='ids']");
	var ids = new Array();
	var count = 0;
	for ( var i = 0; i < boxes.length; i++) {
		if (boxes[i].checked) {
			ids[count] = boxes[i].value;
			count++;
		}
	}   
	if(count <= 0) {
		messageBox("请至少选中一条记录！");
		return null;
	}
	return ids;
}

//全选或者全不选
function checkboxAll(status) {
	var boxes = $("[name='ids']");
	for( var i=0; i < boxes.length; i++ )   {
		boxes[i].checked = status;
	}
}

function formatDate(date, format) {   
	if (!date) return;   
	if (!format) format = "yyyy-MM-dd";   
	switch(typeof date) {   
		case "string":   
			date = new Date(date.replace(/-/, "/"));   
			break;   
		case "number":   
			date = new Date(date);   
			break;   
	}	
	if (!date instanceof Date) return;   
	var dict = {   
		"yyyy": date.getFullYear(),   
		"M": date.getMonth() + 1,   
		"d": date.getDate(),   
		"H": date.getHours(),   
		"m": date.getMinutes(),   
		"s": date.getSeconds(),   
		"MM": ("" + (date.getMonth() + 101)).substr(1),   
		"dd": ("" + (date.getDate() + 100)).substr(1),   
		"HH": ("" + (date.getHours() + 100)).substr(1),   
		"mm": ("" + (date.getMinutes() + 100)).substr(1),   
		"ss": ("" + (date.getSeconds() + 100)).substr(1)   
	};	   
	return format.replace(/(yyyy|MM?|dd?|HH?|ss?|mm?)/g, function() {   
		return dict[arguments[0]];   
	});				   
}

//ie8不支持indexof问题解决
if (!Array.prototype.indexOf)
{
  Array.prototype.indexOf = function(elt )
  {
	var len = this.length >>> 0;

	var from = Number(arguments[1]) || 0;
	from = (from < 0)
		 ? Math.ceil(from)
		 : Math.floor(from);
	if (from < 0)
	  from += len;

	for (; from < len; from++)
	{
	  if (from in this &&
		  this[from] === elt)
		return from;
	}
	return -1;
  };
}

