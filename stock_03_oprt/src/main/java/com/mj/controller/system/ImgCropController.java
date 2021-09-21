package com.mj.controller.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;
import com.jfinal.ext.kit.RetKit;
import com.mj.api.attach.AttachFileApi;
import com.mj.controller.BaseController;
import com.mj.kit.KeyKit;
import com.mj.model.base.AttachFile;

import cn.hutool.core.io.FileUtil;
import net.coobird.thumbnailator.Thumbnails;
import sun.misc.BASE64Decoder;

@SuppressWarnings("restriction")
public class ImgCropController extends BaseController {

	public void index() {
		if (StringUtils.isNotBlank("options")) {
			setAttr("options", getPara("options"));
		}
		if (StringUtils.isNotBlank("otherOptions")) {
			setAttr("otherOptions", getPara("otherOptions"));
		}
		setAttr("attachId", getPara("attachId"));
		render("img_crop.html");
	}

	public void uploadCropedImg() {
		String oldAttachId = getPara("attachId");
		String newAttachId = KeyKit.uuid24();
		String base64Str = getPara("base64Str");
		Map<String, String> data = Maps.newHashMap();

		try {
			AttachFile entity = AttachFileApi.api.findById(oldAttachId);
			if (entity == null) {
				renderJson(RetKit.fail());
				return;
			}
			String suffix = entity.getFileSuffix();

			File sf = new File(AttachFileApi.getFileDir() + "\\");
			if (!sf.exists()) {
				sf.mkdirs();
			}

			String outPutFilePath = AttachFileApi.getBaseUploadPath() + File.separator + newAttachId + "." + suffix;
			File outPutFile = new File(outPutFilePath);
			OutputStream os = new FileOutputStream(outPutFile);
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] b = decoder.decodeBuffer(base64Str.split(",")[1]);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			os.write(b);
			os.close();

			String storeUrl = AttachFileApi.getFileDir() + File.separator + newAttachId + "." + suffix;
			String thumbUrl = AttachFileApi.getFileDir() + File.separator + oldAttachId + "_thumb." + suffix;
			File file = new File(AttachFileApi.getBaseUploadPath() + File.separator + storeUrl);
			File thumbFile = new File(AttachFileApi.getBaseUploadPath() + File.separator + thumbUrl);
			if (FileUtil.exist(thumbFile)) {
				FileUtil.del(thumbFile);
			}
			thumbFile = FileUtil.copy(outPutFilePath, AttachFileApi.getBaseUploadPath() + File.separator + thumbUrl,
					true);
			Thumbnails.of(thumbFile).size(300, 500).toFile(thumbFile);
//			if (outPutFile.length() / 1024 > 1024) { // 大于1M
//				// 压缩图片
//				Thumbnails.of(outPutFile).size(300, 500).toFile(outPutFile);
//			}
			outPutFile.renameTo(file);
			long fileSize = file.length();

			entity.setStoreUrl(storeUrl);
			entity.setThumbUrl(thumbUrl);
			entity.setFileSize((int) fileSize);
			entity.setThumbFlg("Y");
			entity.update();

		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(RetKit.ok(data));
	}

}
