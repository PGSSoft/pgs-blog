package com.pgs.spark.bigdata.web;

import com.pgs.spark.bigdata.service.SocialMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * The type Social media resource.
 */
@RestController
@RequestMapping("/api")
public class SocialMediaResource {

    @Autowired
    private SocialMediaService socialMediaService;

    /**
     * List social medias response entity.
     *
     * @return the response entity
     */
    @RequestMapping(value = "/socialMedia",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> listSocialMedias() {
        List<String> socialMedias = socialMediaService.listSocialMedias();
        return ResponseEntity.ok(socialMedias);
    }

    /**
     * Perform job response entity.
     *
     * @param jobName the job name
     * @param id      the id
     * @return the response entity
     */
    @RequestMapping(value = "/socialMedia/perform",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> performJob(@RequestParam("jobName") final String jobName, final String id) {
        socialMediaService.performCrawling(jobName, UUID.fromString(id));
        return ResponseEntity.ok().build();
    }
}
