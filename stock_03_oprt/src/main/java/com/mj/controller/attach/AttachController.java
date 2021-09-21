package com.mj.controller.attach;


import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mj.api.attach.AttachFileApi;
import com.mj.model.base.AttachFile;

/**
 * 附件上传
 */

@Clear
public class AttachController extends AbstractAttachController {

	/**
	 * @Description:视频播放
	 * @author lu
	 * @version 2019 年 05 月 06 日  08:53:31 
	 */
	public void videoPlay() {
		String attachId = getPara("attachId");
		Integer type = getParaToInt("type",1);
		AttachFile attach = AttachFile.dao.findById(attachId);
		if(attach==null) {
			attach = new AttachFile();
		}
		setAttr("type",type);
		setAttr("attach", attach);
		render("video_play.html");
	}

	/**
	 * 删除附件
	 * @throws UnsupportedEncodingException 
	 */
	public void remove(){

		String attachId = this.getPara("attachId");
		AttachFile attachFile = AttachFile.dao.findById(attachId);
		if(attachFile == null){
			renderJson(RetKit.fail("文件已不存在"));
			return;
		}
		AttachFileApi.api.remove(attachId);
		renderJson(RetKit.ok());
	}


	/**
	 * 重命名附件
	 * @throws UnsupportedEncodingException 
	 */
	public void rename(){
		String attachId = this.getPara("attachId");
		AttachFile entity = AttachFile.dao.findById(attachId);
		setAttr("entity", entity);
		//createToken(IConstant.FORM_TOKEN, 30*60);
		render("attach_rename.html");
	}

	/**
	 * 执行保存更新处理
	 */
	@Before({Tx.class})
	public void update4Rename() {
		AttachFile entity = getBean(AttachFile.class,"entity");
		Ret ret = AttachFileApi.api.update4Rename(entity);
		if (ret.isOk()) {
			LogKit.info(MessageFormat.format("{0},执行重命名文件操作成功,文件名为:{1}",
					this.getLoginUserRealName(), entity.getFileName()));
		} 
		renderJson(ret);
	}


	/**
	 * 重命名附件
	 * @throws UnsupportedEncodingException 
	 */
	public void imageView(){
		String attachId = this.getPara("attachId");
		Integer winHeight = this.getParaToInt("winHeight",500);
		//AttachFile entity = AttachFile.dao.findById(attachId);
		setAttr("attachId", attachId);
		setAttr("winHeight", winHeight-64);
		//createToken(IConstant.FORM_TOKEN, 30*60);
		render("attach_image_view.html");
	}
}
