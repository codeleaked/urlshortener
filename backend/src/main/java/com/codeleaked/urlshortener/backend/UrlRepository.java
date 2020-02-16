package com.codeleaked.urlshortener.backend;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UrlRepository extends MongoRepository<UrlEntry, String> {

    Optional<UrlEntry> findById(String id);
    Optional<UrlEntry> findByUrl(String url);

}