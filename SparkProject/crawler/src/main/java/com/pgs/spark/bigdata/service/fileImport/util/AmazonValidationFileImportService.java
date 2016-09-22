package com.pgs.spark.bigdata.service.fileImport.util;

import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.service.fileImport.FileImportService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Iterator;

/**
 * The type Amazon validation file import service.
 */
@Component(value = "amazonValidation")
public class AmazonValidationFileImportService extends FileImportService {

    @Override
    protected void splitFileToLinesAndStoreInDb(final Path path) throws IOException {
        final Iterator<String> fileLinesIterator = Files.lines(path).iterator();
        int lineNumber = 0;

        while (fileLinesIterator.hasNext()) {
            final String line = fileLinesIterator.next();
            if (!line.trim().isEmpty()) {
                parseLineAndStoreInDb(line, lineNumber++, path.toString());
            }
        }
    }

    @Override
    protected Document parseLineAndStoreInDb(final String line, final int lineNumber, final String fileUrl) {
        final LocalDate randomDate = getRandomDate(LocalDate.of(2014, 1, 1), LocalDate.now());
        return documentService.addDocument(fileUrl + ":" + lineNumber, line, randomDate, null);
    }
}