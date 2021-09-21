package com.mj.kit;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QrCodeKit {

	public static int DEFAULT_QRCODE_WIDTH = 512;
	public static int DEFAULT_QRCODE_HEIGHT = 512;
	public static String DEFAULT_QRCODE_IMAGE_TYPE = "png";

	public static void zxingCodeCreate(String text, String outPutPath) throws WriterException, IOException {
		zxingCodeCreate(text, DEFAULT_QRCODE_WIDTH, DEFAULT_QRCODE_HEIGHT, outPutPath, DEFAULT_QRCODE_IMAGE_TYPE);
	}

	public static void zxingCodeCreate(String text, int width, int height, String outPutPath, String imageType)
			throws WriterException, IOException {
		Map<EncodeHintType, String> his = new HashMap<EncodeHintType, String>();
		// 设置编码字符集
		his.put(EncodeHintType.CHARACTER_SET, "utf-8");
		his.put(EncodeHintType.MARGIN, "0");
		// 1、生成二维码
		BitMatrix encode = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, his);
		encode = deleteWhite(encode);
		// 2、获取二维码宽高
		int codeWidth = encode.getWidth();
		int codeHeight = encode.getHeight();

		// 3、将二维码放入缓冲流
		BufferedImage image = new BufferedImage(codeWidth, codeHeight, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < codeWidth; i++) {
			for (int j = 0; j < codeHeight; j++) {
				// 4、循环将二维码内容定入图片
				image.setRGB(i, j, encode.get(i, j) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		// 判断文件夹是否存在
		String dirname = outPutPath.substring(0, outPutPath.lastIndexOf(File.separator));
		File dir = new File(dirname);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new RuntimeException("创建文件目录失败！");
		}
		File outPutImage = new File(outPutPath);
		// 如果图片不存在创建图片
		if (!outPutImage.exists())
			outPutImage.createNewFile();
		// 5、将二维码写入图片
		ImageIO.write(image, imageType, outPutImage);
	}

	/**
	 * @Description: 二维码加入图片
	 * @author zyf
	 * @version 2020年4月24日 上午10:27:14
	 * @param text
	 * @param qrcodeWidth
	 * @param qrcodeHeight
	 * @param outPutPath
	 * @param imgPath
	 * @param imageType
	 */
	public static void zxingCodeCreate(String text, int qrcodeWidth, int qrcodeHeight, String qrcodePath, int logoWidth,
			int logoHeight, String logoPath, String imageType) throws WriterException, IOException {
		zxingCodeCreate(text, qrcodeWidth, qrcodeHeight, qrcodePath, imageType);

		BufferedImage image = ImageIO.read(new File(qrcodePath));
		Graphics2D g = image.createGraphics();

		/**
		 * 读取Logo图片
		 */
		BufferedImage logo = ImageIO.read(new File(logoPath));

		// 计算图片放置位置
		int x = (image.getWidth() - logoWidth) / 2;
		int y = (image.getHeight() - logoHeight) / 2;

		// 开始绘制图片
		g.drawImage(logo, x, y, logoWidth, logoHeight, null);
		g.drawRoundRect(x, y, logoWidth, logoHeight, 10, 10);
		g.drawRect(x, y, logoWidth, logoHeight);
		g.dispose();

		ImageIO.write(image, imageType, new File(qrcodePath));

	}

	public static BitMatrix deleteWhite(BitMatrix matrix) {
		int[] rec = matrix.getEnclosingRectangle();
		int resWidth = rec[2] + 1;
		int resHeight = rec[3] + 1;
		BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
		resMatrix.clear();
		for (int i = 0; i < resWidth; i++) {
			for (int j = 0; j < resHeight; j++) {
				if (matrix.get(i + rec[0], j + rec[1])) {
					resMatrix.set(i, j);
				}
			}
		}
		return resMatrix;
	}

	public static void main(String[] args) {
		String outPutPath = "E:\\git\\session_data\\至高无上服装有限公司 (1).png";
		try {
			zxingCodeCreate(
					"http://localhost:8087/seller/detail?sellerId=5e7083b8bc3662a43381deb9&fromScene=qrcode_seller",
					220, 220, outPutPath, "png");
			System.out.println("success path-----" + outPutPath);
			outPutPath = "E:\\git\\session_data\\" + System.currentTimeMillis() + "_product.png";
			zxingCodeCreate(
					"http://localhost:8087product/detail?productId=5e7083b8bc3662a43381deb9&fromScene=qrcode_product",
					115, 115, outPutPath, "png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
