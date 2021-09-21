package com.baidu.ueditor.manager;

import java.io.InputStream;
import java.util.Map;

import com.baidu.ueditor.define.State;
/**
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.BucketManager.FileListIterator;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
*/
public class QiniuFileManager extends AbstractFileManager {

    @Override
    public State list(Map<String, Object> conf, int start) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public State saveFile(byte[] data, String rootPath, String savePath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public State saveFile(InputStream is, String rootPath, String savePath,
            long maxSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public State saveFile(InputStream is, String rootPath, String savePath) {
        // TODO Auto-generated method stub
        return null;
    }
	
    /**
	private final Auth auth;
	private final String bucket;
	private UploadManager uploadManager;
	private BucketManager bucketManager;
	
	public QiniuFileManager(String ak, String sk, String bucket) {
		auth = Auth.create(ak, sk);
		this.bucket = bucket;
		uploadManager = new UploadManager();
		bucketManager = new BucketManager(auth);
	}
	
	private String getUpToken(){
		return auth.uploadToken(bucket);
	}
	
	private State getState(String[] keys) {
		MultiState state = new MultiState(true);
		BaseState fileState = null;

		for (String key : keys) {
			if (key == null) {
				break;
			}
			fileState = new BaseState(true);
			fileState.putInfo("url", key);
			state.addState(fileState);
		}

		return state;
	}
	
	@Override
	public State list(Map<String, Object> conf, int start) {
		String dirPath = (String) conf.get("dir");
		List<String> allowFiles = getAllowFiles(conf.get("allowFiles"));
		int count = (Integer) conf.get("count");
		
		if (dirPath.startsWith("/")) {
			dirPath = dirPath.substring(1);
		}
		
		FileListIterator it = bucketManager.createFileListIterator(bucket, dirPath, count, null);
		List<String> list = new ArrayList<String>();
		while (it.hasNext()) {
			FileInfo[] items = it.next();
			for (FileInfo fileInfo : items) {
				String key = fileInfo.key;
				String ext = FilenameUtils.getExtension(key);
				if (allowFiles.contains(ext)) {
					list.add("/" + fileInfo.key);
				}
			}
		}
		
		Collections.reverse(list);
		
		State state = null;
		if (start < 0 || start > list.size()) {
			state = new MultiState(true);
		} else {
			String[] fileList = Arrays.copyOfRange(list.toArray(new String[]{}), start, start + count);
			state = getState(fileList);
		}

		state.putInfo("start", start);
		state.putInfo("total", list.size());
		return state;
	}

	@Override
	public State saveFile(byte[] data, String rootPath, String savePath) {
		if (savePath.startsWith("/")) {
			savePath = savePath.substring(1);
		}
		
		try {
			uploadManager.put(data, savePath, getUpToken());
		} catch (QiniuException e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		}
		State state = new BaseState(true);
		state.putInfo("size", data.length);
		state.putInfo("title", getFileName(savePath));
		return state;
	}
	
	@Override
	public State saveFile(InputStream is, String rootPath, String savePath, long maxSize) {
		if (savePath.startsWith("/")) {
			savePath = savePath.substring(1);
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] data = null;
		try {
			IOUtils.copy(is, output);
			data = output.toByteArray();
			if (data.length > maxSize) {
				return new BaseState(false, AppInfo.MAX_SIZE);
			}
			uploadManager.put(data, savePath, getUpToken());
		} catch (IOException e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(is);
		}
		State state = new BaseState(true);
		state.putInfo("size", data.length);
		state.putInfo("title", getFileName(savePath));
		return state;
	}

	@Override
	public State saveFile(InputStream is, String rootPath, String savePath) {
		if (savePath.startsWith("/")) {
			savePath = savePath.substring(1);
		}
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] data = null;
		try {
			IOUtils.copy(is, output);
			data = output.toByteArray();
			uploadManager.put(data, savePath, getUpToken());
		} catch (IOException e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(is);
		}
		State state = new BaseState(true);
		state.putInfo("size", data.length);
		state.putInfo("title", getFileName(savePath));
		return state;
	}

	private String getFileName(String savePath) {
		return FilenameUtils.getBaseName(savePath);
	}
	*/

}
