package com.codeleaked.urlshortener.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendApplicationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@MockBean
	private UrlRepository urlRepository;

	@Test
	void unableToFindIdShouldReturnNotFound() {
		// given
		Mockito.when(urlRepository.findById("ABC123")).thenReturn(Optional.empty());

		// when
		ResponseEntity<String> entity = testRestTemplate.getForEntity(
						"http://localhost:" + port + "/decode/ABC123", String.class);

		// then
		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody()).isEqualTo("Not Found");
	}

	@Test
	void shouldReturnUrlIfIdIsFoundInTheDatabase() {
		// given
		Mockito.when(urlRepository.findById("ABC123"))
				.thenReturn(Optional.of(new UrlEntry("ABC123", "http://codeleaked.com")));

		// when
		ResponseEntity<String> entity = testRestTemplate.getForEntity(
				"http://localhost:" + port + "/decode/ABC123", String.class);

		// then
		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody()).isEqualTo("http://codeleaked.com");
	}

	@Test
	void shouldEncodeUrlToAnAlphaNumericStringOfSizeSix() {
		// given
		Mockito.when(urlRepository.findByUrl("http://codeleaked.com")).thenReturn(Optional.empty());

		// when
		ResponseEntity<String> entity = testRestTemplate.postForEntity(
						"http://localhost:" + port + "/encode", "http://codeleaked.com", String.class);

		// then
		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody()).matches("[0-9a-zA-Z]{6}");
	}

	@Test
	void shouldReuseIdIfUrlHasBeenEncodedBefore() {
		// given
		Mockito.when(urlRepository.findByUrl("http://codeleaked.com"))
				.thenReturn(Optional.of(new UrlEntry("ABC123", "http://codeleaked.com")));

		// when
		ResponseEntity<String> entity = testRestTemplate.postForEntity(
				"http://localhost:" + port + "/encode", "http://codeleaked.com", String.class);

		// then
		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody()).isEqualTo("ABC123");
	}

}
