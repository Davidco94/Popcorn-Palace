package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.TicketRequest;
import com.att.tdp.popcorn_palace.dto.TicketResponse;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private TicketService ticketService;

    private Showtime showtime;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        showtime = Showtime.builder()
                .id(1L)
                .theater("Main Hall")
                .availableSeats(10)
                .build();
        userId = UUID.randomUUID();
    }

    @Test
    void testBookTicket_Success() {
        TicketRequest request = new TicketRequest(1L, 5, userId);

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(ticketRepository.existsByShowtimeAndSeatNumber(showtime, 5)).thenReturn(false);
        when(ticketRepository.save(any())).thenAnswer(i -> {
            Ticket t = i.getArgument(0);
            t.setBookingId(UUID.randomUUID());
            return t;
        });

        TicketResponse response = ticketService.bookTicket(request);

        assertNotNull(response.getBookingId());
    }

    @Test
    void testBookTicket_SeatTaken() {
        TicketRequest request = new TicketRequest(1L, 5, userId);

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(ticketRepository.existsByShowtimeAndSeatNumber(showtime, 5)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> ticketService.bookTicket(request));
    }

    @Test
    void testBookTicket_InvalidSeatNumber() {
        TicketRequest request = new TicketRequest(1L, 0, userId); // Invalid seat number

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

        assertThrows(IllegalArgumentException.class, () -> ticketService.bookTicket(request));
    }

    @Test
    void testBookTicket_NoSeatsAvailable() {
        showtime.setAvailableSeats(0);
        TicketRequest request = new TicketRequest(1L, 5, userId);

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(ticketRepository.existsByShowtimeAndSeatNumber(showtime, 5)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> ticketService.bookTicket(request));
    }

    @Test
    void testBookTicket_ShowtimeNotFound() {
        TicketRequest request = new TicketRequest(1L, 5, userId);

        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ticketService.bookTicket(request));
    }
}