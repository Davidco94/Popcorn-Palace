package com.att.tdp.popcorn_palace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeResponse {
    private Long id;
    private Double price;
    private Long movieId;
    private String theater;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

