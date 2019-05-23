package com.optimiza.ehope.web.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;

import net.coobird.thumbnailator.Thumbnails;

public class ImageUtil {

	private static final List<String> ALLOWED_IMAGE_TYPES = new ArrayList<String>(Arrays.asList("image/png", "image/jpeg"));

	public static final int IMG_WIDTH = 80;//default
	public static final int IMG_HEIGHT = 80;//default

	public static byte[] reduceSize(MultipartFile image, int width, int height) throws IOException {
		String contentType = image.getContentType();
		if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
			throw new BusinessException("Unsupported image type!", "unsupportedImageType", ErrorSeverity.ERROR);
		}
		BufferedImage thumbnail = Thumbnails.of(image.getInputStream()).size(width, height).asBufferedImage();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(thumbnail, contentType.substring(contentType.lastIndexOf("/") + 1), baos);
		baos.flush();
		byte[] imageInBytes = baos.toByteArray();
		baos.close();
		return imageInBytes;
	}

}
