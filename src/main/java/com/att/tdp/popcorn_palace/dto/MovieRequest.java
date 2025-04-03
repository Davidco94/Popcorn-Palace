package com.att.tdp.popcorn_palace.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovieRequest {

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String genre;

    @NotNull(message = "Duration is mandatory")
    @Min(value = 1, message = "Duration must be greater than 0")
    private Integer duration;

    @DecimalMin(value = "0.0", inclusive = false, message = "Rating must be greater than 0")
    @DecimalMax(value = "10.0", message = "Rating must be at most 10")
    private Double rating;


    @NotNull(message = "Release year is mandatory")
    @Min(value = 1900, message = "Release year must be realistic")
    private Integer releaseYear;
}

