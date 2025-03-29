package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.MovieRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class MovieServiceTest {

    @Mock
    MovieRepository movieRepository;

    @Mock
    ShowtimeRepository showtimeRepository;

    @InjectMocks
    MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateMovie() {
        Movie movie = Movie.builder()
                .id(1L)
                .title("Old Title")
                .genre("Drama")
                .duration(90)
                .rating(5.4)
                .releaseYear(2022)
                .build();

        Movie updated = Movie.builder()
                .title("New Title")
                .genre("Drama")
                .duration(90)
                .rating(5.4)
                .releaseYear(2022)
                .build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(movie);
        when(showtimeRepository.findByMovieId(1L)).thenReturn(Collections.emptyList());

        Movie result = movieService.updateMovie(1L, updated);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
    }

    @Test
    void testAddMovie() {
        MovieRequest movieRequest = MovieRequest.builder()
                .title("Test Movie")
                .genre("Comedy")
                .duration(100)
                .rating(5.4)
                .releaseYear(2021)
                .build();

        Movie movie = Movie.builder()
                .title("Test Movie")
                .genre("Comedy")
                .duration(100)
                .rating(5.4)
                .releaseYear(2021)
                .build();

        when(movieRepository.existsByTitle("Test Movie")).thenReturn(false);
        when(movieRepository.save(any())).thenReturn(movie);

        Movie savedMovie = movieService.addMovie(movieRequest);

        assertNotNull(savedMovie);
        assertEquals("Test Movie", savedMovie.getTitle());
    }

    @Test
    void testAddMovie_DuplicateTitle_ShouldThrowException() {
        MovieRequest movieRequest = MovieRequest.builder()
                .title("Duplicate")
                .genre("Action")
                .duration(120)
                .rating(8.0)
                .releaseYear(2023)
                .build();

        when(movieRepository.existsByTitle("Duplicate")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(movieRequest);
        });

        assertEquals("Movie with this title already exists", exception.getMessage());
    }

    @Test
    void testUpdateMovie_InvalidDurationWithShowtimes_ShouldThrowException() {
        Movie movie = Movie.builder()
                .id(1L)
                .title("Test")
                .genre("Drama")
                .duration(120)
                .rating(5.4)
                .releaseYear(2022)
                .build();

        Movie updated = Movie.builder()
                .title("Test")
                .genre("Drama")
                .duration(150)
                .rating(5.4)
                .releaseYear(2022)
                .build();

        Showtime showtime = Showtime.builder()
                .id(1L)
                .movie(movie)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(100))
                .build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.findByMovieId(1L)).thenReturn(List.of(showtime));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.updateMovie(1L, updated);
        });

        assertTrue(exception.getMessage().contains("Cannot update this movie duration"));
    }

    @Test
    void testGetAllMovies_ShouldReturnList() {
        List<Movie> movies = List.of(
                Movie.builder().title("M1").build(),
                Movie.builder().title("M2").build()
        );
        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = movieService.getAllMovies();

        assertEquals(2, result.size());
        assertEquals("M1", result.get(0).getTitle());
    }

    @Test
    void testDeleteMovie_Success() {
        when(movieRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> movieService.deleteMovie(1L));
    }

    @Test
    void testDeleteMovie_NotFound_ShouldThrowException() {
        when(movieRepository.existsById(1L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            movieService.deleteMovie(1L);
        });

        assertEquals("Movie with ID 1 does not exist", exception.getMessage());
    }
}
