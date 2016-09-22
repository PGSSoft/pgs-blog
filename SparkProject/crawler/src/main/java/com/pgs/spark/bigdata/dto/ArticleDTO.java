package com.pgs.spark.bigdata.dto;

import com.pgs.spark.bigdata.domain.Tag;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * The type Article to.
 */
@Data
public class ArticleDTO {

    private String content;
    private String title;
    private Set<Tag> tags;
    private LocalDate date;

}
