package com.pgs.spark.bigdata.parser;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Objects.nonNull;

class TestUtil {

    public static String readFileAsString(String path) throws IOException {
        BufferedReader bankierExample = Files.newBufferedReader(Paths.get(path));
        StringBuilder builder = new StringBuilder();
        String htmlLine;
        while (nonNull(htmlLine = bankierExample.readLine()))
            builder.append(htmlLine);

        bankierExample.close();
        return builder.toString();
    }

    public static String getHtmlFromUrl(String URL) throws IOException {
        return Jsoup.connect(URL).get().outerHtml();
    }
}
