package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;
import com.att.tdp.popcorn_palace.dto.ShowtimeResponse;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ShowtimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShowtimeService showtimeService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        Mockito.reset(showtimeService);
    }

    @Test
    public void testAddShowtime_Success() throws Exception {
        ShowtimeRequest request = ShowtimeRequest.builder()
                .movieId(1L)
                .price(30.0)
                .theater("Main Theater")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        ShowtimeResponse response = ShowtimeResponse.builder()
                .id(1L)
                .movieId(1L)
                .price(30.0)
                .theater("Main Theater")
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        Mockito.when(showtimeService.addShowtime(any(ShowtimeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testUpdateShowtime_Success() throws Exception {
        ShowtimeRequest request = ShowtimeRequest.builder()
                .movieId(1L)
                .price(35.0)
                .theater("Main Theater")
                .startTime(LocalDateTime.now().plusDays(2))
                .endTime(LocalDateTime.now().plusDays(2).plusHours(2))
                .build();

        ShowtimeResponse response = ShowtimeResponse.builder()
                .id(1L)
                .movieId(1L)
                .price(35.0)
                .theater("Main Theater")
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        Mockito.when(showtimeService.updateShowtime(eq(1L), any(ShowtimeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/showtimes/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(35.0));
    }

    @Test
    public void testDeleteShowtime_Success() throws Exception {
        Mockito.doNothing().when(showtimeService).deleteShowtime(1L);

        mockMvc.perform(delete("/showtimes/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Showtime deleted successfully"));
    }

    @Test
    public void testGetShowtime_Success() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = start.plusHours(2);

        ShowtimeResponse response = ShowtimeResponse.builder()
                .id(1L)
                .movieId(1L)
                .price(25.0)
                .theater("Hall A")
                .startTime(start)
                .endTime(end)
                .build();

        Mockito.when(showtimeService.getShowtime(1L)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/showtimes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.theater").value("Hall A"));
    }

    @Test
    public void testGetShowtime_NotFound() throws Exception {
        Mockito.when(showtimeService.getShowtime(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/showtimes/999"))
                .andExpect(status().isBadRequest());
    }
}
