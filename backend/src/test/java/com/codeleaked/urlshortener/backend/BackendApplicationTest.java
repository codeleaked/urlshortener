package com.codeleaked.urlshortener.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendApplicationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	void unableToFindIdShouldReturnNotFound() {
		ResponseEntity<String> entity = testRestTemplate.getForEntity(
						"http://localhost:" + port + "/decode/ABC123", String.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody()).isEqualTo("Not Found");
	}

	@Test
	void shouldEncodeUrlToAnAlphaNumericStringOfSizeSix() {
		ResponseEntity<String> entity = testRestTemplate.postForEntity(
						"http://localhost:" + port + "/encode", "http://codeleaked.com", String.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody()).matches("[0-9a-zA-Z]{6}");
	}

	@Test
	void shouldDecodeTheEncodedUrl() {
		String id = testRestTemplate.postForEntity(
				"http://localhost:" + port + "/encode", "http://codeleaked.com", String.class).getBody();

		ResponseEntity<String> entity = testRestTemplate.getForEntity(
				"http://localhost:" + port + "/decode/" + id, String.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody()).isEqualTo("http://codeleaked.com");
	}

}
