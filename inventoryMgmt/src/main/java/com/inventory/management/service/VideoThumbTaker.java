package com.inventory.management.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VideoThumbTaker {
	private final static String VIDEO_THUMBNAIL_FOLDER_NAME = "/static/images";
	private final static String VIDEO_FOLDER_NAME = "/static/video";
    private static final Logger logger = LoggerFactory.getLogger(VideoThumbTaker.class);

	public String captureImage(String fileName) {

		Path filePath = Paths.get(getFilePath(VIDEO_FOLDER_NAME), fileName);
		File inputFile = filePath.toFile();
		String imageFileName = fileName.substring(0, fileName.lastIndexOf("."))+".jpg";
		try {
			int frameNumber = 90;
			Picture picture = FrameGrab.getFrameAtSec(inputFile, frameNumber);
			BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
			
			Path destPath = Paths.get(getFilePath(VIDEO_THUMBNAIL_FOLDER_NAME), imageFileName);
			File destImagePath = destPath.toFile();
			ImageIO.write(bufferedImage, "jpg", destImagePath);
		} catch (Exception e) {
			logger.error("Error while reading file", e);
		}
		return VIDEO_THUMBNAIL_FOLDER_NAME + "/" + imageFileName;
	}
	

    /**
     * Get the filePath.
     *
     * @return String.
     */
    private String getFilePath(String location) {
        URL url = this.getClass().getResource(location);
        return new File(url.getFile()).getAbsolutePath();
    }
}