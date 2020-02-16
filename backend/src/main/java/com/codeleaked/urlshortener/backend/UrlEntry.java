package com.codeleaked.urlshortener.backend;

import org.springframework.data.annotation.Id;

public class UrlEntry {

    @Id
    public String id;

    public String url;

    public UrlEntry(String id, String url) {
        this.id = id;
        this.url = url;
    }
}
