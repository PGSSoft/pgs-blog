package com.pgs.spark.bigdata.service.fileImport;

import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.repository.ResultRepository;
import com.pgs.spark.bigdata.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The type File import service.
 */
public abstract class FileImportService {

    /**
     * The Document service.
     */
    @Autowired
    protected DocumentService documentService;

    /**
     * The Result repository.
     */
    @Autowired
    protected ResultRepository resultRepository;

    /**
     * Import from path and store in db.
     *
     * @param path the path
     * @throws IOException the io exception
     */
    public void importFromPathAndStoreInDb(final URI path) throws IOException {
        final Path filePath = Paths.get(path);
        splitFileToLinesAndStoreInDb(filePath);
    }

    /**
     * Split file to lines and store in db.
     *
     * @param filePath the file path
     * @throws IOException the io exception
     */
    protected abstract void splitFileToLinesAndStoreInDb(final Path filePath) throws IOException;

    /**
     * Parse line and store in db document.
     *
     * @param line       the line
     * @param lineNumber the line number
     * @param fileUrl    the file url
     * @return the document
     */
    protected abstract Document parseLineAndStoreInDb(final String line, final int lineNumber, final String fileUrl);

    /**
     * Gets random date.
     *
     * @param from the from
     * @param to   the to
     * @return the random date
     */
    protected LocalDate getRandomDate(final LocalDate from, final LocalDate to) {
        long minDay = from.toEpochDay();
        long maxDay = to.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

}
