// 图片裁剪
function imgCrop(attachId, imgOptions,otherOptions){
	var optStr = imgOptions ? JSON.stringify(imgOptions) : "";
	var otherOptions = otherOptions ? JSON.stringify(otherOptions) : "";
	modalHref(ctx + '/img/crop?attachId=' + attachId + '&options=' + encodeURIComponent(optStr)+'&otherOptions=' + encodeURIComponent(otherOptions), 'imgCropModal');
}

/**
 * @param attachField 附件对应的id名称
 * @param attachDivId 附件上一层最大的divId
 * @param attachNum  附件的最大个数 如果值为1，则默认是单张上传 
 * @param position  位置  m:中部  b:底部  默认中部
 * @param outWidth  位置  页面显示外框宽度
 * @param inlineWidth  位置  页面显示内框宽度
 * @param inlineHeigth  位置  页面显示内框高度
 * @param min_width  位置  最小宽度
 * @param min_height  位置  最小高度
 * @returns
 * 注：顺序一定要正确
 */
function uploadCorpFile(attachField,attachDivId,attachNum,position,outWidth,inlineWidth,inlineHeigth,min_width,min_height) {
	 if(isNullOrUndefined(attachNum)){
		 attachNum=1;
	 }
	 if(isNullOrUndefined(position)){
		 position='m';
	 }
	$remote = ctx+"/attach/corp/upload?attachField="+attachField+"&attachDivId="+attachDivId+"&attachNum="+attachNum+
	"&position="+position+"&outWidth="+outWidth+"&inlineWidth="+inlineWidth+
	"&inlineHeigth="+inlineHeigth+"&min_width="+min_width+"&min_height="+min_height,
	$modal = $('<div class="modal" id="corpFileModal"><div class="modal-body"></div></div>');
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
 * @param position  位置  m:中部  b:底部  默认中部
 * @param outWidth  位置  页面显示外框宽度
 * @param inlineWidth  位置  页面显示内框宽度
 * @param inlineHeigth  位置  页面显示内框高度
 * @param min_width  位置  最小宽度
 * @param min_height  位置  最小高度
 * @returns
 */
function uploadCorpFileComplete(attachField,attachDivId,attachId,attachFileName,attachFileSuffix,attachNum,position,outWidth,inlineWidth,inlineHeigth,min_width,min_height) {
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
		+'  <div style="padding: 0 0 0.5rem 1.0715rem;width:'+outWidth+'px;height:auto" id="attachDiv_'+attachId+'">'
		+'    <figure class="overlay overlay-hover" style="height:'+inlineHeigth+'px;width:'+inlineWidth+'px;">';
	disphtml+='<img class="overlay-figure gallery-items" src="'+downloadThumbUrl+'" data-high-res-src="'+downloadUrl+'"  width="100%" height="100%" />';
	disphtml+=''
		+'    <figcaption class="overlay-panel overlay-background overlay-fade overlay-icon">'
		
	disphtml+='<a class="icon wb-search" style="margin:0 5px;font-size:1.286rem;width:1.286rem;"  href="javascript:void(0)" onclick="clickImg(event,this)" ></a>';
	disphtml+='  <a class="icon wb-download" style="margin:0 5px;font-size:1.286rem;width:1.286rem" href="'+downloadUrl+'" target="_self" ></a>'
		+'    	<a class="icon wb-close" style="margin:0 5px;font-size:1.286rem;width:1.286rem"  onclick="_removeCorpAttach(\''+attachId+'\',\''+attachField+'\',\''+attachDivId+'\',\''+attachNum+'\',\''+position+'\',\''+outWidth+'\',\''+inlineWidth+'\',\''+inlineHeigth+'\',\''+min_width+'\',\''+min_height+'\')"></a>';
	//文件名称位置
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
	
	//默认不可缩小放大拖拽，
	imgCrop(attachId, null, {
		width:min_width,
		height:min_height
	});

	//step3:用于特殊回调额外处理
	if (typeof uploadCorpSuccess  === "function") { // 是函数 其中 FunName 为函数名称
		uploadCorpSuccess (attachId,attachField,attachDivId);
	} else{// 不是函数
		//console.log("不存在该函数");
	}
	
}

/**
 * 删除附件  （调用可写成_removeAttach('asfdsdgsdfgdf','singleImage','singleImageDiv')）
 * @param attachId  附件id
 * @param attachField  附件对应的id名称
 * @param attachDivId  附件上一层最大的divId
 * @param attachNum 附件的最大个数 如果值为1，则默认是单张上传
 * @param position  位置  m:中部  b:底部  默认中部
 * @param outWidth  位置  页面显示外框宽度
 * @param inlineWidth  位置  页面显示内框宽度
 * @param inlineHeigth  位置  页面显示内框高度
 * @param min_width  位置  最小宽度
 * @param min_height  位置  最小高度
 * @returns
 */
function _removeCorpAttach(attachId,attachField,attachDivId,attachNum,position,outWidth,inlineWidth,inlineHeigth,min_width,min_height){
	 if(isNullOrUndefined(attachNum)){
		 attachNum=1;
	 }
	 if(isNullOrUndefined(position)){
		 position='m';
	 }
	 
	//单文件删除的时候
	if(attachNum==1){
		//step1:设置附件id值为空
		$("#"+attachField).val("");
		var disphtml = ''
			+'  <div style="padding: 0 0 0.5rem 1.0715rem;width: '+outWidth+'px;height:auto;" >'
			+'	<figure class="overlay" style="height:'+inlineHeigth+'px;width:'+inlineWidth+'px;">'
			+'	<figcaption class="overlay-panel overlay-background overlay-fade overlay-icon">'
			+'		<a class="icon wb-upload" style="margin:0 5px;font-size:1.286rem;width:1.286rem" onclick="uploadCorpFile(\''+attachField+'\',\''+attachDivId+'\',1,\''+position+'\',\''+outWidth+'\',\''+inlineWidth+'\',\''+inlineHeigth+'\',\''+min_width+'\',\''+min_height+'\')"></a> '
			+'	</figcaption>'
			+'  </figure>'
			+'</div>';
		
		//step2:将上传附件按钮显示
		$("#"+attachDivId).html(disphtml);
		if (typeof removeCorpAttachSuccess === "function") { // 是函数 其中 FunName 为函数名称
			removeCorpAttachSuccess(attachId,attachField,attachDivId);
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