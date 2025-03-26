package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.MovieRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    MovieRepository movieRepository;

    @InjectMocks
    MovieService movieService;

    public MovieServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddMovie() {
        MovieRequest request = MovieRequest.builder()
                .title("Test Movie")
                .genre("Drama")
                .duration(100)
                .rating("PG")
                .releaseYear(2022)
                .build();

        Movie movie = Movie.builder()
                .title(request.getTitle())
                .genre(request.getGenre())
                .duration(request.getDuration())
                .rating(request.getRating())
                .releaseYear(request.getReleaseYear())
                .build();

        when(movieRepository.existsByTitle("Test Movie")).thenReturn(false);
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie result = movieService.addMovie(request);
        assertEquals("Test Movie", result.getTitle());
    }

    @Test
    void testUpdateMovie() {
        Movie movie = Movie.builder().title("Old Title").build();
        Movie updated = Movie.builder().title("New Title").build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(movie);

        Optional<Movie> result = movieService.updateMovie(1L, updated);
        assertTrue(result.isPresent());
        assertEquals("New Title", result.get().getTitle());
    }
}