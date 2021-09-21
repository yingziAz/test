package com.mj.controller.attach;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.mj.kit.FileUtil;
import com.mj.kit.KeyKit;
import com.mj.model.base.AttachFile;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import sun.misc.BASE64Decoder;

/**
 * 附件上传
 */
public class AttachUploadController extends AbstractAttachController {

	private static List<String> IMG_TYPES = Arrays.asList("png", "jpg", "jpeg", "gif", "bmp");

	private static int IMAGE_STORE_MAX_SIZE = 1000;

	public void index() {
		// attachField 附件对应的id名称
		setAttr("attachField", getPara("attachField"));
		// attachDivId 附件上一层最大的divId
		setAttr("attachDivId", getPara("attachDivId"));
		// attachNum 附件的最大个数最大的个数 如果值为1，则默认是单张上传
		setAttr("attachNum", getParaToInt("attachNum", 1));
		// fileType a:无限制 w:word i:image e:excel 默认无限制：a
		setAttr("fileType", getPara("fileType", "a"));
		// 位置 m:中部 b:底部 默认中部
		setAttr("position", getPara("position", "m"));
		render("attach_file_upload.html");
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
	 * 大文件上传
	 */
	public void doUploadLargeData() {
		try {
			InputStream input = this.getRequest().getInputStream();
			if (input == null) {
				renderJson(RetKit.fail("未选择上传文件！"));
				return;
			}
			
			Map<String, Object> result = storeAvatar(input);
			Ret ret = RetKit.ok(result);
			renderJson(ret);
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
//				if(imageMinSize > IMAGE_STORE_MAX_SIZE) {
//					imageChangeSize = IMAGE_STORE_MAX_SIZE;
//					
//					Thumbnails.of(tempFile).size(imageChangeSize, imageChangeSize).toFile(storeFile);
//				}else {
					tempFile.renameTo(storeFile);
//				}

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
	
	public static Map<String, Object> storeAvatar(InputStream is) {
		String attachFileId = KeyKit.uuid24();
		Map<String, Object> map = new HashMap<>();
		try {
			String base64Str = inputStream2String(is);
			System.out.println(base64Str);  
			if(StringUtils.isBlank(base64Str)){
				return map;
			}

			AttachFile entity = new AttachFile();
			entity.setId(attachFileId);
			String storeUrl = getFileDir() + "/" + attachFileId + ".png";
			entity.setStoreUrl(storeUrl);

			// 输出的文件流  
			File sf = new File(getFileDir()+"\\");  
			if(!sf.exists()){  
				sf.mkdirs();  
			}  
			OutputStream os = new FileOutputStream(getBaseUploadPath()+File.separator+storeUrl);  
			// 开始读取  
			BASE64Decoder decoder = new BASE64Decoder(); 
			byte[] b = decoder.decodeBuffer(base64Str.split(",")[1]);  
			for(int i=0;i<b.length;++i){  
				if(b[i]<0)  
				{//调整异常数据  
					b[i]+=256;  
				}  
			}  
			os.write(b); 
			// 完毕，关闭所有链接  
			os.close();  
			is.close();  

			File file = new File(getBaseUploadPath()+File.separator+storeUrl);
			String fileName = attachFileId + ".png";
			String fileType = FileUtil.getExtensionName(fileName);
			entity.setFileName(fileName);
			entity.setFileSuffix(StringUtils.lowerCase(fileType));
			entity.setStatus(0);
			entity.setCreateDate(new Date());

			long fileSize = file.length();
			entity.setFileSize((int) fileSize);
//			String filename = getBaseUploadPath() + "/" + entity.getStoreUrl();
//			checkFileDir(filename);
			//缩略图
			String thumbUrl =getFileDir() + "/" + attachFileId + "_thumb."+entity.getFileSuffix();
			Thumbnails.of(file).scale(0.20f).toFile(getBaseUploadPath() + "/" +thumbUrl);
			entity.setThumbUrl(thumbUrl);
			entity.setThumbFlg("Y");
			entity.save();
			
			
			map.put("attachId", attachFileId);
			map.put("fileName", entity.getFileName());
			map.put("fileSuffix", entity.getFileSuffix());
			map.put("url", "/attach/download?attachId=" + attachFileId);			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return map;
	}
}
