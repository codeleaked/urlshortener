package com.codeleaked.urlshortener.backend;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@SpringBootApplication
public class BackendApplication {

    private static final String NOT_FOUND = "Not Found";

    @Autowired
    private UrlRepository repository;

    private static Logger log = LoggerFactory.getLogger(BackendApplication.class);

    private static RandomStringGenerator idGenerator = new RandomStringGenerator.Builder()
            .withinRange('0', 'z')
            .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
            .build();

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @PostMapping("/encode")
    public String encode(@RequestBody String url) {
        log.info("Encode {}", url);

        String id;
        Optional<UrlEntry> urlEntry = repository.findByUrl(url);
        if (urlEntry.isPresent()) {
            id = urlEntry.get().id;
        } else {
            id = generateUniqueId();
            saveUrl(id, url);
        }

        return id;
    }

    @GetMapping("/decode/{id}")
    public String decode(@PathVariable String id) {
        log.info("Decode {}", id);
        Optional<UrlEntry> urlEntry = repository.findById(id);
        if (urlEntry.isPresent()) {
            return urlEntry.get().url;
        } else {
            return NOT_FOUND;
        }
    }

    private void saveUrl(String id, String url) {
        log.info("Save id={}, url={}", id, url);
        repository.save(new UrlEntry(id, url));
    }

    private boolean isExistent(String id) {
        return (id == null || repository.findById(id).isPresent());
    }

    private String generateUniqueId() {
        String id = null;
        while (isExistent(id)) {
            id = idGenerator.generate(6);
        }
        return id;
    }

}