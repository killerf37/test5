package com.ginfon.core.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.util.Date;
import java.util.Iterator;

/**
 * @author sqp123
 * @date 2018/7/5 16:30
 */
public class ImageUtils {

	/***
	 * 	获取图片的类型
	 * 
	 * @param o
	 * @return
	 * @date 2018/7/5 16:30
	 */
	public static String getFormatName(Object o) {
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(o);
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				return null;
			}
			ImageReader reader = iter.next();
			iis.close();
			return reader.getFormatName();
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * 	使用ImageReader获取图片尺寸
	 *
	 * @param src 源图片路径
	 */
	public void getImageSizeByImageReader(String src) {
		long beginTime = new Date().getTime();
		File file = new File(src);
		try {
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = (ImageReader) readers.next();
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			reader.setInput(iis, true);
			System.out.println("width:" + reader.getWidth(0));
			System.out.println("height:" + reader.getHeight(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
		long endTime = new Date().getTime();
		System.out.println("使用[ImageReader]获取图片尺寸耗时：[" + (endTime - beginTime) + "]ms");
	}

	/**
	 * 	使用BufferedImage获取图片尺寸
	 *
	 * @param src 源图片路径
	 */
	public void getImageSizeByBufferedImage(String src) {
		long beginTime = new Date().getTime();
		File file = new File(src);
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		BufferedImage sourceImg = null;
		try {
			sourceImg = ImageIO.read(is);
			System.out.println("width:" + sourceImg.getWidth());
			System.out.println("height:" + sourceImg.getHeight());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		long endTime = new Date().getTime();
		System.out.println("使用[BufferedImage]获取图片尺寸耗时：[" + (endTime - beginTime) + "]ms");
	}

	/***
	 * 	获取图片的宽
	 * 
	 * @param srcImageFile
	 * @return
	 * @date 2018/7/5 16:30
	 */
	public static int getImageWidth(String srcImageFile) {
		try {
			File file = new File(srcImageFile);
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = (ImageReader) readers.next();
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			reader.setInput(iis, true);

			return reader.getWidth(0);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/***
	 * 	获取文件的高
	 * 
	 * @param srcImageFile
	 * @return
	 * @date 2018/7/5 16:30
	 */
	public static int getImageHeight(String srcImageFile) {
		File file = new File(srcImageFile);
		try {
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = (ImageReader) readers.next();
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			reader.setInput(iis, true);

			return reader.getHeight(0);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/***
	 * 	剪裁图片
	 * 
	 * @param src
	 * @param dest
	 * @param x    起点横坐标
	 * @param y    纵坐标
	 * @param w    长
	 * @param h    高
	 * @throws IOException
	 * @date 2018/7/5 16:30
	 */
	public static boolean cutImage(String src, String dest, int x, int y, int w, int h) {
		try {
			Iterator<?> iterator = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = (ImageReader) iterator.next();
			InputStream in = new FileInputStream(src);
			ImageInputStream iis = ImageIO.createImageInputStream(in);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			x = (x > 0 ? x : 0);
			y = (y > 0 ? y : 0);
			Rectangle rect = new Rectangle(x, y, w, h);
			param.setSourceRegion(rect);
			BufferedImage bi = reader.read(0, param);
			ImageIO.write(bi, "jpg", new File(dest));
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}

	}

	/***
	 * 	图片旋转指定角度
	 * 
	 * @param bufferedimage 图像
	 * @param degree        角度
	 * @return
	 * @date 2018/7/5 16:30
	 */
	public static BufferedImage rotateImage(final BufferedImage bufferedimage, final int degree) {
		int w = bufferedimage.getWidth();
		int h = bufferedimage.getHeight();
		int type = bufferedimage.getColorModel().getTransparency();
		BufferedImage img;
		Graphics2D graphics2d;
		(graphics2d = (img = new BufferedImage(w, h, type)).createGraphics())
				.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// graphics2d.setBackground(Color.white);
		graphics2d.setPaint(Color.WHITE);
		graphics2d.fillRect(0, 0, w, h);
		graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
		graphics2d.drawImage(bufferedimage, 0, 0, Color.WHITE, null);
		graphics2d.dispose();
		return img;
	}

	public static boolean rotateImage(String srcImageFile, String result, int degree) {
		try {
			BufferedImage src = ImageIO.read(new File(srcImageFile)); // 读入文件
			BufferedImage tag = rotateImage(src, degree);

			ImageIO.write(tag, "JPEG", new File(result));

			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	public static BufferedImage Rotate(Image src, int angel) {
		int src_width = src.getWidth(null);
		int src_height = src.getHeight(null);
		// angel=angel>0?angel:(360-angel);
		// calculate the new image size
		Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);

		BufferedImage res = null;
		res = new BufferedImage(rect_des.width, rect_des.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = res.createGraphics();
		g2.setPaint(Color.WHITE);
		g2.fillRect(0, 0, rect_des.width, rect_des.height);
		// transform
		g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
		g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);

		g2.drawImage(src, null, null);
		return res;
	}

	public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
		// if angel is greater than 90 degree, we need to do some conversion
		if (angel >= 90) {
			if (angel / 90 % 2 == 1) {
				int temp = src.height;
				src.height = src.width;
				src.width = temp;
			}
			angel = angel % 90;
		}

		double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
		double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
		double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
		double angel_dalta_width = Math.atan((double) src.height / src.width);
		double angel_dalta_height = Math.atan((double) src.width / src.height);

		int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
		int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
		int des_width = src.width + len_dalta_width * 2;
		int des_height = src.height + len_dalta_height * 2;
		return new Rectangle(new Dimension(des_width, des_height));
	}

	/**
	 * 	截取图片
	 * 
	 * @param srcImageFile 原图片地址
	 * @param x            截取时的x坐标
	 * @param y            截取时的y坐标
	 * @param desWidth     截取的宽度
	 * @param desHeight    截取的高度
	 */
	public static void imgCut(String srcImageFile, int x, int y, int desWidth, int desHeight) {
		try {
			Image img;
			ImageFilter cropFilter;
			BufferedImage bi = ImageIO.read(new File(srcImageFile + "_src.jpg"));
			int srcWidth = bi.getWidth();
			int srcHeight = bi.getHeight();
			if (srcWidth >= desWidth && srcHeight >= desHeight) {
				Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
				cropFilter = new CropImageFilter(x, y, desWidth, desHeight);
				img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
				BufferedImage tag = new BufferedImage(desWidth, desHeight, BufferedImage.TYPE_INT_RGB);
				Graphics g = tag.getGraphics();
				g.drawImage(img, 0, 0, null);
				g.dispose();
				// 输出文件
				ImageIO.write(tag, "JPEG", new File(srcImageFile + "_cut.jpg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 删除原来的图片
			FileUploadUtil.deleteFile(srcImageFile + "_src.jpg");
		}
	}

	public static boolean Rotate(String srcImageFile, String result, int degree) {
		try {
			degree = degree > 0 ? degree : (360 - degree);
			BufferedImage src = ImageIO.read(new File(srcImageFile));
			BufferedImage tag = Rotate(src, degree);
			ImageIO.write(tag, "JPEG", new File(result));
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	/**
	 *
	 * @param srcImageFile 原图片地址
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param degree
	 */
	// 帶有角度的cut图片
	public static void cutAndRotateImage(String srcImageFile, int x, int y, int w, int h, int degree) {
		try {
			BufferedImage src = ImageIO.read(new File(srcImageFile + "_src.jpg")); // ImageIO.read(new
																					// File(srcImageFile)); // 读入文件
			BufferedImage tag = Rotate(src, degree);
			Iterator<?> iterator = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = (ImageReader) iterator.next();
			// InputStream in=new FileInputStream(tag);
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ImageOutputStream ios = ImageIO.createImageOutputStream(bs);
			ImageIO.write(tag, "jpg", ios);
			InputStream in = new ByteArrayInputStream(bs.toByteArray());
			ImageInputStream iis = ImageIO.createImageInputStream(in);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			x = (x > 0 ? x : 0);
			y = (y > 0 ? y : 0);
			Rectangle rect = new Rectangle(x, y, w, h);
			param.setSourceRegion(rect);
			BufferedImage bi = reader.read(0, param);
			// 输出文件
			ImageIO.write(bi, "jpg", new File(srcImageFile + "_cut.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 删除原来的图片
			FileUploadUtil.deleteFile(srcImageFile + "_src.jpg");
		}
	}

	/***
	 * 	按照比例缩放
	 * 
	 * @param srcImageFile
	 * @param result
	 * @param scale        缩放比例
	 * @param flag         缩放选择:true 放大; false 缩小;
	 * @return
	 * @date 2018/7/5 16:30
	 */
	public static boolean scale(String srcImageFile, String result, int scale, boolean flag) {
		try {
			File file = new File(srcImageFile);
			BufferedImage src = ImageIO.read(file); // 读入文件
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = (ImageReader) readers.next();
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			reader.setInput(iis, true);
			int width = reader.getWidth(0); // 得到源图宽
			int height = reader.getHeight(0); // 得到源图长
			if (flag) {// 放大
				width = width * scale;
				height = height * scale;
			} else {// 缩小
				width = width / scale;
				height = height / scale;
			}
			Image image = src.getScaledInstance(width, height, Image.SCALE_DEFAULT);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();
			g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			ImageIO.write(tag, "jpg", new File(result));// 输出到文件流
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/***
	 * 缩放图像，按照长宽缩放
	 * 
	 * @param srcImageFile
	 * @param result
	 * @param height       变换后的高度
	 * @param width        变换后的长度
	 * @param bb           比例不对称时，是否补白，true 补白;false 不补白
	 * @return
	 * 
	 * @date 2018/7/5 16:30
	 */
	public static boolean scale2(String srcImageFile, String result, int height, int width, boolean bb) {
		try {
			double ratio = 0.0; // 缩放比例
			File file = new File(srcImageFile);
			BufferedImage bi = ImageIO.read(file); // 读入文件
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = (ImageReader) readers.next();
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			reader.setInput(iis, true);
			int width1 = reader.getWidth(0); // 得到源图宽
			int height1 = reader.getHeight(0); // 得到源图长
			Image itemp = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			// 计算比例
			if ((height1 > height) || (width1 > width)) {
				if (height1 > width1) {
					ratio = (new Integer(height)).doubleValue() / height1;
				} else {
					ratio = (new Integer(width)).doubleValue() / width1;
				}
				AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
				itemp = op.filter(bi, null);
			}
			if (bb) {// 补白
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);
				if (width == itemp.getWidth(null))
					g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				else
					g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				g.dispose();
				itemp = image;
			}
			ImageIO.write((BufferedImage) itemp, "JPEG", new File(result));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/***
	 * 	转换图像格式
	 * 
	 * @param srcImageFile  原图像地址
	 * @param formatName    转换类型
	 * @param destImageFile 转换后的地址
	 * @return
	 * @author roychenyi
	 * @date 2018/7/5 16:30
	 */
	public static boolean convert(String srcImageFile, String formatName, String destImageFile) {
		try {
			File f = new File(srcImageFile);
			f.canRead();
			f.canWrite();
			BufferedImage src = ImageIO.read(f);
			ImageIO.write(src, formatName, new File(destImageFile));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 	彩色转为黑白
	 * 
	 * @param srcImageFile  源图像地址
	 * @param destImageFile 目标图像地址
	 */
	public final static void gray(String srcImageFile, String destImageFile) {
		try {
			BufferedImage src = ImageIO.read(new File(srcImageFile));
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			ColorConvertOp op = new ColorConvertOp(cs, null);
			src = op.filter(src, null);
			ImageIO.write(src, "jpg", new File(destImageFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 	给图片添加图片水印
	 * 
	 * @param pressImg      水印图片
	 * @param srcImageFile  源图像地址
	 * @param destImageFile 目标图像地址
	 * @param x             修正值。 默认在中间
	 * @param y             修正值。 默认在中间
	 * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 */
	public static boolean pressImage(String pressImg, String srcImageFile, String destImageFile, int x, int y,
			float alpha) {
		try {
			File file = new File(srcImageFile);
			BufferedImage src = ImageIO.read(file); // 读入文件
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = (ImageReader) readers.next();
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			reader.setInput(iis, true);
			int width = reader.getWidth(0); // 得到源图宽
			int height = reader.getHeight(0); // 得到源图长
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, width, height, null);
			// 水印文件
			Image src_biao = ImageIO.read(new File(pressImg));
			int wideth_biao = src_biao.getWidth(null);
			int height_biao = src_biao.getHeight(null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			g.drawImage(src_biao, (width - wideth_biao - x), (height - height_biao - y), wideth_biao, height_biao,
					null);
			// 水印文件结束
			g.dispose();
			ImageIO.write((BufferedImage) image, "jpg", new File(destImageFile));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 	给图片添加文字水印
	 * 
	 * @param pressText     水印文字
	 * @param srcImageFile  源图像地址
	 * @param destImageFile 目标图像地址
	 * @param fontName      水印的字体名称
	 * @param fontStyle     水印的字体样式
	 * @param color         水印的字体颜色
	 * @param fontSize      水印的字体大小
	 * @param x             修正值
	 * @param y             修正值
	 * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 */
	public static boolean pressText(String pressText, String srcImageFile, String destImageFile, String fontName,
			int fontStyle, Color color, int fontSize, int x, int y, float alpha) {
		try {
			File file = new File(srcImageFile);
			BufferedImage src = ImageIO.read(file); // 读入文件
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = (ImageReader) readers.next();
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			reader.setInput(iis, true);
			int width = reader.getWidth(0); // 得到源图宽
			int height = reader.getHeight(0); // 得到源图长
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, width, height, null);
			g.setColor(color);
			g.setFont(new Font(fontName, fontStyle, fontSize));
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			// 在指定坐标绘制水印文字
			g.drawString(pressText, (width - (getLength(pressText) * fontSize)) / 2 + x, (height - fontSize) / 2 + y);
			g.dispose();
			ImageIO.write(image, "jpg", new File(destImageFile));// 输出到文件流
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 	计算text的长度（一个中文算两个字符）
	 * 
	 * @param text
	 * @return
	 */
	public static int getLength(String text) {
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (new String(text.charAt(i) + "").getBytes().length > 1) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length / 2;
	}

	public static void main(String[] args) {
		String src = "D:/0.jpg";
		/*
		 * imageUtils.cutImage(src, "D://1cut.jpg", -10, 40, 100000, 560);
		 * imageUtils.rotateImage(src, "D:/2rotate.jpg", -30);
		 * imageUtils.rotateImage(src, "D:/2rotate1.png", 30); imageUtils.Rotate(src,
		 * "D://2rotate2.jpg", 330); imageUtils.Rotate(src,"D://2rotate21.jpg",30);
		 * imageUtils.cutAndRotateImage(src, "D://9cutRotate1.jpg", 244, 427, 614, 614,
		 * 0); imageUtils.scale(src, "D://3scale1.jpg", 3, false);
		 * imageUtils.scale2(src, "D://4scale2.jpg", 780,560, false);
		 * imageUtils.convert(src, "png", "D://5convert.png");
		 * imageUtils.gray(src,"D://6gray.jpg");
		 * imageUtils.pressImage("D://3scale1.jpg", src, "D://7pressImage.jpg", 100,
		 * 100, 0.9f); imageUtils.pressText("图片1414", src, "D://8pressText.jpg", "宋体",
		 * Font.BOLD, Color.black, 80, 10, 10,0.6f);
		 */
		ImageUtils util = new ImageUtils();
		util.getImageSizeByImageReader(src);
		util.getImageSizeByBufferedImage(src);
		System.out.println(getImageHeight(src));
		ImageUtils.Rotate("E:\\apache-tomcat-7.0.59\\webapps\\wxs\\upload\\image\\1\\2016\\3\\30\\1459347622160.jpg",
				"E:\\apache-tomcat-7.0.59\\webapps\\wxs\\upload/image/1/2016/3/30/1459347622160_s.jpg", 90);
		ImageUtils.Rotate("E:/apache-tomcat-7.0.59/webapps/wxs/upload/image/1/2016/3/30/1459349306443_s.jpg",
				"E:/apache-tomcat-7.0.59/webapps/wxs/upload/image/1/2016/3/30/1459349306443_s_200.jpg", 45);
	}

	/**
	 * 	文件上传
	 * 
	 * @param file
	 * @param filePath
	 * @param fileName
	 * @throws Exception
	 */
	public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		FileOutputStream out = new FileOutputStream(filePath + fileName);
		out.write(file);
		out.flush();
		out.close();
	}

	/**
	 *
	 * @param file     文件
	 * @param path     文件存放路径
	 * @param fileName 源文件名
	 * @return
	 */
	public static boolean upload(MultipartFile file, String path, String fileName) {

		// 生成新的文件名
		// String realPath = path + "/" + FileNameUtils.getFileName(fileName);

		// 使用原文件名
		String realPath = path + "/" + fileName;

		File dest = new File(realPath);

		// 判断文件父目录是否存在
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdir();
		}

		try {
			// 保存文件
			file.transferTo(dest);
			return true;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
