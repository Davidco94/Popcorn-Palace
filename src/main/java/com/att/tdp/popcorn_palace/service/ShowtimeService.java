package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.Configuration;
import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    public Showtime addShowtime(ShowtimeRequest showtimeRequest) {
        log.info("Adding showtime for theater: {} starting at: {}", showtimeRequest.getTheater(), showtimeRequest.getStartTime());
        Movie movie = movieRepository.findById(showtimeRequest.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie with ID " + showtimeRequest.getMovieId() + " not found"));
        Showtime showtime = Showtime.builder()
                .movie(movie)
                .theater(showtimeRequest.getTheater())
                .startTime(showtimeRequest.getStartTime())
                .endTime(showtimeRequest.getEndTime())
                .price(showtimeRequest.getPrice())
                .availableSeats(Configuration.numberOfSeats)
                .build();

        // Check for overlapping showtimes in the same theater
        List<Showtime> overlaps = showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
                showtime.getTheater(), showtime.getEndTime(), showtime.getStartTime());
        if (!overlaps.isEmpty()) {
            log.error("Overlapping showtime detected for theater: {}", showtimeRequest.getTheater());
            throw new IllegalArgumentException("Showtime overlaps with an existing showtime in the same theater");
        }

        Showtime savedShowtime = showtimeRepository.save(showtime);
        log.info("Showtime added with ID: {}", savedShowtime.getId());
        return savedShowtime;
    }

    public Showtime updateShowtime(Long id, ShowtimeRequest showtimeRequest) {
        log.info("Updating showtime with ID: {}", id);

        Showtime existing = showtimeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Showtime with ID " + id + " not found"));

        Movie movie = movieRepository.findById(showtimeRequest.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie with ID " + showtimeRequest.getMovieId() + " not found"));

        long durationMinutes = Duration.between(showtimeRequest.getStartTime(), showtimeRequest.getEndTime()).toMinutes();
        if (durationMinutes < movie.getDuration()) {
            throw new IllegalArgumentException("Showtime duration is shorter than movie duration.");
        }

        if (!existing.getTheater().equals(showtimeRequest.getTheater()) ||
                !existing.getStartTime().equals(showtimeRequest.getStartTime()) ||
                !existing.getEndTime().equals(showtimeRequest.getEndTime())) {

            List<Showtime> overlaps = showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
                    showtimeRequest.getTheater(), showtimeRequest.getEndTime(), showtimeRequest.getStartTime());

            overlaps.removeIf(s -> s.getId().equals(id)); // Ignore self

            if (!overlaps.isEmpty()) {
                throw new IllegalArgumentException("Showtime overlaps with another in the same theater.");
            }
        }
        existing.setMovie(movie);
        existing.setTheater(showtimeRequest.getTheater());
        existing.setStartTime(showtimeRequest.getStartTime());
        existing.setEndTime(showtimeRequest.getEndTime());
        existing.setPrice(showtimeRequest.getPrice());
        existing.setAvailableSeats(Configuration.numberOfSeats);

        Showtime updated = showtimeRepository.save(existing);
        log.info("Showtime updated with ID: {}", updated.getId());
        return updated;
    }

    //לפני מחיקה נמחוק את כל הטיקטיםם שקשורים לID הזה
    public void deleteShowtime(Long id) {
        log.info("Deleting showtime with ID: {}", id);
        showtimeRepository.deleteById(id);
        log.info("Showtime with ID {} deleted", id);
    }

    public Optional<Showtime> getShowtime(Long id) {
        log.info("Fetching showtime with ID: {}", id);
        return showtimeRepository.findById(id);
    }
}
