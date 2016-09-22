package com.pgs.spark.bigdata.processor.utils;

import org.apache.spark.sql.Row;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * The type Row utils.
 */
public class RowUtils {

    private RowUtils() {
    }

    /**
     * Uuid from row uuid.
     *
     * @param row      the row
     * @param position the position
     * @return the uuid
     */
    public static UUID uuidFromRow(Row row, int position) {
        return UUID.fromString(row.getString(position));
    }

    /**
     * Local date from row local date.
     *
     * @param row      the row
     * @param position the position
     * @return the local date
     */
    public static LocalDate localDateFromRow(Row row, int position) {
        Date date = row.getDate(position);
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
