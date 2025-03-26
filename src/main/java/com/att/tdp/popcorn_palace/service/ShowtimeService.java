package com.att.tdp.popcorn_palace.service;

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
                .totalSeats(showtimeRequest.getTotalSeats())
                .availableSeats(showtimeRequest.getTotalSeats())
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

    public Optional<Showtime> updateShowtime(Long id, Showtime showtimeDetails) {
        log.info("Updating showtime with ID: {}", id);
        return showtimeRepository.findById(id).map(showtime -> {
            // If theater or time slots change, check for overlaps
            if (!showtime.getTheater().equals(showtimeDetails.getTheater()) ||
                    !showtime.getStartTime().equals(showtimeDetails.getStartTime()) ||
                    !showtime.getEndTime().equals(showtimeDetails.getEndTime())) {

                List<Showtime> overlaps = showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
                        showtimeDetails.getTheater(), showtimeDetails.getEndTime(), showtimeDetails.getStartTime());
                overlaps.removeIf(s -> s.getId().equals(id));
                if (!overlaps.isEmpty()) {
                    log.error("Overlapping showtime detected during update for theater: {}", showtimeDetails.getTheater());
                    throw new IllegalArgumentException("Showtime overlaps with an existing showtime in the same theater");
                }
            }
            showtime.setMovie(showtimeDetails.getMovie());
            showtime.setTheater(showtimeDetails.getTheater());
            showtime.setStartTime(showtimeDetails.getStartTime());
            showtime.setEndTime(showtimeDetails.getEndTime());
            showtime.setPrice(showtimeDetails.getPrice());
            Showtime updatedShowtime = showtimeRepository.save(showtime);
            log.info("Showtime updated with ID: {}", updatedShowtime.getId());
            return updatedShowtime;
        });
    }

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
