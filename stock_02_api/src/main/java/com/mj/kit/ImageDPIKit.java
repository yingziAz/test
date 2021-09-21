package com.mj.kit;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Element;

public class ImageDPIKit {
	// 1英寸=2.54
	private static final double INCH_2_CM = 2.54d;
	private static final String PNG = "png";
	private static final String JPEG = "jpeg";

	public static byte[] processPNG(BufferedImage image, int dpi) throws MalformedURLException, IOException {
		
		for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(PNG); iw.hasNext();) {
			ImageWriter writer = iw.next();
			ImageWriteParam writeParam = writer.getDefaultWriteParam();
			ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier
					.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
			IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
			if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
				continue;
			}
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageOutputStream stream = null;
			try {
				setDPI(metadata, dpi);
				stream = ImageIO.createImageOutputStream(output);
				writer.setOutput(stream);
				writer.write(metadata, new IIOImage(image, null, metadata), writeParam);
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
			return output.toByteArray();
		}
		return null;
	}

	/**
	 * @Description:设置dpi
	 * @author zyf
	 * @version 2020年4月26日 下午4:59:35
	 * @param metadata
	 * @param dpi
	 * @throws IIOInvalidTreeException
	 */
	private static void setDPI(IIOMetadata metadata, int dpi) throws IIOInvalidTreeException {
		double dotsPerMilli = 1.0 * dpi / 10 / INCH_2_CM;
		IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
		horiz.setAttribute("value", Double.toString(dotsPerMilli));

		IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
		vert.setAttribute("value", Double.toString(dotsPerMilli));

		IIOMetadataNode dim = new IIOMetadataNode("Dimension");
		dim.appendChild(horiz);
		dim.appendChild(vert);

		IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
		root.appendChild(dim);

		metadata.mergeTree("javax_imageio_1.0", root);
	}

	public byte[] processJEPG(BufferedImage image, int dpi) throws IOException {
		for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(JPEG); iw.hasNext();) {
			ImageWriter writer = iw.next();

			ImageWriteParam writeParams = writer.getDefaultWriteParam();
			writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			// 调整图片质量
			writeParams.setCompressionQuality(1f);

			IIOMetadata data = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), writeParams);
			Element tree = (Element) data.getAsTree("javax_imageio_jpeg_image_1.0");
			Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
			jfif.setAttribute("Xdensity", dpi + "");
			jfif.setAttribute("Ydensity", dpi + "");
			jfif.setAttribute("resUnits", "1"); // density is dots per inch

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageOutputStream stream = null;
			try {
				stream = ImageIO.createImageOutputStream(out);
				writer.setOutput(stream);
				writer.write(data, new IIOImage(image, null, null), writeParams);
			} finally {
				stream.close();
			}

			return out.toByteArray();
		}
		return null;

	}

	public static void main(String[] args) {
		String outPutPath = "E:\\git\\session_data\\至高无上服装有限公司 (1).png";
		String path = "E:\\git\\session_data\\dpi300.png";
		try {

			byte[] output = processPNG(ImageIO.read(new File(outPutPath)), 300);
			if (output != null) {
				@SuppressWarnings("resource")
				FileOutputStream fileOutputStream = new FileOutputStream(path);
				fileOutputStream.write(output);
				System.out.println("success path-----" + outPutPath);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
