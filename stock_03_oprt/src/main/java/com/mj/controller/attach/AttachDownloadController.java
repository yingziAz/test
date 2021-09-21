package com.mj.controller.attach;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.Clear;
import com.mj.model.base.AttachFile;

/**
 * 附件上传
 */

@Clear
public class AttachDownloadController extends AbstractAttachController {
	@Clear
	public void index() throws UnsupportedEncodingException {
		String attachId = this.getPara("attachId");
		AttachFile attachFile = AttachFile.dao.findById(attachId);
		if (attachFile == null)
			throw new RuntimeException("文件不存在");
		try {
			HttpServletResponse resp = this.getResponse();
			resp.setContentType("application/octet-stream;charset=GB18030");
			String fileName = attachFile.getFileName();
			resp.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("GB18030"), "ISO-8859-1"));

			InputStream in = null;
			in = readFile(attachFile);
			byte[] b = new byte[8192];
			int len;
			while ((len = in.read(b)) > 0) {
				resp.getOutputStream().write(b, 0, len);
			}
			in.close();
			resp.getOutputStream().flush();
			resp.getOutputStream().close();
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderNull();
	}

	/**
	 * 缩略图
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Clear
	public void thumb() throws UnsupportedEncodingException {

		String attachId = this.getPara("attachId");
		AttachFile attachFile = AttachFile.dao.findById(attachId);
		if (attachFile == null)
			throw new RuntimeException("文件不存在");

		try {
			HttpServletResponse resp = this.getResponse();
			resp.setContentType("application/octet-stream;charset=GB18030");
			String fileName = attachFile.getFileName();
			resp.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("GB18030"), "ISO-8859-1"));

			InputStream in = null;
			in = readThumbFile(attachFile);
			byte[] b = new byte[8192];
			int len;
			while ((len = in.read(b)) > 0) {
				resp.getOutputStream().write(b, 0, len);
			}
			in.close();
			resp.getOutputStream().flush();
			resp.getOutputStream().close();
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderNull();
	}

}
