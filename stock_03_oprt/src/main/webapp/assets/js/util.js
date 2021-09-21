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
 * 打开模态框
 * @param e  事件
 * @param obj  this  
 */
function modal(e,obj){
	console.log(e);
	e.preventDefault();
	$remote = obj.remote ||obj.href, 
	$modal = $('<div class="modal" id="ajaxModal"><div class="modal-body"></div></div>');
	$('body').append($modal);
	$modal.modal({
		backdrop : 'static',
		keyboard : false
	});
	if ($remote.indexOf("?") > -1) {
		$remote += "&t=" + (new Date()).valueOf();
	} else {
		$remote += "?t=" + (new Date()).valueOf();
	}
	$modal.load($remote);
}

/**
 * 打开模态框
 * @param href  路径
 */
function modalHref(href,divId){
	$modal = $('<div class="modal example-modal-lg" id="'+divId+'"><div class="modal-body"></div></div>');
	$('body').append($modal);
	$modal.modal({
		backdrop : 'static',
		keyboard : false
	});
	if (href == undefined) {
		href = "";    
	}
	if (href.indexOf("?") > -1) {
		href += "&" + (new Date()).valueOf()
	} else {
		href += "?" + (new Date()).valueOf()
	}
	console.log(href);
	$modal.load(href);
}

/**
 * 关闭模态框
 * @param e  事件
 * @param obj  this  
 */
function closeModal(divId){
	$("#"+divId).modal('hide');  //手动关闭
	$("#"+divId).remove();//清除页面
}

//客户选择器
function cmpPicker(){
	modalHref(ctx+'/cmp/picker','ajaxModal');
}

//分公司选择器
function corpSelect(){
	modalHref(ctx+'/corp/corpSelector','corpModal');
}

//客服专员选择器  //type 1:返回方法  accountCallBack()  2:accountCallBack2()
function accountPicker(type){
	if(isNullOrUndefined(type)){
		type==1;
	}
	modalHref(ctx+'/system/account/accountPicker?type='+type,'ajaxModal');
}

/**
 * 多附件上传
 * @param attachIds
 * @param attachDivId
 */
function uploadWords(e,attachIds,attachDivId) {
	e.preventDefault();
	$remote = ctx+"/attach/upload/words?attachField="+attachIds+"&attachDivId="+attachDivId,
	$modal = $('<div class="modal" id="filesModal"><div class="modal-body"></div></div>');
	$('body').append($modal);
	$modal.modal({
		backdrop : 'static',
		keyboard : false
	});
	$modal.load($remote);
}

/**
 * 单文件上传操作
 * @param attachFieldId
 * @param attachDivId
 * @param attachFileId(file_id)
 * @param attachFileName(file_name)
 * @param attachFileSuffix(doc/x)
 * @returns
 */
function uploadOneFileComplete(attachFieldId,attachDivId,attachFileId,attachFileName,attachFileSuffix) {	
	$("#contractAttach").val(attachFileId);
	$("#contractAttach").trigger('input:changed',attachFileId);
	
	var downloadUrl = ctx +"/attach/download?attachId=" + attachFieldId;
	var dispFilename = attachFileName;
	if(typeof(readonly) == "undefined"){
		readonly =false;
	}
	var disphtml = ''
		+'  <div style="padding:15px;float:left;">'
			+'    <div>';
	if(!isImageType(attachFileSuffix)){
		disphtml+=''
			+'    <img src='+ctx+'"/assets/images/upload.png"  style="height:150px;" />';
	}else{
		disphtml+=''
			+'    <img src="'+downloadUrl+'" data-high-res-src="'+downloadUrl+'" class="gallery-items"  style="height:150px"  />';
	}
		disphtml+=''
			+'    </div>'
			+'    <div style="margin-top:5px;">'+dispFilename+'</div>'
			+'    <div class="text-left" style="height:25px">'
			+'      <a class="btn btn-white btn-bitbucket btn-xs" href="'+downloadUrl+'" target="_blank">'
			+'        <i class="fa fa-download"></i> 下载'
			+'      </a>';
		if(!readonly){
		disphtml+=''
			+'      <a class="btn btn-white btn-bitbucket btn-xs" onclick="_removeAttach(\''+attachFileId+'\',\''+dispFilename+'\',\''+attachFieldId+'\')">'
			+'        <i class="fa fa-trash"></i> 删除'
			+'      </a>';
		}
		disphtml+=''
			+'    </div>'
			+'  </div>';
	$("#" + attachDivId).html('<div class="file-box" name="'+attachFieldId+'" id="attachDiv_'+attachFileId+'">' + disphtml + "</div>");
	//模板文件错误文件信息删除
	if($("#attachError")){
		$("#attachError").remove();
		//将保存按钮设置为可填
		if($("#btnSubmit")){
			$("#attachError").removeAttr("disabled");
		}
	}
}

