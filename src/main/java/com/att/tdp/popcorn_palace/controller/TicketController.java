package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.TicketRequest;
import com.att.tdp.popcorn_palace.dto.TicketResponse;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets")
@Slf4j
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @Operation(summary = "Book a ticket")
    public ResponseEntity<TicketResponse> bookTicket(@Validated @RequestBody TicketRequest ticketRequest) {
        log.info("Received request to book ticket for showtime ID: {} and seat number: {}",
                ticketRequest.getShowtimeId(), ticketRequest.getSeatNumber());
        TicketResponse bookedTicket = ticketService.bookTicket(ticketRequest);
        return ResponseEntity.ok(bookedTicket);
    }

}
