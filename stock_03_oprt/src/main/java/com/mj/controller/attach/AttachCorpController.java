package com.mj.controller.attach;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.mj.api.attach.AttachFileApi;
import com.mj.kit.ConfigKit;
import com.mj.kit.FileUtil;
import com.mj.kit.KeyKit;
import com.mj.model.base.AttachFile;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * 附件上传
 */
public class AttachCorpController extends AbstractAttachController {

	private static List<String> IMG_TYPES = Arrays.asList("png", "jpg", "jpeg", "gif", "bmp");

	private static int IMAGE_STORE_MAX_SIZE = 1000;

	public void index() {
		// attachField 附件对应的id名称
		setAttr("attachField", getPara("attachField"));
		// attachDivId 附件上一层最大的divId
		setAttr("attachDivId", getPara("attachDivId"));
		// attachNum 附件的最大个数最大的个数 如果值为1，则默认是单张上传
		setAttr("attachNum", getParaToInt("attachNum", 1));
		// 附件显示的外框宽度
		setAttr("outWidth", getParaToInt("outWidth",150));
		// 附件显示的内宽宽度
		setAttr("inlineWidth", getParaToInt("inlineWidth",135));
		// 附件显示的内宽高度
		setAttr("inlineHeigth", getParaToInt("inlineHeigth",135));
		// 文件上传的最小宽度
		setAttr("min_width", getParaToInt("min_width",150));
		// 文件上传的最小高度
		setAttr("min_height", getParaToInt("min_height",150));
		// 位置 m:中部 b:底部 默认中部
		setAttr("position", getPara("position", "m"));
		render("attach_corp_upload.html");
	}

	/**
	 * 执行图片上传处理
	 */
	public void doImageUpload() {
		UploadFile uploadFile = getFile();
		if (uploadFile == null) {
			renderJson(RetKit.fail("未选择上传图片！"));
			return;
		}
		if (uploadFile != null) {
			Map<String,String> result = storeUploadFile(uploadFile);
			Ret ret = RetKit.ok(result);
			renderJson(ret);
			return;
		}
	}

	/**
	 * 执行文件上传处理
	 */
	public void doFileUpload() {
		UploadFile uploadFile = getFile();
		if (uploadFile == null) {
			renderJson(RetKit.fail("未选择上传文件！"));
			return;
		}
		if (uploadFile != null) {
			Map<String,String> result = storeUploadFile(uploadFile);
			Ret ret = RetKit.ok(result);
			renderJson(ret);
			return;
		}
	}
	
	/**
	 * <p>Description: 上传到云端</p>
	 * @author yjc
	 * @date 2020年2月17日
	 */
	public void updateCloud(){
		UploadFile uploadFile = getFile("file");
		if (uploadFile == null) {
			renderJson(RetKit.fail("未选择上传文件！"));
			return;
		}

		Map<String,String> result = storeUploadFile(uploadFile);
		String attachId = result.get("attachId");
		
		AttachFile file= AttachFileApi.api.findById(attachId);
		
		Ret ret = RetKit.ok();
		ret.set("attachId", attachId);
		ret.set("storeUrl", file.getStoreUrl());
		ret.set("fileName", uploadFile.getOriginalFileName());
		ret.set("fileSuffix",FileUtil.getExtensionName(uploadFile.getOriginalFileName()));
		ret.set("url", ConfigKit.api.getAliCosDomain() + file.getQcloudUrl());
		renderJson(ret);
		return;
	}
	

	public Map<String,String> storeUploadFile(UploadFile file) {

		Map<String,String> data = Maps.newHashMap();
		String attachId= KeyKit.uuid24();
		try {
			AttachFile entity = new AttachFile();
			String fileName = file.getOriginalFileName();
			String fileSuffix = FileUtil.getExtensionName(fileName);

			entity.setId(attachId);
			entity.setUserId(getLoginUserId());

			String storeUrl = getFileDir() + File.separator  + attachId+"."+fileSuffix;

			entity.setStoreUrl(storeUrl);
			entity.setFileName(fileName);
			entity.setFileSuffix(StringUtils.lowerCase(fileSuffix));
			entity.setStatus(0);
			entity.setCreateDate(new Date());
			entity.setThumbFlg("N");

			long fileSize = file.getFile().length();
			entity.setFileSize((int) fileSize);

			File tempFile = file.getFile();
			
			String storeFileName = getBaseUploadPath() + File.separator +entity.getStoreUrl();
			checkFileDir(storeFileName);

			boolean isImage = false;
			if (IMG_TYPES.contains(entity.getFileSuffix())) {
				isImage = true;
			}

			File storeFile =  new File(storeFileName);
			int imageMinSize = 0;
			if(isImage) {
				
				//jpg 文件过大处理不了           modify by lu 20190819
				
//				BufferedImage image = ImageIO.read(file.getFile());  
				BufferedImage image =null;
				if(StringUtils.equals(entity.getFileSuffix(), "jpg") ) {
					//jpg文件特殊处理  ImageIO.read()处理jpg时有些特定色素无法处理
					File srcImageFileGood = storeFile;
					JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(new FileInputStream(tempFile));
					BufferedImage rImage = decoder.decodeAsBufferedImage();
					ImageIO.write(rImage, "JPEG", srcImageFileGood);
					
					image=ImageIO.read(srcImageFileGood);  
				}else {
					image = ImageIO.read(file.getFile());  
				}
//				
				

				int imageWidth = image.getWidth();
				int imageHeitht = image.getHeight();

				imageMinSize = imageWidth;
				if(imageMinSize > imageHeitht) {
					imageMinSize = imageHeitht;
				}

				int imageChangeSize = imageMinSize;
				if(imageMinSize > IMAGE_STORE_MAX_SIZE) {
					imageChangeSize = IMAGE_STORE_MAX_SIZE;
					
					Thumbnails.of(tempFile).size(imageChangeSize, imageChangeSize).toFile(storeFile);
				}else {
					tempFile.renameTo(storeFile);
				}

				String storeThumbUrl = getFileDir() + File.separator + attachId+"_thumb."+fileSuffix;
				File storeThumbFile = new File(getBaseUploadPath() +File.separator +storeThumbUrl);
				entity.setThumbFlg("Y");
				entity.setThumbUrl(storeThumbUrl);
				
				Thumbnails.of(storeFile).sourceRegion(Positions.CENTER, imageMinSize, imageMinSize).size(200, 200).toFile(storeThumbFile);
					
			}else {
				tempFile.renameTo(storeFile);
			}
			// 是否为图片，如果是图片则显示缩略图
			entity.save();

			data.put("attachId", attachId);
			data.put("fileName", file.getOriginalFileName());
			data.put("fileSuffix", fileSuffix);
			data.put("url", "/attach/download?attachId=" + attachId);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return data;
	}
	
	public static String inputStream2String(InputStream is) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null){
			buffer.append(line);
		}
		return buffer.toString();
	}
	
}
