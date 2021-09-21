package com.mj.controller.attach;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.JFinal;
import com.mj.controller.BaseController;
import com.mj.kit.DateUtil;
import com.mj.model.base.AttachFile;

/**
 * 附件控制类
 * @author daniel.su
 *
 */
public abstract class AbstractAttachController extends BaseController {
	
	protected static List<String> IMG_TYPES = Arrays.asList("png", "jpg", "jpeg", "gif", "bmp");
	protected static int IMAGE_STORE_MAX_SIZE = 1000;
	

	public static String getBaseUploadPath() {
		//获取上传跟路径
		return JFinal.me().getConstants().getBaseUploadPath();
	}


	public static String getFileDir() {
		String fileDir = DateUtil.dateToStr(new Date(), "yyyyMM");
		File folder = new File(getBaseUploadPath() + File.separator  + fileDir);
		if (!folder.exists() && !folder.mkdirs()) {
			throw new RuntimeException("创建文件目录失败！");
		}
		return fileDir;
	}

	protected static void checkFileDir(String filename) {
		String dirname = filename.substring(0,filename.lastIndexOf(File.separator));
		File dir = new File(dirname);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new RuntimeException("创建文件目录失败！");
		}
	}


	protected static InputStream readFile(String attachFileId) {
		try {
			AttachFile attachFile = AttachFile.dao.findById(attachFileId);
			if(attachFile == null) throw new RuntimeException("文件不存在");
			String fileName = getBaseUploadPath() + File.separator  + attachFile.getStoreUrl();
			return new FileInputStream(fileName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected static InputStream readFile(AttachFile attachFile) {
		try {
			if(attachFile == null) throw new RuntimeException("文件不存在");
			String fileName = getBaseUploadPath() + File.separator  + attachFile.getStoreUrl();

			File file = new File(fileName);

			if(file.exists()) {
				return new FileInputStream(file);
			}else {
				if(StringUtils.isNotEmpty(attachFile.getQcloudUrl())) {
					URL url = new URL(attachFile.getQcloudUrl());
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();//利用HttpURLConnection对象,我们可以从网络中获取网页数据.  
					conn.setDoInput(true);  
					conn.connect();  
					return conn.getInputStream();

				}else {
					throw new RuntimeException("文件不存在");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected static InputStream readThumbFile(AttachFile attach) {
		try {
			if(attach == null) throw new RuntimeException("文件不存在");


			String fileName = getBaseUploadPath() + File.separator  + attach.getThumbUrl();
			File file = new File(fileName);
			if(file.exists()) {
				return new FileInputStream(file);
			}else {

				if(StringUtils.isNotEmpty(attach.getQcloudThumbUrl())) {
					URL url = new URL(attach.getQcloudThumbUrl());
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();//利用HttpURLConnection对象,我们可以从网络中获取网页数据.  
					conn.setDoInput(true);  
					conn.connect();  
					return conn.getInputStream();
				}else {

					throw new RuntimeException("文件不存在");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

}
