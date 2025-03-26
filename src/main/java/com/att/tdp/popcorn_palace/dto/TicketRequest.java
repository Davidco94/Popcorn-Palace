package com.att.tdp.popcorn_palace.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketRequest {

    @NotNull(message = "Showtime ID is mandatory")
    private Long showtimeId;

    @NotNull(message = "Seat number is mandatory")
    private Integer seatNumber;
}
