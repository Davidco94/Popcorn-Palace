package com.att.tdp.popcorn_palace.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketRequest {

    @NotNull(message = "Showtime ID is mandatory")
    private Long showtimeId;

    @NotNull(message = "Seat number is mandatory")
    private Integer seatNumber;

    @NotNull(message = "User ID is mandatory")
    private UUID userId;
}
