/**
 * Copyright (c) 2011-2019, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinal.core;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.Action;
import com.jfinal.core.Const;
import com.jfinal.core.Controller;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * ActionReporter
 */
public class ActionReporter {

	private static final String title = "\nJFinal-" + Const.JFINAL_VERSION + " action report -------- ";
	private static boolean reportAfterInvocation = true;
	private static int maxOutputLengthOfParaValue = 512;
	private static Writer writer = new SystemOutWriter();

	private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	public static void setReportAfterInvocation(boolean reportAfterInvocation) {
		ActionReporter.reportAfterInvocation = reportAfterInvocation;
	}

	public static void setMaxOutputLengthOfParaValue(int maxOutputLengthOfParaValue) {
		if (maxOutputLengthOfParaValue < 16) {
			throw new IllegalArgumentException("maxOutputLengthOfParaValue must more than 16");
		}
		ActionReporter.maxOutputLengthOfParaValue = maxOutputLengthOfParaValue;
	}

	public static void setWriter(Writer writer) {
		if (writer == null) {
			throw new IllegalArgumentException("writer can not be null");
		}
		ActionReporter.writer = writer;
	}

	public static boolean isReportAfterInvocation(HttpServletRequest request) {
		if (reportAfterInvocation) {
			return true;
		} else {
			String contentType = request.getContentType();
			if (contentType != null && contentType.toLowerCase().indexOf("multipart") != -1) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Report the action
	 * 
	 * @throws NotFoundException
	 */
	public static final void report(String target, Controller controller, Action action) throws NotFoundException {

		CtClass ctClass = ClassPool.getDefault().get(action.getControllerClass().getName());
		String desc = ActionReporterUtil.getMethodDescWithoutName(action.getMethod());
		CtMethod ctMethod = ctClass.getMethod(action.getMethodName(), desc);
		int lineNumber = ctMethod.getMethodInfo().getLineNumber(0);

		StringBuilder sb = new StringBuilder(title).append(sdf.get().format(new Date()))
				.append(" --------------------------\n");
		sb.append("Url         : ").append(controller.getRequest().getMethod()).append(" ").append(target).append("\n");
		Class<? extends Controller> cc = action.getControllerClass();
		sb.append("Controller  : ").append(cc.getName()).append(".(").append(cc.getSimpleName())
				.append(".java:" + lineNumber + ")");
		sb.append("\nMethod      : ").append(action.getMethodName()).append("\n");

		String urlParas = controller.getPara();
		if (urlParas != null) {
			sb.append("UrlPara     : ").append(urlParas).append("\n");
		}

		Interceptor[] inters = action.getInterceptors();
		if (inters.length > 0) {
			sb.append("Interceptor : ");
			for (int i = 0; i < inters.length; i++) {
				if (i > 0)
					sb.append("\n              ");
				Interceptor inter = inters[i];
				Class<? extends Interceptor> ic = inter.getClass();
//				sb.append(ic.getName()).append(".(").append(ic.getSimpleName()).append(".java:1)");
				CtClass icClass = ClassPool.getDefault().get(ic.getName());
				CtMethod icMethod = icClass.getMethod("intercept", "(Lcom/jfinal/aop/Invocation;)V");
				int icLineNumber = icMethod.getMethodInfo().getLineNumber(0);
				sb.append(ic.getName()).append(".(").append(ic.getSimpleName()).append(".java:" + icLineNumber + ")");
			}
			sb.append("\n");
		}

		// print all parameters
		HttpServletRequest request = controller.getRequest();
		Enumeration<String> e = request.getParameterNames();
		if (e.hasMoreElements()) {
			sb.append("Parameter   : ");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				String[] values = request.getParameterValues(name);
				if (values.length == 1) {
					sb.append(name).append("=");
					if (values[0] != null && values[0].length() > maxOutputLengthOfParaValue) {
						sb.append(values[0].substring(0, maxOutputLengthOfParaValue)).append("...");
					} else {
						sb.append(values[0]);
					}
				} else {
					sb.append(name).append("[]={");
					for (int i = 0; i < values.length; i++) {
						if (i > 0)
							sb.append(",");
						sb.append(values[i]);
					}
					sb.append("}");
				}
				sb.append("  ");
			}
			sb.append("\n");
		}
		sb.append("--------------------------------------------------------------------------------\n");

		try {
			writer.write(sb.toString());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static class SystemOutWriter extends Writer {
		public void write(String str) throws IOException {
			System.out.print(str);
		}

		public void write(char[] cbuf, int off, int len) throws IOException {
		}

		public void flush() throws IOException {
		}

		public void close() throws IOException {
		}
	}

}
