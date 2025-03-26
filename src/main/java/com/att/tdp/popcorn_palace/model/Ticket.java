package com.att.tdp.popcorn_palace.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    @NotNull(message = "Showtime is mandatory")
    @JsonIdentityReference(alwaysAsId = true)
    @Schema(example = "1")
    private Showtime showtime;

    @NotNull(message = "Seat number is mandatory")
    @Schema(example = "20")
    private Integer seatNumber;
}
