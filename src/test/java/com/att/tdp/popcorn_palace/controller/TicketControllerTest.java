package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.TicketRequest;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testBookTicket() throws Exception {
        // Create a dummy showtime for testing
        Showtime showtime = Showtime.builder()
                .theater("Theater 1")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .price(12.50)
                .build();
        showtime.setId(1L);

        TicketRequest ticketRequest = TicketRequest.builder()
                .showtimeId(showtime.getId())
                .seatNumber(5)
                .build();

        Ticket ticket = Ticket.builder()
                .showtime(showtime)
                .seatNumber(ticketRequest.getSeatNumber())
                .build();
        ticket.setId(1L);

        Mockito.when(ticketService.bookTicket(any(TicketRequest.class))).thenReturn(ticket);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticket)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.seatNumber").value(5));
    }
}
