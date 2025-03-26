package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.TicketRequest;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    @Mock
    TicketRepository ticketRepository;

    @Mock
    ShowtimeRepository showtimeRepository;

    @InjectMocks
    TicketService ticketService;

    Showtime showtime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        showtime = Showtime.builder()
                .id(1L)
                .theater("Main Hall")
                .totalSeats(50)
                .availableSeats(10)
                .build();
    }

    @Test
    void testBookTicket_Success() {
        TicketRequest request = new TicketRequest();
        request.setShowtimeId(1L);
        request.setSeatNumber(5);

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(ticketRepository.existsByShowtimeAndSeatNumber(showtime, 5)).thenReturn(false);
        when(ticketRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Ticket result = ticketService.bookTicket(request);

        assertEquals(5, result.getSeatNumber());
        assertEquals(showtime, result.getShowtime());
    }

    @Test
    void testBookTicket_SeatTaken() {
        TicketRequest request = new TicketRequest();
        request.setShowtimeId(1L);
        request.setSeatNumber(5);

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(ticketRepository.existsByShowtimeAndSeatNumber(showtime, 5)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.bookTicket(request);
        });
    }

    @Test
    void testBookTicket_InvalidSeat() {
        TicketRequest request = new TicketRequest();
        request.setShowtimeId(1L);
        request.setSeatNumber(60); // beyond total

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.bookTicket(request);
        });
    }
}