/**
 * 文件上传  （调用可写成uploadFile('singleImage','singleImageDiv')）
 * @param attachField 附件对应的id名称
 * @param attachDivId 附件上一层最大的divId
 * @param attachNum  附件的最大个数 如果值为1，则默认是单张上传 
 * @param fileType  a:无限制 w:word i:image e:excel 默认无限制：a
 * @param position  位置  m:中部  b:底部  默认中部
 * @returns
 * 注：顺序一定要正确
 * 调用示例：
 * 1.uploadFile('singleImage','singleImageDiv')  单个文件上传
 * 2.uploadFile('singleImage','singleImageDiv',2)  2个文件上传
 * 3.uploadFile('singleImage','singleImageDiv',2,'w')  2个word上传
 * 4.uploadFile('singleImage','singleImageDiv',2,'i')  2个图片上传
 * 5.uploadFile('singleImage','singleImageDiv',2,'e')  2个excel上传
 * 6.uploadFile('singleImage','singleImageDiv',2,'e','b')  2个excel上传,附件名称在底部
 * 7.uploadFile('singleImage','singleImageDiv',,,'b')  1个附件上传,附件名称在底部
 * 8.uploadFile('singleImage','singleImageDiv',,'w','b')  1个word上传,附件名称在底部
 */
function uploadFile(attachField,attachDivId,attachNum,fileType,position) {
	 if(isNullOrUndefined(attachNum)){
		 attachNum=1;
	 }
	 if(isNullOrUndefined(fileType)){
		 fileType='a';
	 }
	 if(isNullOrUndefined(position)){
		 position='m';
	 }
	$remote = ctx+"/attach/upload?attachField="+attachField+"&attachDivId="+attachDivId+"&attachNum="+attachNum+"&fileType="+fileType+"&position="+position,
	$modal = $('<div class="modal" id="filesModal"><div class="modal-body"></div></div>');
	$('body').append($modal);
	$modal.modal({
		backdrop : 'static',
		keyboard : false
	});
	$modal.load($remote);
}

/**
 * 文件上传回调
 * @param attachField  附件对应的id名称
 * @param attachDivId  附件上一层最大的divId
 * @param attachId  本次上传获取的附件id
 * @param attachFileName  本次上传获取的附件名称
 * @param attachFileSuffix   本次上传获取的附件后缀名
 * @param attachNum  附件的最大个数 如果值为1，则默认是单张上传
 * @param fileType   a:无限制 w:word i:image e:excel 默认无限制：a
 * @param position  位置  m:中部  b:底部  默认中部
 * @returns
 */
