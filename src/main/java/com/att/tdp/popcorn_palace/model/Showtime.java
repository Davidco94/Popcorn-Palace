package com.att.tdp.popcorn_palace.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @Schema(example = "1")
    @NotNull(message = "Movie is mandatory")
    @JsonIdentityReference(alwaysAsId = true)
    private Movie movie;

    @NotBlank(message = "Theater is mandatory")
    private String theater;

    @NotNull(message = "Start time is mandatory")
    private LocalDateTime startTime;

    @NotNull(message = "End time is mandatory")
    private LocalDateTime endTime;

    @NotNull(message = "Price is mandatory")
    private Double price;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int availableSeats;
}
