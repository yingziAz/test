package com.mj.kit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;

/**
 * @Description:生成背景png图片
 * @author lu
 * @version 2019 年 09 月 09 日  09:02:34 
 */
public class PngKit {
	
	

	/**
	 * @Description:根据文字，生成背景透明的png图片（未定死长宽）,根据文字长度生成图片
	 * @author lu
	 * @version 2019 年 09 月 09 日  09:03:43 
	 * @param drawStr 文字
	 * @param outFile  输出路径
	 * @return
	 */
	public static Ret createPng(String drawStr,File outFile) {
		try {
			BufferedImage imgMap = createPngIo(null,null,new Font("苹方中等", Font.PLAIN, 60), Color.white, 60, drawStr);  
			if(imgMap==null) {
				return RetKit.fail("图片流生成失败");
			}
			ImageIO.write(imgMap, "PNG", outFile);  
			return RetKit.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return RetKit.fail("图片生成失败");
		}
	}

	/**
	 * @Description:根据字体高度，文字，生成背景透明的png图片（未定死长宽）,根据文字长度生成图片
	 * @author lu
	 * @version 2019 年 09 月 09 日 08:49:31
	 * @param fontHeight 字体高度
	 * @param drawStr 文字
	 * @param outFile  输出路径
	 * @return
	 */
	public static Ret createPng(Integer fontHeight, String drawStr,File outFile) {
		try {
			BufferedImage imgMap = createPngIo(null,null,new Font("苹方中等", Font.PLAIN, fontHeight), Color.white, fontHeight, drawStr);  
			if(imgMap==null) {
				return RetKit.fail("图片流生成失败");
			}
			ImageIO.write(imgMap, "PNG", outFile);  
			return RetKit.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return RetKit.fail("图片生成失败");
		}
	}
	
	/**
	 * @Description:根据字体高度，文字，生成背景透明的png图片（未定死长宽）,根据文字长度生成图片
	 * @author lu
	 * @version 2019 年 09 月 09 日 08:49:31
	 * @param fontHeight 字体高度
	 * @param drawStr 文字
	 * @param outFile  输出路径
	 * @return
	 */
	public static Ret createPng(Integer width, Integer height,Integer fontHeight, String drawStr,File outFile) {
		try {
			BufferedImage imgMap = createPngIo(width,height,new Font("苹方中等", Font.PLAIN, fontHeight), Color.white, fontHeight, drawStr);  
			if(imgMap==null) {
				return RetKit.fail("图片流生成失败");
			}
			ImageIO.write(imgMap, "PNG", outFile);  
			return RetKit.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return RetKit.fail("图片生成失败");
		}
	}

	/**
	 * @Description:根据字体，字体颜色，字体高度，文字,生成背景透明的png图片（未定死长宽）,根据文字长度生成图片
	 * @author lu
	 * @version 2019 年 09 月 09 日 08:54:41
	 * @param font 字体
	 * @param color 字体颜色
	 * @param fontHeight 文字高度
	 * @param drawStr 文字
	 * @return
	 */
	public static BufferedImage createPngIo(Integer width, Integer height,Font font, Color color, Integer fontHeight, String drawStr) {
		try {

			// 获取font的样式应用在str上的整个矩形
			Rectangle2D r = font.getStringBounds(drawStr,
					new FontRenderContext(AffineTransform.getScaleInstance(1, 1), false, false));
			// 获取单个字符的高度
			int unitHeight = (int) Math.floor(r.getHeight());
			// 获取整个str用了font样式的宽度这里用四舍五入后+1保证宽度绝对能容纳这个字符串作为图片的宽度
			if(width==null) {
				width = (int) Math.round(r.getWidth()) + 1;
			}
			// 把单个字符的高度+3保证高度绝对能容纳字符串作为图片的高度
			if(height==null) {
				height = unitHeight + 3;
			}

			BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D gd = buffImg.createGraphics();
			// 设置透明 start
			buffImg = gd.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
			gd = buffImg.createGraphics();
			// 设置透明 end
			gd.setFont(font); // 设置字体
			gd.setColor(color); // 设置颜色
			// gd.drawRect(0, 0, width - 1, height - 1); //画边框
//			gd.drawString(drawStr, (width / 2 - fontHeight * drawStr.length() / 2 )+5, font.getSize()); // 输出文字（中文横向居中）
			 gd.drawString(drawStr,0,fontHeight); //输出文字（中文横向居中）
			return buffImg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
