package com.mj.controller.attach;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * 大文件附件上传
 */
public class AttachLargeFileUploadController extends AbstractAttachController {

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
		render("attach_large_file_upload.html");
	}

	private File getChunkFileFolder(String fileMd5) {
		StringBuilder sb = new StringBuilder();
		sb.append(getBaseUploadPath());
		sb.append(File.separator).append(getLoginUserId());
		sb.append(File.separator).append(fileMd5);
		return new File(sb.toString());
	}
	
	private File getChunkFile(String fileMd5, Integer chunk) {
		StringBuilder sb = new StringBuilder();
		sb.append(getBaseUploadPath());
		sb.append(File.separator).append(getLoginUserId());
		sb.append(File.separator).append(fileMd5);
		sb.append(File.separator).append(chunk);
		File f =  new File(sb.toString());
		return f;
	}
	
	/**
	 * 校验文件MD5，判断是否已经上传,可实现秒传功能
	 */
	public void checkFileMd5() {
		String fileMd5 = getPara("fileMd5");
		//AttachFile entity = AttachFileApi.api.findByMd5(getLoginUserCorpId(),fileMd5);
		//if(entity != null) {
		//	//新创建一条数据
		//	Map<String,String> data = Maps.newHashMap();
		//	data.put("attachId", entity.getId());
		//	data.put("fileName", entity.getFileName());
		//	data.put("fileSuffix", entity.getFileSuffix());
		//	renderJson(RetKit.ok(data));
		//	return;
		//}
		File dir = getChunkFileFolder(fileMd5);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		renderJson(RetKit.ok());
	}

	/**
	 * 分片发送前校验文件，分片若已存在则跳过
	 * @author zhaoxin.yuan
	 * @version 2018年4月13日 上午10:03:09
	 */
	public void checkChunk(){
		String fileMd5 = getPara("fileMd5");
		Integer chunk = getParaToInt("chunk");
		Integer chunkSize = getParaToInt("chunkSize");
		//检查当前分块是否上传成功
		File chunkFile = getChunkFile(fileMd5, chunk);
		if(chunkFile.exists() && chunkFile.length() == chunkSize){  
			// 上传过  
			renderJson(RetKit.ok(true));
		}else{
			// 存在分片则删除	
			if(chunkFile.exists()) {
				chunkFile.delete();
			}
			renderJson(RetKit.ok(false));
		}
	}
	
	/**
	 * 执行文件上传
	 */
	public void doUpload() {
		UploadFile upload = getFile("file");
		try {
			Integer chunk = getParaToInt("chunk");
			String fileMd5 = getPara("fileMd5");
			
			File dir = getChunkFileFolder(fileMd5);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			File chunkFile = getChunkFile(fileMd5, chunk);
			upload.getFile().renameTo(chunkFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderNull();
	}

	/**
	 * 大文件合并
	 */
	public void mergeChunks(){
		Ret ret = null;
		//合并文件
		String fileMd5 = getPara("fileMd5");
		String fileName = getPara("fileName");
		Integer fileSize = getParaToInt("fileSize");
		
		AttachFile entity = new AttachFile();
		String fileSuffix = FileUtil.getExtensionName(fileName);
		String attachId= KeyKit.uuid24();
		entity.setId(attachId);
		entity.setUserId(getLoginUserId());
		String storeUrl = getFileDir() + File.separator  + attachId+"."+fileSuffix;
		entity.setStoreUrl(storeUrl);
		entity.setFileName(fileName);
		entity.setFileSuffix(StringUtils.lowerCase(fileSuffix));
		entity.setStatus(0);
		entity.setFileSize(fileSize);
		entity.setFileMd5(fileMd5);
		entity.setCreateDate(new Date());
		entity.setThumbFlg("N");

		String storeFileName = getBaseUploadPath() + File.separator +entity.getStoreUrl();
		File storeFile = new File(storeFileName);
		try {
			mergeChunk(getChunkFileFolder(fileMd5),storeFile);
			
			boolean isImage = false;
			if (IMG_TYPES.contains(entity.getFileSuffix())) {
				isImage = true;
			}
			int imageMinSize = 0;
			if(isImage) {
				BufferedImage image = ImageIO.read(storeFile);  
				int imageWidth = image.getWidth();
				int imageHeitht = image.getHeight();

				imageMinSize = imageWidth;
				if(imageMinSize > imageHeitht) {
					imageMinSize = imageHeitht;
				}

				int imageChangeSize = imageMinSize;
				if(imageMinSize > IMAGE_STORE_MAX_SIZE) {
					imageChangeSize = IMAGE_STORE_MAX_SIZE;
					Thumbnails.of(storeFile).size(imageChangeSize, imageChangeSize).toFile(storeFile);
				}

				String storeThumbUrl = getFileDir() + File.separator + attachId+"_thumb."+fileSuffix;
				File storeThumbFile = new File(getBaseUploadPath() +File.separator +storeThumbUrl);
				entity.setThumbFlg("Y");
				entity.setThumbUrl(storeThumbUrl);
				Thumbnails.of(storeFile).sourceRegion(Positions.CENTER, imageMinSize, imageMinSize).size(200, 200).toFile(storeThumbFile);
			}
			// 是否为图片，如果是图片则显示缩略图
			entity.save();
			Map<String,String> data = Maps.newHashMap();
			data.put("attachId", attachId);
			data.put("fileName", entity.getFileName());
			data.put("fileSuffix", fileSuffix);
			data.put("url", "/attach/download?attachId=" + attachId);
			ret = RetKit.ok(data);
			
		} catch (IOException e) {
			e.printStackTrace();
			ret= RetKit.fail("上传大文件失败 ！");
		}
		renderJson(ret);
		return;
	
	}

	@SuppressWarnings("resource")
	private static void mergeChunk(File dir, File output) throws IOException {
		File[] fileArray = dir.listFiles(new FileFilter(){  
			//排除目录只要文件  
			@Override  
			public boolean accept(File pathname) {  
				if(pathname.isDirectory()){  
					return false;
				}  
				return true;  
			}  
		});
		//转成集合，便于排序  
		List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));  
		Collections.sort(fileList,new Comparator<File>() {  
			@Override  
			public int compare(File o1, File o2) {  
				if(Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())){  
					return -1;  
				}  
				return 1;  
			}  
		});
		// 目标文件安排
		if(output.exists()) { 
			output.delete();
		}
		output.createNewFile();  
		//输出流  
		FileChannel outChnnel = new FileOutputStream(output).getChannel();  
		//合并  
		FileChannel inChannel;  
		for(File file : fileList) {
			inChannel = new FileInputStream(file).getChannel();  
			inChannel.transferTo(0, inChannel.size(), outChnnel);  
			inChannel.close();
			file.delete();
		}
		outChnnel.close();

		File userDir = dir.getParentFile();
		//清除文件夹
		if(dir.isDirectory() && dir.exists()){  
			dir.delete();
		}
		
		if(userDir.isDirectory() && userDir.exists()){  
			userDir.delete();
		}
	}
}
