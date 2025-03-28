package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;
import com.att.tdp.popcorn_palace.dto.ShowtimeResponse;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/showtimes")
@Tag(name = "Showtime")
@Slf4j
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    @Operation(summary = "Add a new showtime")
    public ResponseEntity<ShowtimeResponse> addShowtime(@Validated @RequestBody ShowtimeRequest showtimeRequest) {
        log.info("Received request to add a showtime for theater: {}", showtimeRequest.getTheater());
        ShowtimeResponse savedShowtime = showtimeService.addShowtime(showtimeRequest);
        return ResponseEntity.ok(savedShowtime);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing showtime")
    public ResponseEntity<ShowtimeResponse> updateShowtime(@PathVariable Long id, @Validated @RequestBody ShowtimeRequest showtimeDetails) {
        log.info("Received request to update showtime with ID: {}", id);
        ShowtimeResponse updatedShowtime = showtimeService.updateShowtime(id, showtimeDetails);
        log.info("Showtime with ID {} updated successfully", id);
        return ResponseEntity.ok(updatedShowtime);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a showtime")
    public ResponseEntity<String> deleteShowtime(@PathVariable Long id) {
        log.info("Received request to delete showtime with ID: {}", id);
        showtimeService.deleteShowtime(id);
        return ResponseEntity.ok("Showtime deleted successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a showtime by id")
    public ResponseEntity<ShowtimeResponse> getShowtime(@PathVariable Long id) {
        log.info("Received request to fetch showtime with ID: {}", id);
        return showtimeService.getShowtime(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found with id " + id));
    }
}
