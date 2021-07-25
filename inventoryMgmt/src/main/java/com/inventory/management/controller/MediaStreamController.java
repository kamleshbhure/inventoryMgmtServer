package com.inventory.management.controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.inventory.management.message.ResponseMessage;
import com.inventory.management.model.FileInfo;
import com.inventory.management.service.FilesStorageService;
import com.inventory.management.service.MediaStreamService;
import com.inventory.management.service.VideoThumbTaker;

@RestController
@RequestMapping("/media")
public class MediaStreamController {

	public static final String VIDEO_PATH = "/static/video";
	public static final String AUDIO_PATH = "/static/audio";

	@Autowired
    private MediaStreamService mediaStreamService;

	@Autowired
    private VideoThumbTaker videoThumbTaker;

    @Autowired
    FilesStorageService storageService;

	@GetMapping("/video/{fileType}/{fileName}")
    public ResponseEntity<byte[]> streamVideo(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable("fileType") String fileType,
                                                    @PathVariable("fileName") String fileName) {
        return mediaStreamService.prepareContent(VIDEO_PATH, fileName, fileType, httpRangeList, "video");
    }
    
    @GetMapping("/audio/{fileType}/{fileName}")
    public ResponseEntity<byte[]> streamAudio(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable("fileType") String fileType,
                                                    @PathVariable("fileName") String fileName) {
        return mediaStreamService.prepareContent(AUDIO_PATH, fileName, fileType, httpRangeList, "audio");
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
      String message = "";
      try {
        storageService.save(file);
        String filename = file.getOriginalFilename();
        String url = MvcUriComponentsBuilder
            .fromMethodName(
            		MediaStreamController.class, 
            		"streamVideo", 
            		null, 
            		getExtensionByStringHandling(filename), 
            		filename.substring(0, filename.lastIndexOf("."))
            ).build().toString();
        FileInfo fileinfo = new FileInfo(filename, url);

        videoThumbTaker.captureImage(filename);

        return ResponseEntity.status(HttpStatus.OK).body(fileinfo);
      } catch (Exception e) {
        message = "Could not upload the file: " + file.getOriginalFilename() + "! Because error:"+e.getMessage();
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
      }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
      List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
        String filename = path.getFileName().toString();
        String url = MvcUriComponentsBuilder
            .fromMethodName(
            		MediaStreamController.class, 
            		"streamVideo", 
            		null, 
            		getExtensionByStringHandling(filename), 
            		filename.substring(0, filename.lastIndexOf("."))
            ).build().toString();
        return new FileInfo(filename, url);
      }).collect(Collectors.toList());

      return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
      Resource file = storageService.load(filename);
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    private String getExtensionByStringHandling(String fileName) {
    	int index = fileName.lastIndexOf('.');
    	String extension = ""; 
        if (index > 0) {
          extension = fileName.substring(index + 1);
        }
        return extension;
    }

}