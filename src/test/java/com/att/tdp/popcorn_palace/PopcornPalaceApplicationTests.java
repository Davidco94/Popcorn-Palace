package com.att.tdp.popcorn_palace;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PopcornPalaceApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	private String getUrl(String path) {
		return "http://localhost:" + port + path;
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testFullCRUDFlow() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// --- Add Movie ---
		Map<String, Object> movie = new HashMap<>();
		movie.put("title", "Flow Test Movie");
		movie.put("genre", "Adventure");
		movie.put("duration", 120);
		movie.put("rating", 7.5);
		movie.put("releaseYear", 2023);

		HttpEntity<String> movieRequest = new HttpEntity<>(objectMapper.writeValueAsString(movie), headers);
		ResponseEntity<String> movieResponse = restTemplate.postForEntity(getUrl("/api/movies"), movieRequest, String.class);
		assertThat(movieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// --- Get All Movies ---
		ResponseEntity<String> getMovies = restTemplate.getForEntity(getUrl("/api/movies"), String.class);
		assertThat(getMovies.getStatusCode()).isEqualTo(HttpStatus.OK);

		// --- Update Movie ---
		movie.put("rating", 8.5);
		HttpEntity<String> updateMovieRequest = new HttpEntity<>(objectMapper.writeValueAsString(movie), headers);
		ResponseEntity<String> updateMovieResponse = restTemplate.exchange(getUrl("/api/movies/1"), HttpMethod.PUT, updateMovieRequest, String.class);
		assertThat(updateMovieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// --- Add Showtime ---
		Map<String, Object> showtime = new HashMap<>();
		showtime.put("movieId", 1);
		showtime.put("price", 25.0);
		showtime.put("theater", "Main Theater");
		showtime.put("startTime", LocalDateTime.now().plusDays(1).toString());
		showtime.put("endTime", LocalDateTime.now().plusDays(1).plusHours(2).toString());

		HttpEntity<String> showtimeRequest = new HttpEntity<>(objectMapper.writeValueAsString(showtime), headers);
		ResponseEntity<String> showtimeResponse = restTemplate.postForEntity(getUrl("/api/showtimes"), showtimeRequest, String.class);
		assertThat(showtimeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// --- Get Showtime ---
		ResponseEntity<String> getShowtime = restTemplate.getForEntity(getUrl("/api/showtimes/1"), String.class);
		assertThat(getShowtime.getStatusCode()).isEqualTo(HttpStatus.OK);

		// --- Update Showtime ---
		showtime.put("price", 30.0);
		HttpEntity<String> updateShowtimeRequest = new HttpEntity<>(objectMapper.writeValueAsString(showtime), headers);
		ResponseEntity<String> updateShowtimeResponse = restTemplate.exchange(getUrl("/api/showtimes/1"), HttpMethod.PUT, updateShowtimeRequest, String.class);
		assertThat(updateShowtimeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// --- Book Ticket ---
		Map<String, Object> ticket = new HashMap<>();
		ticket.put("showtimeId", 1);
		ticket.put("seatNumber", 15);
		ticket.put("userId", UUID.randomUUID().toString());

		HttpEntity<String> ticketRequest = new HttpEntity<>(objectMapper.writeValueAsString(ticket), headers);
		ResponseEntity<String> ticketResponse = restTemplate.postForEntity(getUrl("/api/tickets"), ticketRequest, String.class);
		assertThat(ticketResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// --- Delete Showtime ---
		ResponseEntity<Void> deleteShowtime = restTemplate.exchange(getUrl("/api/showtimes/1"), HttpMethod.DELETE, null, Void.class);
		assertThat(deleteShowtime.getStatusCode()).isEqualTo(HttpStatus.OK);

		// --- Delete Movie ---
		ResponseEntity<Void> deleteMovie = restTemplate.exchange(getUrl("/api/movies/1"), HttpMethod.DELETE, null, Void.class);
		assertThat(deleteMovie.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}