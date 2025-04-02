package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.MovieRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@Tag(name = "Movies")
@Slf4j
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @Operation(summary = "Add a new movie")
    public ResponseEntity<Movie> addMovie(@Valid @RequestBody MovieRequest request) {
        log.info("Received request to add movie: {}", request.getTitle());
        Movie saved = movieService.addMovie(request);
        log.info("Movie '{}' added successfully with ID: {}", saved.getTitle(), saved.getId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all movies")
    public ResponseEntity<List<Movie>> getAllMovies() {
        log.info("Received request to fetch all movies");
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PostMapping("/update/{movieTitle}")
    @Operation(summary = "Update an existing movie by title")
    public ResponseEntity<Movie> updateMovieByTitle(@PathVariable String movieTitle, @Validated @RequestBody MovieRequest movieRequest) {
        log.info("Received request to update movie with title: {}", movieTitle);
        Long id = movieService.getIdByTitle(movieTitle);

        Movie movieDetails = Movie.builder()
                .title(movieRequest.getTitle())
                .genre(movieRequest.getGenre())
                .duration(movieRequest.getDuration())
                .rating(movieRequest.getRating())
                .releaseYear(movieRequest.getReleaseYear())
                .build();

        Movie updatedMovie = movieService.updateMovie(id, movieDetails);
        log.info("Movie with ID {} updated successfully", id);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{movieTitle}")
    @Operation(summary = "Delete a movie by title")
    public ResponseEntity<String> deleteMovieByTitle(@PathVariable String movieTitle) {
        log.info("Received request to delete movie with title: {}", movieTitle);
        Long id = movieService.getIdByTitle(movieTitle);
        movieService.deleteMovie(id);
        log.info("Movie with title '{}' deleted successfully", movieTitle);
        return ResponseEntity.ok("Movie deleted successfully");
    }
}