function uploadFileComplete(attachField,attachDivId,attachId,attachFileName,attachFileSuffix,attachNum,fileType,position) {
	var downloadUrl = ctx+"/attach/download?attachId=" + attachId;
	var downloadThumbUrl = ctx+"/attach/download/thumb?attachId=" + attachId;
	//step1:附件id处理
	var _attachFieldVal = $("#"+attachField).val();
	if(_attachFieldVal) {
		_attachFieldVal += "," + attachId;
	} else {
		_attachFieldVal = attachId;
	}
	if(attachNum==1){
		//如果只能是一张，则替换attachId
		_attachFieldVal = attachId;
	}
	$("#"+attachField).val(_attachFieldVal);
	$("#"+attachField).trigger('input:changed',_attachFieldVal);
	
	//step2:渲染缩略图
	var disphtml = ''
		+'  <div style="padding: 0 0 0.5rem 1.0715rem;width: 150px;height:auto" id="attachDiv_'+attachId+'">'
		+'    <figure class="overlay overlay-hover" style="height:135px;width:135px;">';
	if(!isImageType(attachFileSuffix)){
		if(attachFileSuffix == 'ppt' || attachFileSuffix=='pptx' || attachFileSuffix=='pdf'){
			disphtml+='<div class="ppt_wd" style="margin:10px;text-align:center;word-wrap:break-word;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:2;overflow: hidden;">'+attachFileName+'</div>';
		}else if( attachFileSuffix == 'doc' || attachFileSuffix=='docx'){
			disphtml+='<div class="word_wd" style="margin:10px;text-align:center;word-wrap:break-word;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:2;overflow: hidden;">'+attachFileName+'</div>';
		}else if( attachFileSuffix == 'xls' || attachFileSuffix=='xlsx'){
			disphtml+='<div class="xls_wd" style="margin:10px;text-align:center;word-wrap:break-word;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:2;overflow: hidden;">'+attachFileName+'</div>';
		}else if( attachFileSuffix == 'txt'){
			disphtml+='<div class="txt_wd" style="margin:10px;text-align:center;word-wrap:break-word;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:2;overflow: hidden;">'+attachFileName+'</div>';
		}else{
			disphtml+=' <div class="img_wd" style="margin:10px;text-align:center;word-wrap:break-word;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:2;overflow: hidden;">'+attachFileName+'</div>';
		}
	}else{
		disphtml+='<img class="overlay-figure gallery-items" src="'+downloadThumbUrl+'" data-high-res-src="'+downloadUrl+'"  />';
	}
	disphtml+=''
		+'    <figcaption class="overlay-panel overlay-background overlay-fade overlay-icon">'
		
		
	if(isOnLine(attachFileSuffix)){
//		disphtml+='	<a class="icon wb-search" style="margin:0 5px;font-size:1.286rem;width:1.286rem;"  href="https://view.officeapps.live.com/op/view.aspx?src='+downloadUrl+'" target="_blank" ></a>';
	}else if(attachFileSuffix =='pdf'){
//		disphtml+='<a class="icon wb-search" style="margin:0 5px;font-size:1.286rem;width:1.286rem;"  href="'+downloadUrl+'" target="_blank" ></a>';
	}else if(isImageType(attachFileSuffix)){
		disphtml+='<a class="icon wb-search" style="margin:0 5px;font-size:1.286rem;width:1.286rem;"  href="javascript:void(0)" onclick="clickImg(event,this)" ></a>';
	}else if(isVideoType(attachFileSuffix)){
//		disphtml+='<a class="icon wb-search" style="margin:0 5px;font-size:1.286rem;width:1.286rem;"  href="javascript:void(0)" onclick="videoPlay(\''+attachId+'\',2)" ></a>';
	}
	disphtml+='  <a class="icon wb-download" style="margin:0 5px;font-size:1.286rem;width:1.286rem" href="'+downloadUrl+'" target="_self" ></a>'
		+'    	<a class="icon wb-close" style="margin:0 5px;font-size:1.286rem;width:1.286rem"  onclick="_removeAttach(\''+attachId+'\',\''+attachField+'\',\''+attachDivId+'\',\''+attachNum+'\',\''+fileType+'\',\''+position+'\')"></a>';
	//文件名称位置
	if(position=='m'){
		if(!isImageType(attachFileSuffix)){
			disphtml+='    	<h5 class="animation-slide-bottom" style="margin:-15px;text-align:center;word-wrap:break-word;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:2;overflow: hidden;">'+attachFileName+'</h5>';
		}
	}
	disphtml+='    </figcaption>'
		+'  </figure>';
	//文件名称位置
	if(position=='b'){
		disphtml+='<h5 class="animation-slide-bottom" style="margin:10px;text-align:center;word-wrap:break-word;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:2;overflow: hidden;">'+attachFileName+'</h5>';
	}
	disphtml+='</div>';
	
	//如果只有一张，则覆盖更新
	if(attachNum==1){
		$("#" + attachDivId).html(disphtml);
	}else{
		$("#" + attachDivId).append(disphtml);
	}
	
	//step3:用于特殊回调额外处理
	if (typeof uploadSuccess === "function") { // 是函数 其中 FunName 为函数名称
		uploadSuccess(attachId,attachField,attachDivId);
	} else{// 不是函数
		//console.log("不存在该函数");
	}
	
}

