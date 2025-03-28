package com.att.tdp.popcorn_palace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class TicketResponse {
    private UUID bookingId;
}
