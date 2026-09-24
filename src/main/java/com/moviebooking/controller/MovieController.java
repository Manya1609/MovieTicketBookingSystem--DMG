package com.moviebooking.controller;

import com.moviebooking.dto.response.MovieResponse;
import com.moviebooking.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<Page<MovieResponse>> getAllMovies(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(movieService.getAllMovies(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }
}
