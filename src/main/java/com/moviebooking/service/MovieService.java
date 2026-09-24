package com.moviebooking.service;

import com.moviebooking.dto.response.MovieResponse;
import com.moviebooking.entity.Movie;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(MovieResponse::from);
    }

    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", id));
        return MovieResponse.from(movie);
    }
}
