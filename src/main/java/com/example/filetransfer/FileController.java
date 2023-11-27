package com.example.filetransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/filetransfer")
public class FileController {

    @Autowired
    private Environment env;

    Logger logger = LoggerFactory.getLogger(FileController.class);

    @PostMapping("/{uploads:.+}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String uploads) {
        try {
            // Generate a unique filename
            String fileName = file.getOriginalFilename();
            String uploadsDir = env.getProperty(uploads);
            if (uploadsDir == null) {
                String errStr = "Invalid upload reference: " + uploads;
                logger.error(errStr);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errStr);    
            }
            Path filePath = Paths.get(uploadsDir).resolve(fileName);
            file.transferTo(filePath.toFile());
            logger.info("Successfully uploaded file " + fileName);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @GetMapping("/{downloads:.+}")
    public ResponseEntity<String> listFiles(@PathVariable String downloads) {

        String downloadDir = env.getProperty(downloads);
        if (downloadDir == null) {
            String errStr = "Invalid download reference: " + downloads;
            logger.error(errStr);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errStr);    
        }


        File dir = new File(downloadDir);
        String fileList = "";
        for (String file : dir.list()) {
            fileList += file + '\n';
        }
            return ResponseEntity.status(HttpStatus.OK).body(fileList);
        }

    @GetMapping("{downloads:.+}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String downloads, @PathVariable String fileName) {
        try {

            String downloadDir = env.getProperty(downloads);
            if (downloadDir == null) {
              logger.error("Invalid download reference: " + downloads);
                
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);   
            }

            Path filePath = Paths.get(downloadDir).resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                logger.info("Processing request to download file " + fileName);
                return ResponseEntity.ok()
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
