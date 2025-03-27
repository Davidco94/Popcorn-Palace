package com.att.tdp.popcorn_palace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShowtimeRequest {
    @NotNull
    private Double price;

    @NotNull
    private Long movieId;

    @NotBlank
    private String theater;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull(message = "Total seats are mandatory")
    private Integer totalSeats;
}

