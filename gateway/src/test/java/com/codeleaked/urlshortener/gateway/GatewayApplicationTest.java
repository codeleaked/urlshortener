package com.codeleaked.urlshortener.gateway;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GatewayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayApplicationTest {

	private ConfigurableApplicationContext application1;
	private ConfigurableApplicationContext application2;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@BeforeEach
	void setUp() {
		application1 = startApp(9001);
		application2 = startApp(9002);
	}

	@AfterEach
	void tearDown() {
		application1.close();
		application2.close();
	}

	@Test
	void shouldRoundRobinOverInstancesWhenCallingServicesViaLoadBalancer() {
		ResponseEntity<String> response1 = testRestTemplate
				.getForEntity("http://localhost:" + port + "/retrieve/ABC123", String.class);
		ResponseEntity<String> response2 = testRestTemplate
				.getForEntity("http://localhost:" + port + "/retrieve/ABC123", String.class);

		then(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(response1.getBody()).isEqualTo("Response from instance 1 to decode ABC123");
		then(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(response2.getBody()).isEqualTo("Response from instance 2 to decode ABC123");
	}

	private ConfigurableApplicationContext startApp(int port) {
		return SpringApplication.run(TestApplication.class, "--server.port=" + port, "--spring.jmx.enabled=false");
	}

	@Configuration
	@EnableAutoConfiguration
	@RestController
	static class TestApplication {

		private static AtomicInteger atomicInteger = new AtomicInteger();

		@GetMapping("/decode/{id}")
		public String decode(@PathVariable String id) {
			return "Response from instance " + atomicInteger.incrementAndGet() + " to decode " + id;
		}

	}
}
