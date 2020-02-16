package com.codeleaked.urlshortener.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@SpringBootApplication
@RestController
public class GatewayApplication {

    private static Logger log = LoggerFactory.getLogger(GatewayApplication.class);

    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    public GatewayApplication(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.lbFunction = lbFunction;
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @PostMapping("/shorten")
    public Mono<String> shorten(@RequestBody String url) {
        Objects.requireNonNull(url);

        log.info("Shorten {}", url);

        return WebClient.builder()
                .filter(lbFunction)
                .build()
                .post().uri("http://shortener/encode")
                .body(BodyInserters.fromValue(url))
                .retrieve().bodyToMono(String.class);
    }

    @GetMapping("/retrieve/{id}")
    public Mono<String> retrieve(@PathVariable String id) {
        Objects.requireNonNull(id);

        log.info("Retrieve {}", id);

        return WebClient.builder()
                .filter(lbFunction)
                .build()
                .get().uri("http://shortener/decode/" + id)
                .retrieve().bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.error(e);
                });
    }

}

