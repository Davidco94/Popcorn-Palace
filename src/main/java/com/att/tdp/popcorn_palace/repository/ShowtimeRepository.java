package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    // Find show times in a theater that overlap with the given period
    List<Showtime> findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(String theater, LocalDateTime endTime, LocalDateTime startTime);

    // Find all show times by movie ID
    List<Showtime> findByMovieId(Long movieId);
}
