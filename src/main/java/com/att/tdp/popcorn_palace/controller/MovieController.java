package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.MovieRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movies")
@Slf4j
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @Operation(summary = "Add a new movie")
    public ResponseEntity<Movie> addMovie(@Validated @RequestBody MovieRequest movieRequest) {
        log.info("Received request to add movie: {}", movieRequest.getTitle());
        Movie savedMovie = movieService.addMovie(movieRequest);
        return ResponseEntity.ok(savedMovie);
    }

    @GetMapping
    @Operation(summary = "Get all movies")
    public ResponseEntity<List<Movie>> getAllMovies() {
        log.info("Received request to fetch all movies");
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing movie")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @Validated @RequestBody MovieRequest movieRequest) {
        log.info("Received request to update movie with ID: {}", id);
        Movie movieDetails = Movie.builder()
                .title(movieRequest.getTitle())
                .genre(movieRequest.getGenre())
                .duration(movieRequest.getDuration())
                .rating(movieRequest.getRating())
                .releaseYear(movieRequest.getReleaseYear())
                .build();
        return movieService.updateMovie(id, movieDetails)
                .map(movie -> {
                    log.info("Movie with ID {} updated successfully", id);
                    return ResponseEntity.ok(movie);
                })
                .orElseGet(() -> {
                    log.warn("Movie with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a movie")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
        log.info("Received request to delete movie with ID: {}", id);
        movieService.deleteMovie(id);
        return ResponseEntity.ok("Movie deleted successfully");

    }
}
