package com.att.tdp.popcorn_palace.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(example = "84438967-f68f-4fa0-b620-0f08217e76af")
    private UUID bookingId;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    @NotNull(message = "Showtime is mandatory")
    @JsonIdentityReference(alwaysAsId = true)
    @Schema(example = "1")
    private Showtime showtime;

    @NotNull(message = "Seat number is mandatory")
    @Schema(example = "20")
    private Integer seatNumber;

    @Column(nullable = false)
    @Schema(example = "84438967-f68f-4fa0-b620-0f08217e76af")
    private UUID userId;
}