//判断是否为图片文件
function isImageType(fileType) {
	if (isNullOrUndefined(fileType)) {
		return false;
	}
	var lowerType = fileType.toLowerCase();
	// jgp、jpeg、tiff、raw、bmp、gif、png
	if ("jpg" == lowerType || "jpeg" == lowerType || "tiff" == lowerType
			|| "raw" == lowerType || "bmp" == lowerType || "gif" == lowerType
			|| "png" == lowerType) {
		return true;
	}
	return false;
}

// 判断能否在线预览
function isOnLine(fileType) {
	if (isNullOrUndefined(fileType)) {
		return false;
	}
	var lowerType = fileType.toLowerCase();
	if ("doc" == lowerType || "docx" == lowerType || "xls" == lowerType
			|| "xlsx" == lowerType || "ppt" == lowerType || "pptx" == lowerType) {
		return true;
	}
	return false;
}

// 判断是否为视频文件
function isVideoType(fileType) {
	if (isNullOrUndefined(fileType)) {
		return false;
	}
	var lowerType = fileType.toLowerCase();
	// "mp4", "webm", "ogg"
	if ("mp4" == lowerType || "webm" == lowerType || "ogg" == lowerType) {
		return true;
	}
	return false;
}

/**
 * 删除附件  （调用可写成_removeAttach('asfdsdgsdfgdf','singleImage','singleImageDiv')）
 * @param attachId  附件id
 * @param attachField  附件对应的id名称
 * @param attachDivId  附件上一层最大的divId
 * @param attachNum 附件的最大个数 如果值为1，则默认是单张上传
 * @param fileType   a:无限制 w:word i:image e:excel  默认无限制：a
 * @param position  位置  m:中部  b:底部  默认中部
 * @returns
 */
function _removeAttach(attachId,attachField,attachDivId,attachNum,fileType,position){
	 if(isNullOrUndefined(attachNum)){
		 attachNum=1;
	 }
	 if(isNullOrUndefined(fileType)){
		 fileType='a';
	 }
	 if(isNullOrUndefined(position)){
		 position='m';
	 }
	 
	//单文件删除的时候
		if(attachNum==1){
			//step1:设置附件id值为空
			$("#"+attachField).val("");
			var disphtml = ''
				+'  <div style="padding: 0 0 0.5rem 1.0715rem;width: 150px;height:auto;" >'
				+'	<figure class="overlay" style="height:135px;width:135px;">'
				+'	<img src='+ctx+'"/assets/images/upload.png"  class="overlay-figure" />'
				+'	<figcaption class="overlay-panel overlay-background overlay-fade overlay-icon">'
				+'		<a class="icon wb-upload" style="margin:0 5px;font-size:1.286rem;width:1.286rem" onclick="uploadFile(\''+attachField+'\',\''+attachDivId+'\',1,\''+fileType+'\',\''+position+'\')"></a> '
				+'	</figcaption>'
				+'  </figure>'
				+'</div>';
			
			//step2:将上传附件按钮显示
			$("#"+attachDivId).html(disphtml);
			if (typeof removeAttachSuccess === "function") { // 是函数 其中 FunName 为函数名称
				removeAttachSuccess(attachId,attachField,attachDivId);
			}else{// 不是函数
				console.log("不存在该函数");
			}
		}else{
			//多文件的时候删除
			//step1:处理attaid
			var attachs = $("#"+attachField).val();
			//找到对应的删除的attachId，这只为空，然后重新片接
			attachs = attachs.replace(attachId,"");
			//处理附件逗号问题
			var newattaches ="";
			var attach = attachs.split(",");
			for (var i = 0; i<attach.length;i++) {
				if(!isNullOrUndefined(attach[i])){
					if(!isNullOrUndefined(newattaches)){
						newattaches += ",";
					}
					newattaches += attach[i];
				}
			}
			$("#"+attachField).val(newattaches);
			
			//step2:删除删除的附件对应的div
			$("#attachDiv_" + attachId).remove();
			
			if (typeof removeMoreAttachSuccess === "function") { // 是函数 其中 FunName 为函数名称
				removeMoreAttachSuccess(attachId,attachField,attachDivId);
			}else{// 不是函数
				console.log("不存在该函数");
			}
		}
		
//	$.ajax({
//		type: "post",
//		url: ctx+"/attach/maintain/remove",
//		data: {"attachId":attachId},
//		dataType: "json",
//		success: function(result){
//			if(result.state=="ok"){
//				
//			}else{
//				console.log(result);
//			}
//		}
//	});
}

