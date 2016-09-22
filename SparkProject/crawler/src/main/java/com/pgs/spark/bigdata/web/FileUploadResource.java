package com.pgs.spark.bigdata.web;

import com.pgs.spark.bigdata.service.FileDataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The type File upload resource.
 */
@RestController
@RequestMapping(value = "/api/crawler")
public class FileUploadResource {

    private static final String ROOT = "upload-dir";

    @Autowired
    private FileDataImportService fileDataImportService;

    /**
     * Handle training file upload.
     *
     * @param file the file
     * @return the response entity
     */
    @RequestMapping(method = RequestMethod.POST, value = "/fileUpload/training", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleTrainingFileUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                if(!Paths.get(ROOT).toFile().exists()){
                    Files.createDirectory(Paths.get(ROOT));
                }
                if (Paths.get(ROOT, file.getOriginalFilename()).toFile().exists()) {
                    Files.delete(Paths.get(ROOT, file.getOriginalFilename()));
                }
                Files.copy(file.getInputStream(), Paths.get(ROOT, file.getOriginalFilename()));
                fileDataImportService.importData(Paths.get(ROOT, file.getOriginalFilename()).toUri(), true);

                return ResponseEntity.ok().build();

            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Handle validation file upload.
     *
     * @param file the file
     * @return the response entity
     */
    @RequestMapping(method = RequestMethod.POST, value = "/fileUpload/validation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleValidationFileUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                if(!Paths.get(ROOT).toFile().exists()){
                    Files.createDirectory(Paths.get(ROOT));
                }
                if (Paths.get(ROOT, file.getOriginalFilename()).toFile().exists()) {
                    Files.delete(Paths.get(ROOT, file.getOriginalFilename()));
                }
                Files.copy(file.getInputStream(), Paths.get(ROOT, file.getOriginalFilename()));
                fileDataImportService.importData(Paths.get(ROOT, file.getOriginalFilename()).toUri(), false);

                return ResponseEntity.ok().build();

            } catch (IOException | RuntimeException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
