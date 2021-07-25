package com.inventory.management.service;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.imageio.ImageIO;


@Service
public class MediaStreamService {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String ACCEPT_RANGES = "Accept-Ranges";
    public static final String BYTES = "bytes";
    public static final int BYTE_RANGE = 128;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Prepare the content.
     *
     * @param fileName String.
     * @param fileType String.
     * @param range    String.
     * @return ResponseEntity.
     */
    public ResponseEntity<byte[]> prepareContent(String location, String fileName, String fileType, String range, String contentTypePrefix) {
        long rangeStart = 0;
        long rangeEnd;
        byte[] data;
        Long fileSize;
        String fullFileName = fileName + "." + fileType;
        Path filePath = Paths.get(getFilePath(location), fullFileName);
        if (Files.exists(filePath)) {
	        try {
	            fileSize = getFileSize(location, fullFileName);
	            if (range == null) {
	            	System.out.println("Range is null");
	                return ResponseEntity.status(HttpStatus.OK)
	                        .header(CONTENT_TYPE, contentTypePrefix + "/" + fileType)
	                        .header(CONTENT_LENGTH, String.valueOf(fileSize))
	                        .body(readByteRange(location, fullFileName, rangeStart, fileSize - 1)); // Read the object and convert it as bytes
	            }
	            String[] ranges = range.split("-");
	            rangeStart = Long.parseLong(ranges[0].substring(6));
	            if (ranges.length > 1) {
	                rangeEnd = Long.parseLong(ranges[1]);
	            } else {
	                rangeEnd = fileSize - 1;
	            }
	            if (fileSize < rangeEnd) {
	                rangeEnd = fileSize - 1;
	            }
	            data = readByteRange(location, fullFileName, rangeStart, rangeEnd);
	        } catch (IOException e) {
	            logger.error("Exception while reading the file {}", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	        System.out.println("Range is "+rangeEnd+"::"+rangeStart);
	        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
	        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
	                .header(CONTENT_TYPE, contentTypePrefix + "/" + fileType)
	                .header(ACCEPT_RANGES, BYTES)
	                .header(CONTENT_LENGTH, contentLength)
	                .header(CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
	                .body(data);
        } else {
        	logger.error("File Not found"+filePath.toUri());
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    /**
     * ready file byte by byte.
     *
     * @param filename String.
     * @param start    long.
     * @param end      long.
     * @return byte array.
     * @throws IOException exception.
     */
    public byte[] readByteRange(String location, String filename, long start, long end) throws IOException {
        Path path = Paths.get(getFilePath(location), filename);
        try (InputStream inputStream = (Files.newInputStream(path));
             ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()
            ) {
            byte[] data = new byte[BYTE_RANGE];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                bufferedOutputStream.write(data, 0, nRead);
            }
            bufferedOutputStream.flush();
            byte[] result = new byte[(int) (end - start) + 1];
            System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
            return result;
        }
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

    /**
     * Content length.
     *
     * @param fileName String.
     * @return Long.
     */
    public Long getFileSize(String localtion, String fileName) {
        return Optional.ofNullable(fileName)
                .map(file -> Paths.get(getFilePath(localtion), file))
                .map(this::sizeFromFile)
                .orElse(0L);
    }

    /**
     * Getting the size from the path.
     *
     * @param path Path.
     * @return Long.
     */
    private Long sizeFromFile(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ioException) {
            logger.error("Error while getting the file size", ioException);
        }
        return 0L;
    }
}