package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.MovieRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        Mockito.reset(movieService);
    }

    @Test
    public void testAddMovie_Success() throws Exception {
        MovieRequest request = MovieRequest.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .duration(148)
                .rating(8.8)
                .releaseYear(2010)
                .build();

        Movie response = Movie.builder()
                .id(1L)
                .title("Inception")
                .genre("Sci-Fi")
                .duration(148)
                .rating(8.8)
                .releaseYear(2010)
                .build();

        Mockito.when(movieService.addMovie(any(MovieRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testGetAllMovies_EmptyList() throws Exception {
        Mockito.when(movieService.getAllMovies()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testUpdateMovie_Success() throws Exception {
        MovieRequest request = MovieRequest.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .duration(148)
                .rating(9.0)
                .releaseYear(2010)
                .build();

        Movie response = Movie.builder()
                .id(1L)
                .title("Inception")
                .genre("Sci-Fi")
                .duration(148)
                .rating(9.0)
                .releaseYear(2010)
                .build();

        Mockito.when(movieService.updateMovie(eq(1L), any(Movie.class))).thenReturn(response);

        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(9.0));
    }

    @Test
    public void testDeleteMovie_Success() throws Exception {
        Mockito.doNothing().when(movieService).deleteMovie(1L);

        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie deleted successfully"));
    }
}