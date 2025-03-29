package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.MovieRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MovieControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAddMovie() throws Exception {
        Movie movie = Movie.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .duration(148)
                .rating(5.4)
                .releaseYear(2010)
                .build();
        movie.setId(1L);

        MovieRequest movieRequest = MovieRequest.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .duration(148)
                .rating(3.5)
                .releaseYear(2010)
                .build();

        Mockito.when(movieService.addMovie(any(MovieRequest.class))).thenReturn(movie);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    public void testGetAllMovies() throws Exception {
        Movie movie = Movie.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .duration(148)
                .rating(5.5)
                .releaseYear(2010)
                .build();
        movie.setId(1L);

        Mockito.when(movieService.getAllMovies()).thenReturn(Collections.singletonList(movie));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Inception"));
    }
}
