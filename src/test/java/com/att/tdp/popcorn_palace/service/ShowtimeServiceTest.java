package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;
import com.att.tdp.popcorn_palace.dto.ShowtimeResponse;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private ShowtimeService showtimeService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        movie = Movie.builder()
                .id(1L)
                .title("Test Movie")
                .duration(120)
                .build();
    }

    @Test
    void testAddShowtime_Success() {
        ShowtimeRequest request = ShowtimeRequest.builder()
                .movieId(1L)
                .theater("Theater A")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(3))
                .price(10.0)
                .build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(showtimeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ShowtimeResponse result = showtimeService.addShowtime(request);

        assertEquals("Theater A", result.getTheater());
        assertEquals(1L, result.getMovieId());
        assertEquals(10.0, result.getPrice());
    }

    @Test
    void testAddShowtime_Overlap_ShouldThrowException() {
        ShowtimeRequest request = ShowtimeRequest.builder()
                .movieId(1L)
                .theater("Theater A")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(3))
                .price(10.0)
                .build();

        Showtime overlapping = Showtime.builder().id(99L).build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any()))
                .thenReturn(new ArrayList<>(List.of(overlapping)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showtimeService.addShowtime(request);
        });

        assertEquals("Showtime overlaps with another in the same theater.", exception.getMessage());
    }

    @Test
    void testAddShowtime_ShortDuration_ShouldThrowException() {
        ShowtimeRequest request = ShowtimeRequest.builder()
                .movieId(1L)
                .theater("Theater A")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusMinutes(90))
                .price(10.0)
                .build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showtimeService.addShowtime(request);
        });

        assertTrue(exception.getMessage().contains("duration must be at least"));
    }

    @Test
    void testUpdateShowtime_Overlap_ShouldThrowException() {
        ShowtimeRequest updateRequest = ShowtimeRequest.builder()
                .movieId(1L)
                .theater("Theater A")
                .startTime(LocalDateTime.now().plusHours(2))
                .endTime(LocalDateTime.now().plusHours(4))
                .price(15.0)
                .build();

        Showtime existing = Showtime.builder()
                .id(1L)
                .movie(movie)
                .theater("Theater A")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(3))
                .build();

        Showtime conflicting = Showtime.builder()
                .id(2L)
                .theater("Theater A")
                .startTime(LocalDateTime.now().plusHours(2))
                .endTime(LocalDateTime.now().plusHours(4))
                .build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any()))
                .thenReturn(new ArrayList<>(List.of(conflicting)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                showtimeService.updateShowtime(1L, updateRequest));

        assertTrue(exception.getMessage().contains("overlaps"));
    }
}
