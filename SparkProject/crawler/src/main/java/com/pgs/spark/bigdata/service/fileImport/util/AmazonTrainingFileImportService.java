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
 * The type Amazon training file import service.
 */
@Component(value = "amazonTraining")
public class AmazonTrainingFileImportService extends FileImportService {

    @Override
    protected void splitFileToLinesAndStoreInDb(Path path) throws IOException {
        final Iterator<String> fileLinesIterator = Files.lines(path).iterator();
        int lineNumber = -1;

        final StringBuilder stringBuilder = new StringBuilder();

        while (fileLinesIterator.hasNext()) {
            String line = fileLinesIterator.next();
            lineNumber++;
            while (!line.matches(".+[0-1]$")) {
                stringBuilder.append(line);
                line = fileLinesIterator.next();
                lineNumber++;
            }
            stringBuilder.append(line);
            if (!stringBuilder.toString().trim().isEmpty()) {
                parseLineAndStoreInDb(stringBuilder.toString().trim(), lineNumber, path.toString());
            }
            stringBuilder.setLength(0);
        }
    }

    @Override
    protected Document parseLineAndStoreInDb(final String line, final int lineNumber, final String fileUrl) {
        final String[] parts = line.split("\\t{1,}");
        final Document document;
        final LocalDate randomDate = getRandomDate(LocalDate.of(2014, 1, 1), LocalDate.now());
        if (Integer.parseInt(parts[parts.length - 1]) == 1) {
            document = documentService.addDocument(fileUrl + ":" + lineNumber, parts[0], randomDate, null, "POSITIVE");
        } else {
            document = documentService.addDocument(fileUrl + ":" + lineNumber, parts[0], randomDate, null, "NEGATIVE");
        }
        return document;
    }
}