//图片查看
function clickImg(e,obj){
	imgShow();
	$(obj).parent().siblings().click();
}

//视频播放
function videoPlay(attachId,type){
	modalHref(ctx+'/attach/maintain/videoPlay?attachId='+attachId+'&type='+type,'videoPlayModal');
}
//Alertify 文案本地化
function registerAlertify4Local(){
	alertify.okBtn("确定").cancelBtn("关闭").theme("bootstrap").logPosition("top right").delay(1000);
}

function str2Date(str) {
	if(isNullOrUndefined(str) || str.length < 8) {
		return null;
	}
	var year = str.substring(0,4);
	var month = str.substring(4,6);
	var day = str.substring(6,8);
	var HH = 0, mm = 0;
	if(str.length > 8) {
		HH = str.substring(9,11);
		mm = str.substring(12,14);
	}
	var d = new Date();
	d.setFullYear(year,month-1,day);
	d.setHours(HH);
	d.setMinutes(mm);
	d.setSeconds(0);
	return d;
}
function month2Str(date) {
	 if(isNullOrUndefined(date)){
	   	 return "";
	 }
	 var y = date.getFullYear();
	 var m = date.getMonth() + 1;  
	 if(m<10) m = "0" + m;
	 var redate = y +""+m; 
	 return redate;
}
//月份相加： yyyy-MM + n
function plusMonth(yearMonth,n) {
	var t= str2Date(yearMonth+"01");
	t.setMonth(t.getMonth() + n);
	return month2Str(t);
}

function date2String(datetime,split){  
    var year = datetime.getFullYear();  
    var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;  
    var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();  
    return year + split + month + split + date;  
} 

//图片显示器初始化
function imgShow(){
 	$('.gallery-items').click(function () {
 		var viewer = ImageViewer();
		var imgSrc = this.src,
	 	highResolutionImage = $(this).data('high-res-img');
	 	viewer.show(imgSrc, highResolutionImage);
	 });
}

//图片查看
function clickImg(e,obj){
	imgShow();
	$(obj).parent().siblings().click();
}

function downloadQcloudFile(id){
	self.location=ctx+"/attach/download/downloadQcloudFile?id="+id;
}


/**
 * 错误处理
 */
function resultErr(result) {
	console.log(result);
	if (result.result == 1) {// 未登录
		toastr.error("用户未登录或超时，请重新登录！","用户未登录或超时",{positionClass: "toast-top-center"});
		if (window != top){
			top.location.href = ctx+"/login";
		}else{
			location.href = "/";
		}
	} else {// 提示错误
		toastr.error(result.msg,"",{positionClass: "toast-top-center"});
//		alertify.alert(result.msg);
	}
}

//左侧列表滚动条设置高度
function leftHeight(time){
	setTimeout(function(){
		var panelBodyHeight=$(".panel-body").outerHeight(true);
		if(winHeight>panelBodyHeight){
			$(".scrollable-container").css({"height":(winHeight-180)+"px"});
		}else{
			$(".scrollable-container").css({"height":(panelBodyHeight)+"px"});
		}
	},time);
}

function downloadQcloudFile(id){
	self.location=ctx+"/attach/download/downloadQcloudFile?id="+id;
}

//加载页面弹出
function showAjaxLoading(){
	//页面load 显示
	$('body').append('<div class="alertify vertical-align text-center" name="commonDialoag"><div class="vertical-align-middle loader-round-circle-self font-size-100"></div></div>');
}
//关闭加载页面
function hideAjaxLoading(){
	$("div[name='commonDialoag']").remove();
}
