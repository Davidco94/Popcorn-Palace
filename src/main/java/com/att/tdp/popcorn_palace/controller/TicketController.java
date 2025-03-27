package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.TicketRequest;
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
    public ResponseEntity<Ticket> bookTicket(@Validated @RequestBody TicketRequest ticketRequest) {
        log.info("Received request to book ticket for showtime ID: {} and seat number: {}",
                ticketRequest.getShowtimeId(), ticketRequest.getSeatNumber());
        Ticket bookedTicket = ticketService.bookTicket(ticketRequest);
        return ResponseEntity.ok(bookedTicket);
    }

    @DeleteMapping("/{ticketId}")
    @Operation(summary = "Cancel a ticket")
    public ResponseEntity<String> cancelTicket(@PathVariable Long ticketId) {
        log.info("Received request to cancel ticket ID: {}", ticketId);
        ticketService.cancelTicket(ticketId);
        return ResponseEntity.ok("Ticket deleted successfully");
    }
}
