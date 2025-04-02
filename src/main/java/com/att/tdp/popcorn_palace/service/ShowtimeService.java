package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.Configuration;
import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;
import com.att.tdp.popcorn_palace.dto.ShowtimeResponse;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.time.Duration;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;


    private Movie fetchMovieOrThrow(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie with ID " + movieId + " not found"));
    }

    private void validateShowtimeDuration(ShowtimeRequest request, int movieDurationInMinutes) {
        long minutesBetween = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        if (minutesBetween < movieDurationInMinutes) {
            throw new IllegalArgumentException("Showtime duration must be at least as long as the movie duration (" + movieDurationInMinutes + " minutes)");
        }
    }

    private void checkForOverlap(ShowtimeRequest request, Long excludeId) {
        List<Showtime> overlaps = showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
                request.getTheater(), request.getEndTime(), request.getStartTime());

        if (excludeId != null) {
            overlaps.removeIf(s -> s.getId().equals(excludeId));
        }

        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException("Showtime overlaps with another in the same theater.");
        }
    }

    private ShowtimeResponse toResponse(Showtime showtime) {
        return ShowtimeResponse.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovie().getId())
                .theater(showtime.getTheater())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .price(showtime.getPrice())
                .build();
    }

    public ShowtimeResponse addShowtime(ShowtimeRequest request) {
        log.info("Adding showtime for theater: {} starting at: {}", request.getTheater(), request.getStartTime());

        Movie movie = fetchMovieOrThrow(request.getMovieId());
        validateShowtimeDuration(request, movie.getDuration());
        checkForOverlap(request, null); // null = no ID to exclude

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .theater(request.getTheater())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .availableSeats(Configuration.numberOfSeats)
                .build();

        Showtime saved = showtimeRepository.save(showtime);
        log.info("Showtime added with ID: {}", saved.getId());
        return toResponse(saved);
    }

    public ShowtimeResponse updateShowtime(Long id, ShowtimeRequest request) {
        log.info("Updating showtime with ID: {}", id);

        Showtime existing = showtimeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Showtime with ID " + id + " not found"));

        Movie movie = fetchMovieOrThrow(request.getMovieId());
        validateShowtimeDuration(request, movie.getDuration());

        boolean timeOrTheaterChanged =
                !existing.getTheater().equals(request.getTheater()) ||
                        !existing.getStartTime().equals(request.getStartTime()) ||
                        !existing.getEndTime().equals(request.getEndTime());

        if (timeOrTheaterChanged) {
            checkForOverlap(request, id);
        }

        existing.setMovie(movie);
        existing.setTheater(request.getTheater());
        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());
        existing.setPrice(request.getPrice());
        existing.setAvailableSeats(Configuration.numberOfSeats);

        Showtime updated = showtimeRepository.save(existing);
        log.info("Showtime updated with ID: {}", updated.getId());
        return toResponse(updated);
    }

    @Transactional
    public void deleteShowtime(Long id) {
        if (!showtimeRepository.existsById(id)) {
            throw new NoSuchElementException("Showtime with ID " + id + " not found");
        }
        log.info("Deleting tickets for showtime ID: {}", id);
        ticketRepository.deleteByShowtimeId(id);
        log.info("Deleting showtime with ID: {}", id);
        showtimeRepository.deleteById(id);
        log.info("Showtime with ID {} deleted", id);
    }

    public Optional<ShowtimeResponse> getShowtime(Long id) {
        log.info("Fetching showtime with ID: {}", id);
        return showtimeRepository.findById(id).map(this::toResponse);
    }
}
