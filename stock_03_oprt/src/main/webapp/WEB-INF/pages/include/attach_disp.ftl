<!--附件显示宏 -->
<!-- attaches 附件 -->
<!-- attachField 附件对应的id名称 -->
<!-- attachDivId 附件上一层最大的divId -->
<!-- attachNum  附件的最大个数 如果值为1，则默认是单张上传  -->
<!-- fileType  a:无限制 w:word i:image e:excel 默认无限制：a -->
<!-- position  位置  m:中部  b:底部  默认中部 -->
<!-- readonly  true:只读 false:可删除 -->
<#macro attach_disp attaches attachField attachDivId attachNum fileType position readonly>
<#if !readonly>
	<!-- 上传编辑  -->
	<#if ((attachNum!1)==1 && attaches?? && (attaches?size)=0) || (attachNum!1) gt 1 >
	<!-- 条件：可编辑，如果是单张上传且没有附件 获取 是多张上传  -->
	<div style="padding: 0 0 0.5rem 1.0715rem;width: 150px;height:auto" id="${attachDivId}_add">
		<figure class="overlay" style="height:135px;width:135px;">
			<img class="overlay-figure img-fluid" src="${ctx}/assets/images/upload.png" alt="上传文件">
			<figcaption class="overlay-panel overlay-background overlay-fade overlay-icon">
				<a class="icon wb-upload" style="margin: 0 5px; font-size: 1.286rem; width: 1.286rem"
					onclick="uploadFile('${attachField}','${attachDivId}','${attachNum!1}','${fileType!}','${position!}')"></a>
			</figcaption>
		</figure>
	</div>
	</#if>
</#if>
<#if attaches?? >
    <#list attaches as attach>
    	<div style="padding:0 0 0.5rem 1.0715rem;width:150px;height:auto" id="attachDiv_${attach.id}">
			<figure class="overlay overlay-hover" style="height:135px;width:135px;">
				<#if attach.isImageAttach!false>
					<img class="overlay-figure gallery-items" src="${ctx}/attach/download?attachId=${attach.id}"
						data-high-res-src="${ctx}/attach/download?attachId=${attach.id}"
						onerror="javascript:this.src='${ctx}/assets/images/upload.png'" alt="${attach.fileName!}" width="100%" height="100%">
				<#elseif (attach.fileSuffix!'doc') == 'ppt' || (attach.fileSuffix!'doc')=='pptx' || (attach.fileSuffix!'doc')=='pdf'>
					<div class="ppt_wd">${attach.fileName!}</div>
				<#elseif (attach.fileSuffix!'doc') == 'doc' || (attach.fileSuffix!'doc')=='docx'>
					<div class="word_wd">${attach.fileName!}</div>
				<#elseif (attach.fileSuffix!'doc') == 'xls' || (attach.fileSuffix!'doc')=='xlsx'>
					<div class="xls_wd">${attach.fileName!}</div>
				<#elseif (attach.fileSuffix!'doc') == 'txt'>
					<div class="txt_wd">${attach.fileName!}</div>
				<#else>
				 	<div class="img_wd">${attach.fileName!}</div>
				</#if>
				<figcaption class="overlay-panel overlay-background overlay-fade overlay-icon">
					<#if attach.onLineView!false>
						<a class="icon wb-search" style="margin:0 5px;font-size:1.286rem;width:1.286rem;"  href="https://view.officeapps.live.com/op/view.aspx?src=${attach.fullUrl}" target="_blank" ></a>
					<#elseif (attach.fileSuffix!'doc') =='pdf'>
						<a class="icon wb-search" style="margin:0 5px;font-size:1.286rem;width:1.286rem;"  href="${attach.fullUrl}" target="_blank" ></a>
					<#elseif attach.isImageAttach!false>
						<a class="icon wb-search" style="margin:0 5px;font-size:1.286rem;width:1.286rem;"  href="javascript:void(0)" onclick="clickImg(event,this)" ></a>
					<#elseif attach.isVideoAttach!false>
						<a class="icon wb-search" style="margin:0 5px;font-size:1.286rem;width:1.286rem;"  href="javascript:void(0)" onclick="videoPlay('${attach.id!}',1)" ></a>
					</#if>
				
					<a class="icon wb-download" style="margin:0 5px;font-size:1.286rem;width:1.286rem"  href="${ctx}/attach/download?attachId=${attach.id!}" target="_blank"></a>
					
					<#if !readonly>
					<a class="icon wb-close" style="margin:0 5px;font-size:1.286rem;width:1.286rem" onclick="_removeAttach('${attach.id!}','${attachField}','${attachDivId}','${attachNum!1}','${fileType!}','${position!}')"></a>
					</#if>
					<#if (position!'m')=='m'>
					<#if attach.isImageAttach!false>
						<h5 class="animation-slide-bottom" style="margin:-15px;text-align:center;word-wrap:break-word;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:2;overflow: hidden;">${attach.fileName!}</h5>
					</#if>
					</#if>
				</figcaption>
			</figure>
			<#if (position!'m')=='b'>
				<h5 class="animation-slide-bottom" style="margin:10px;text-align:center;word-wrap:break-word;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp:2;overflow: hidden;">${attach.fileName!}</h5>
			</#if>
		</div>
    </#list>
</#if>
</#macro>