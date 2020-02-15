package com.codeleaked.urlshortener.backend;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@SpringBootApplication
public class BackendApplication {

    private static Logger log = LoggerFactory.getLogger(BackendApplication.class);

    private static ConcurrentMap<String, String> idToUrl = new ConcurrentHashMap<>();

    private static ConcurrentMap<String, String> urlToId = new ConcurrentHashMap<>();

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
        if (urlToId.containsKey(url)) {
            id = urlToId.get(url);
        } else {
            id = generateUniqueId();
            saveUrl(id, url);
        }

        return id;
    }

    @GetMapping("/decode/{id}")
    public String decode(@PathVariable String id) {
        log.info("Decode {}", id);
        return idToUrl.getOrDefault(id, "Not Found");
    }

    private void saveUrl(String id, String url) {
        log.info("Save id={}, url={}", id, url);
        idToUrl.put(id, url);
        urlToId.put(url, id);
    }

    private boolean isExistent(String id) {
        return (id == null || idToUrl.containsKey(id));
    }

    private String generateUniqueId() {
        String id = null;
        while (isExistent(id)) {
            id = idGenerator.generate(6);
        }
        return id;
    }

}