package com.codeleaked.urlshortener.gateway;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Configuration
public class GatewayConfiguration {

    private final static String SERVICE_ID = "shortener";

    @Bean
    @Primary
    ServiceInstanceListSupplier serviceInstanceListSupplier() {
        return new ServiceInstanceListSupplier() {
            @Override
            public Flux<List<ServiceInstance>> get() {
                return Flux.just(Arrays
                        .asList(new DefaultServiceInstance(SERVICE_ID + "1", SERVICE_ID, "localhost", 9001, false),
                                new DefaultServiceInstance(SERVICE_ID + "2", SERVICE_ID, "localhost", 9002, false))
                );
            }

            @Override
            public String getServiceId() {
                return SERVICE_ID;
            }
        };
    }

}
