package com.mj.event;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cn.hutool.core.io.FileUtil;
import net.dreamlu.event.core.ApplicationEvent;
import net.dreamlu.event.core.EventListener;

public class DelLocalFileListener {
	//执行发送处理
	@SuppressWarnings("unchecked")
	@EventListener(events = DelLocalFileEvent.class,async = true)
	public void doListen(ApplicationEvent event) {
		Map<String, Object> param = (Map<String, Object>) event.getSource();
		File downloadFile = (File)param.get("downloadFile");
		Integer overdueDays = (Integer)param.get("overdueDays");
		if(downloadFile == null || !downloadFile.exists()){
			return;
		}
		if(overdueDays == null){
			overdueDays = 3;//默认为3天
		}
		
		Integer splitIdx = StringUtils.indexOf(downloadFile.getName(), "_");
		if(splitIdx < 0){
			splitIdx = 3;//截取前3个字符
		}
		String prefix = StringUtils.substring(downloadFile.getName(), 0, splitIdx);
		List<File> _files = FileUtil.loopFiles(downloadFile.getParentFile().getAbsolutePath());
		if(!_files.isEmpty()) {
			Long now = (new Date()).getTime();
			for(File _file : _files) {
				if(!StringUtils.startsWith(_file.getName(), prefix)){
					continue;
				}
				if(now - _file.lastModified() > 1000*60*60*overdueDays) {
					FileUtil.del(_file);
				}
			}
		}
	}
}