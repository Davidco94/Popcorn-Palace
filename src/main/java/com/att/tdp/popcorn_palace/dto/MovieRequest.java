package com.att.tdp.popcorn_palace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovieRequest {

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String genre;

    @NotNull(message = "Duration is mandatory")
    private Integer duration;

    private String rating;

    @NotNull(message = "Release year is mandatory")
    private Integer releaseYear;
}

