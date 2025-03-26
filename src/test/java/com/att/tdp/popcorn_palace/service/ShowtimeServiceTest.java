package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShowtimeServiceTest {

    @Mock
    ShowtimeRepository showtimeRepository;

    @Mock
    MovieRepository movieRepository;

    @InjectMocks
    ShowtimeService showtimeService;

    Movie movie;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        movie = Movie.builder().id(1L).title("Movie").build();
    }

    @Test
    void testAddShowtime_Success() {
        ShowtimeRequest request = ShowtimeRequest.builder()
                .movieId(1L)
                .theater("Theater A")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(3))
                .price(10.0)
                .totalSeats(100)
                .build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(showtimeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Showtime result = showtimeService.addShowtime(request);

        assertEquals("Theater A", result.getTheater());
        assertEquals(100, result.getTotalSeats());
        assertEquals(100, result.getAvailableSeats());
    }

    @Test
    void testAddShowtime_Overlap() {
        ShowtimeRequest request = ShowtimeRequest.builder()
                .movieId(1L)
                .theater("Theater A")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(3))
                .price(10.0)
                .totalSeats(100)
                .build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any()))
                .thenReturn(Collections.singletonList(new Showtime()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            showtimeService.addShowtime(request);
        });

        assertTrue(exception.getMessage().contains("overlaps"));
    }
}
