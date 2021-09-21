package com.mj.api.attach;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.core.JFinal;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.mj.config.RedisCacheConfig;
import com.mj.constant.CoreConstant;
import com.mj.constant.ResponseFail;
import com.mj.kit.DateUtil;
import com.mj.kit.FileUtil;
import com.mj.kit.KeyKit;
import com.mj.model.base.AttachFile;

import net.coobird.thumbnailator.Thumbnails;
import sun.misc.BASE64Decoder;

/**
 * 附件APi Api
 * 
 * @author daniel.su
 * 		
 */
public class AttachFileApi {

	private static String REDIS_KEY = CoreConstant.PLATFORM_REDIS_PREFIX+"ATTACH";

	public static final AttachFileApi api = new AttachFileApi();

	public static String getBaseUploadPath() {
		//获取上传跟路径
		return JFinal.me().getConstants().getBaseUploadPath();
	}

	protected static void checkFileDir(String filename) {
		String dirname = filename.substring(0, filename.lastIndexOf(File.separator));
		File dir = new File(dirname);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new RuntimeException("创建文件目录失败！");
		}
	}

	/**
	 * 清洗附件ID
	 * @param attachIds
	 * @return
	 */
	public String cleanAttachIds(String attachIds){
		if(StringUtils.isEmpty(attachIds)){
			return null;
		}
		String[] attachIdArr = StringUtils.split(attachIds,",");
		List<String> eftvAttachIds = new ArrayList<String>(attachIdArr.length);
		for(String attachId : attachIdArr){
			if(StringUtils.isEmpty(attachId)){
				continue;
			}
			if(AttachFile.dao.isAttachIdExsits(attachId)){
				eftvAttachIds.add(attachId);
			}
		}
		if(eftvAttachIds.isEmpty()){
			return null;
		}
		return StringUtils.join(eftvAttachIds,",");
	}

	public List<AttachFile> findByParam(String attachIds){
		List<AttachFile> list = AttachFile.dao.findByParams(attachIds);
		return list;
	}
	
	public List<Record> findByAttachIds(String attachIds){
		List<Record> list = AttachFile.dao.findByParam(attachIds);
		return list;
	}

	public void remove(String attachIds){
		List<AttachFile> attaches = findByParam(attachIds);
		if(attaches != null && !attaches.isEmpty()){
			for(AttachFile attach : attaches){
				if(attach == null){
					continue;
				}
				attach.delete();
			}
		}
	}

	/**
	 * 保存重命名
	 * @param entity
	 * @return
	 */
	public Ret update4Rename(AttachFile entity){
		boolean result = false;
		AttachFile uEntity = findById(entity.getId());
		if(uEntity == null){
			return RetKit.fail(ResponseFail.INPUT_PARAM_ILLEGAL);
		}
		uEntity.setFileName(entity.getFileName());
		result = uEntity.update();
		return result ? RetKit.ok() : RetKit.fail();
	}

	/**
	 * 解析attach file
	 * @param entity
	 * @return
	 */
	public List<AttachFile> parseAttachFile(String imageIds){
		if(StringUtils.isEmpty(imageIds)){
			return null;
		}
		String[] imageIdArr = StringUtils.split(imageIds,",");
		List<AttachFile> list = new ArrayList<AttachFile>(imageIdArr==null?0:imageIdArr.length);
		for(String imageId : imageIdArr){
			if(StringUtils.isEmpty(imageId)){
				continue;
			}
			AttachFile entity = findById(imageId);
			if(entity == null){
				continue;
			}
			entity.put("imgAttach", entity);
			list.add(entity);
		}
		return list;
	}

	/**
	 * 通过ID来获得附件
	 * @param fileId
	 * @return
	 */
	public AttachFile findById(String fileId){
		AttachFile entity = AttachFile.dao.findById(fileId);
		return entity;
	}

	/**
	 * @Description:根据id查找出所有有云端图片的附件
	 * @author lu
	 * @version 2018 年 11 月 20 日  13:19:00 
	 * @param fileId
	 * @return
	 */
	public List<AttachFile> findByIds(List<String> fileIds){
		String fileIds2Str = StringUtils.join(fileIds,"','");
		return AttachFile.dao.find(" select * from base_attach_file where id in ('"+fileIds2Str+"') and qcloud_url is not null ");
	}
	/**
	 * @Description:通过ids获取附件集
	 * @author lu
	 * @version 2019 年 02 月 13 日 11:03:07
	 * @param attachIds
	 * @return
	 */
	public List<AttachFile> findByParams(String attachIds) {
		if (StringUtils.isEmpty(attachIds)) {
			return null;
		}
		String[] attachIdArr = StringUtils.split(attachIds, ",");
		List<AttachFile> list = new ArrayList<AttachFile>(attachIdArr.length);
		for (String attachId : attachIdArr) {
			if (StringUtils.isEmpty(attachId)) {
				continue;
			}
			AttachFile entity = this.findById(attachId);
			if (entity != null) {
				list.add(entity);
			}
		}
		if (list.isEmpty() || list.size() == 0) {
			return null;
		}
		return list;
	}
	/**
	 * @Description:根据id查找出所有有云端图片的附件
	 * @author lu
	 * @version 2018 年 11 月 20 日  13:19:00 
	 * @param fileId
	 * @return
	 */
	public AttachFile findByMd5(String corpId,String fileMd5){
		return AttachFile.dao.findFirst(" select * from base_attach_file where corp_id=? and file_md5= ?",corpId,fileMd5);
	}
	
	
	/**
	 * 获得所有无效attachFile
	 * @Description TODO()
	 * @author pmx
	 * @date 2018年3月9日 上午10:09:30
	 * @return
	 */
	public List<AttachFile> findInvalidAttach(){
		return AttachFile.dao.getInvalidAttach();
	}

	public String getQCloudCosUrl(String attachId,boolean thumb) {
		if(StringUtils.isBlank(attachId)){
			return StringUtils.EMPTY;
		}
		String redisField = ( thumb?(attachId+"_thumb") : attachId);
		Cache redisCache =  Redis.use(RedisCacheConfig.STOCK_CORE_CACHE);
		if(JFinal.me().getConstants().getDevMode()) {
			redisCache.hdel(REDIS_KEY, redisField);
		}
		if(redisCache.hexists(REDIS_KEY,redisField)){
			return redisCache.hget(REDIS_KEY,redisField);
		}
		String returnUrl = StringUtils.EMPTY;
		AttachFile attach = findById(attachId);
		if(attach == null){
			return StringUtils.EMPTY;
		}
		if(!thumb) {
			returnUrl = attach.getQcloudUrl();
		}else {
			returnUrl = attach.getQcloudThumbUrl();
		}
		if(StringUtils.isBlank(returnUrl)){
			returnUrl = attach.getQcloudUrl();
		}
		if(returnUrl != null) {
			redisCache.hset(REDIS_KEY, redisField, returnUrl);
			redisCache.expire(REDIS_KEY, 60*10);
		}
		return returnUrl;
	}


	public static String[] onLineSuffix = new String[]{"doc","docx","xls","xlsx","ppt"};
	
	/**
	 * @Description:在线预览 
	 * @author lu
	 * @version 2019 年 03 月 11 日  15:23:08 
	 * @param fileSuffix
	 * @return
	 */
	public boolean getOnLineView(String fileSuffix){
		if(StringUtils.isEmpty(fileSuffix)){
			return false;
		}
		if(ArrayUtils.contains(onLineSuffix, fileSuffix)){
			return true;
		}
		return false;
	}

	/**
	 * @Description: 保存文件
	 * @author: Administrator
	 * @date: 2018年10月24日 上午8:57:24
	 */
	public Ret saveAttachAndUploadCloud(File file, String storePath, String qcloudCosFolder) {
		boolean result = false;
		AttachFile entity = new AttachFile();
		String attachId = KeyKit.uuid24();
		entity.setId(attachId);
		String fileName = file.getName();
		String fileType = FileUtil.getExtensionName(fileName);
		entity.setFileSuffix(StringUtils.lowerCase(fileType));
		entity.setStoreUrl(storePath);
		entity.setFileName(fileName);
		entity.setStatus(1);
		entity.setCreateDate(new Date());
		long fileSize = file.length();
		entity.setFileSize((int) fileSize);
		entity.setThumbFlg("N");
		result = entity.save();

		//将文件移动至upload 目录下
		String filename = getBaseUploadPath() + "/" + entity.getStoreUrl();
		checkFileDir(filename);
		File uploadedFile = new File(filename);
		file.renameTo(uploadedFile);


		return result ? RetKit.ok(attachId) : RetKit.fail();
	}
	
	public static String getFileDir() {
		String fileDir = DateUtil.dateToStr(new Date(), "yyyyMM");
		File folder = new File(getBaseUploadPath() + File.separator  + fileDir);
		if (!folder.exists() && !folder.mkdirs()) {
			throw new RuntimeException("创建文件目录失败！");
		}
		return fileDir;
	}
	
	/**
	 * 上传base64图片到云端
	 * @param base64Str
	 */
	public String uploadQcloud4Base64(String base64Str){
		String attachFileId = KeyKit.uuid24();
		try {
			//文件夹
			File sf = new File(getFileDir()+"\\");
			if(!sf.exists()){
				sf.mkdirs();
			}
			//base64图片接收
			String outPutFilePath = getBaseUploadPath() + File.separator + attachFileId + ".png";
			File outPutFile = new File(outPutFilePath);
			OutputStream os = new FileOutputStream(outPutFile);
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
			
			AttachFile entity = new AttachFile();
			entity.setId(attachFileId);
			String storeUrl = getFileDir() + "/" + attachFileId + ".cmra";
			entity.setStoreUrl(storeUrl);
			File file = new File(getBaseUploadPath() + File.separator + storeUrl);
			if(outPutFile.length()/1024 > 1024){	//大于1M
				//压缩图片
				Thumbnails.of(outPutFile).size(300, 500).toFile(outPutFile);
			}
			outPutFile.renameTo(file);
			String fileName = attachFileId + ".cmra";
			String fileType = FileUtil.getExtensionName(fileName);
			entity.setFileName(fileName);
			entity.setFileSuffix(StringUtils.lowerCase(fileType));
			entity.setStatus(0);
			entity.setCreateDate(new Date());
			long fileSize = file.length();
			entity.setFileSize((int) fileSize);
			entity.setThumbFlg("N");
			entity.save();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return attachFileId;
	}

	public List<AttachFile> findByCorpId(String corpId) {
		String sql = "select id,store_url,qcloud_url from base_attach_file where corp_id=?";
		return AttachFile.dao.find(sql, corpId);
	}
	
}
