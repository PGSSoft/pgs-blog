package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.service.fileImport.FileImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The type File data import service.
 */
@Service
public class FileDataImportService {

    @Autowired
    @Qualifier("amazonTraining")
    private FileImportService fileTrainingImportService;

    @Autowired
    @Qualifier("amazonValidation")
    private FileImportService fileValidationImportService;

    /**
     * Import training/validation data from file.
     *
     * @param fileURI        the file uri
     * @param isTrainingData the is training data
     * @throws IOException the io exception
     */
    public void importData(final URI fileURI, final boolean isTrainingData) throws IOException {

        if (fileURI == null) {
            throw new NullPointerException("Training file not found");
        }
        if (isTrainingData) {
            fileTrainingImportService.importFromPathAndStoreInDb(fileURI);
        } else {
            fileValidationImportService.importFromPathAndStoreInDb(fileURI);
        }
    }

}
