package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.Configuration;
import com.att.tdp.popcorn_palace.dto.TicketRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;

    private void isSeatValid(TicketRequest ticketRequest, Showtime showtime) {
        int seatNumber = ticketRequest.getSeatNumber();
        if (seatNumber < 1 || seatNumber > Configuration.numberOfSeats) {
            throw new IllegalArgumentException("Invalid seat number");
        }

        boolean seatTaken = ticketRepository.existsByShowtimeAndSeatNumber(showtime, seatNumber);
        if (seatTaken) {
            throw new IllegalArgumentException("Seat already booked");
        }

        if (showtime.getAvailableSeats() <= 0) {
            throw new IllegalArgumentException("No seats available");
        }

        //if (ticketRepository.countBookedSeatsByShowtime(showtime.getId()) <= 0) {
        //    throw new IllegalArgumentException("No seats available");
        //}
    }

    public Ticket bookTicket(TicketRequest ticketRequest) {
        log.info("Booking ticket for showtime ID: {} and seat number: {}",
                ticketRequest.getShowtimeId(), ticketRequest.getSeatNumber());
        Showtime showtime = showtimeRepository.findById(ticketRequest.getShowtimeId())
                .orElseThrow(() -> new IllegalArgumentException("Showtime with ID " + ticketRequest.getShowtimeId() + " not found"));
        isSeatValid(ticketRequest, showtime);

        showtime.setAvailableSeats(showtime.getAvailableSeats() - 1);
        showtimeRepository.save(showtime);

        Ticket ticket = Ticket.builder()
                .showtime(showtime)
                .seatNumber(ticketRequest.getSeatNumber())
                .userId(ticketRequest.getUserId())
                .build();
        Ticket bookedTicket = ticketRepository.save(ticket);
        log.info("Ticket booked with ID: {}", bookedTicket.getBookingId());
        return bookedTicket;
    }

}
