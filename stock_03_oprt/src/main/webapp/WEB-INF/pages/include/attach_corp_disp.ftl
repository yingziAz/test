<!--附件显示宏 -->
<!-- attaches 附件 -->
<!--@param attachField 附件对应的id名称 -->
<!--@param attachDivId 附件上一层最大的divId -->
<!--@param attachNum  附件的最大个数 如果值为1，则默认是单张上传  -->
<!--@param position  位置  m:中部  b:底部  默认中部 -->
<!--@param outWidth  位置  页面显示外框宽度 -->
<!--@param inlineWidth  位置  页面显示内框宽度 -->
<!--@param inlineHeigth  位置  页面显示内框高度 -->
<!--@param min_width  位置  最小宽度 -->
<!--@param min_height  位置  最小高度 -->
<#macro attach_corp_disp attaches attachField attachDivId attachNum position readonly outWidth inlineWidth inlineHeigth min_width min_height>
<#if !readonly>
	<!-- 上传编辑  -->
	<#if ((attachNum!1)==1 && attaches?? && (attaches?size)=0) || (attachNum!1) gt 1 >
	<!-- 条件：可编辑，如果是单张上传且没有附件 获取 是多张上传  -->
	<div style="padding: 0 0 0.5rem 1.0715rem;width: ${outWidth!150}px;height:auto" id="${attachDivId}_add">
		<figure class="overlay" style="height:${inlineHeigth!150}px;width:${inlineWidth!150}px;">
			<figcaption class="overlay-panel overlay-background overlay-fade overlay-icon">
				<a class="icon wb-upload" style="margin: 0 5px; font-size: 1.286rem; width: 1.286rem"
					onclick="uploadCorpFile('${attachField}','${attachDivId}','${attachNum!1}','${position!}','${outWidth}','${inlineWidth}','${inlineHeigth}','${min_width}','${min_height}')"></a>
			</figcaption>
		</figure>
	</div>
	</#if>
</#if>
<#if attaches?? >
    <#list attaches as attach>
    	<div style="padding:0 0 0.5rem 1.0715rem;width:${outWidth!150}px;height:auto" id="attachDiv_${attach.id}">
			<figure class="overlay overlay-hover" style="height:${inlineHeigth!150}px;width:${inlineWidth!150}px;">
				<img class="overlay-figure gallery-items" src="<@qcloud_url attachId="${attach.id!}"  thumb=false/>"
						data-high-res-src="<@qcloud_url attachId="${attach.id!}"  />"
						onerror="javascript:this.src='${ctx}/assets/images/placeholder.png'" alt="${attach.fileName!}" width="100%" height="100%">
			 	<div class="img_wd">${attach.fileName!}</div>
				<figcaption class="overlay-panel overlay-background overlay-fade overlay-icon">
					<a class="icon wb-search" style="margin:0 5px;font-size:1.286rem;width:1.286rem;"  href="javascript:void(0)" onclick="clickImg(event,this)" ></a>
					<a class="icon wb-download" style="margin:0 5px;font-size:1.286rem;width:1.286rem"  onclick="downloadQcloudFile('${attach.id!}')" href="javascript:void(0)" target="_self" ></a>
					<#if !readonly>
					<a class="icon wb-close" style="margin:0 5px;font-size:1.286rem;width:1.286rem" onclick="_removeCorpAttach('${attach.id!}','${attachField}','${attachDivId}','${attachNum!1}','${position!}'
					,'${outWidth}','${inlineWidth}','${inlineHeigth}','${min_width}','${min_height}')"></a>
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