package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.MovieRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.Duration;
import java.util.NoSuchElementException;


@Service
@Slf4j
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;

    public Movie addMovie(MovieRequest movieRequest) {
        log.info("Adding new movie: {}", movieRequest.getTitle());

        if (movieRepository.existsByTitle(movieRequest.getTitle())) {
            throw new IllegalArgumentException("Movie with this title already exists");
        }
        if (movieRequest.getReleaseYear() <= 0) {
            throw new IllegalArgumentException("Release Year cannot be negative number");
        }

        Movie movie = Movie.builder()
                .title(movieRequest.getTitle())
                .genre(movieRequest.getGenre())
                .duration(movieRequest.getDuration())
                .rating(movieRequest.getRating())
                .releaseYear(movieRequest.getReleaseYear())
                .build();

        Movie savedMovie = movieRepository.save(movie);
        log.info("Movie added with ID: {}", savedMovie.getId());
        return savedMovie;
    }

    public List<Movie> getAllMovies() {
        log.info("Fetching all movies");
        return movieRepository.findAll();
    }

    public Movie updateMovie(Long id, Movie movieDetails) {
        log.info("Updating movie with ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Movie with ID " + id + " not found"));
        if (movieDetails.getReleaseYear() <= 0) {
            throw new IllegalArgumentException("Release Year cannot be negative number");
        }
        if (!movie.getTitle().equals(movieDetails.getTitle()) && movieRepository.existsByTitle(movieDetails.getTitle())) {
            throw new IllegalArgumentException("Another movie with this title already exists");
        }
        validateShowtimeDurations(movie.getId(), movieDetails.getDuration());
        movie.setTitle(movieDetails.getTitle());
        movie.setGenre(movieDetails.getGenre());
        movie.setDuration(movieDetails.getDuration());
        movie.setRating(movieDetails.getRating());
        movie.setReleaseYear(movieDetails.getReleaseYear());
        Movie updatedMovie = movieRepository.save(movie);
        log.info("Movie updated: {}", updatedMovie.getTitle());
        return updatedMovie;
    }

    public void deleteMovie(Long id) {
        log.info("Deleting movie with ID: {}", id);
        if (!movieRepository.existsById(id)) {
            throw new NoSuchElementException("Movie with ID " + id + " does not exist");
        }
        movieRepository.deleteById(id);
        log.info("Movie with ID {} deleted", id);
    }

    private void validateShowtimeDurations(Long movieId, int newDuration) {
        List<Showtime> showtimesList = showtimeRepository.findByMovieId(movieId);
        for (Showtime showtime : showtimesList) {
            long durationMinutes = Duration.between(showtime.getStartTime(), showtime.getEndTime()).toMinutes();
            if (durationMinutes < newDuration) {
                throw new IllegalArgumentException("Cannot update this movie duration. " +
                        "Existing Showtime from " + showtime.getStartTime() + " to " + showtime.getEndTime());
            }
        }
    }

}
