/**
 * @Description:
 * @author: Administrator
 * @date: 2019年3月14日 上午11:30:26
 */
package com.mj.kit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import cn.hutool.core.io.FileUtil;

/**
 * @author pdf加水印
 *
 */
public class PDFWaterMarkKit{
	
	public static PDFWaterMarkKit kit = new PDFWaterMarkKit(); 
	
	/**
	 * @Description: 给PDF加水印
	 * @param: src:PDF源文件
	 *         dest:PDF加水印文件
	 *         textWaterMaker:水印内容
	 * @return:
	 * @date: 2019年3月14日 下午1:38:18
	 */
	public void doWaterMarker(File pdfFile ,String textWaterMaker) throws IOException, DocumentException {
		String pdfPath = pdfFile.getAbsolutePath();
		PdfReader reader = new PdfReader(pdfPath);
		String dest = StringUtils.replace(pdfPath, ".pdf", "_wm.pdf");
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
		stamper.setRotateContents(false);
		BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.EMBEDDED);
		
		PdfGState gs = new PdfGState();
		gs.setFillOpacity(0.3f);//透明度
		
		int toPage = stamper.getReader().getNumberOfPages();
		
		PdfContentByte content = null;
		Rectangle pageRect = null;
		int row = 3;
		int column = 2;
		for (int i = 1; i <= toPage; i++) {
			pageRect = stamper.getReader().getPageSizeWithRotation(i);
			float rowHeight = pageRect.getHeight()/row;
			float columnWidth = pageRect.getWidth()/column;
			for (int rowIdx = 0; rowIdx < row+1; rowIdx++) {
				float y = rowIdx*rowHeight;
				for( int columnIdx = 0; columnIdx < column+1 ;columnIdx ++) {
					//获得PDF最顶层
					content = stamper.getOverContent(i);
					content.saveState();
					// set Transparency
					content.setGState(gs);
					content.beginText();
					content.setColorFill(BaseColor.LIGHT_GRAY);
					content.setFontAndSize(base, 20);
					// 水印文字成30度角倾斜
					content.showTextAligned(Element.ALIGN_CENTER, textWaterMaker, columnIdx*columnWidth , y, 25);
					content.endText();
				}
				
			}
		}
		stamper.close();
		reader.close();
		FileUtil.rename(new File(dest), pdfPath, false, true);
	}

	/*public static void main(String[] args) throws Exception {
		new PDFWaterMarkKit().doWaterMarker(new File("D://note.pdf"));
		System.out.println("完成");
	}*/
}
