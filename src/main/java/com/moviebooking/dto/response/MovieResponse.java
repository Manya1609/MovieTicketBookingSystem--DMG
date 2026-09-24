package com.moviebooking.dto.response;

import com.moviebooking.entity.Movie;

public class MovieResponse {

    private Long id;
    private String title;
    private String description;
    private int duration;
    private String language;
    private String genre;

    public MovieResponse() {}

    public static MovieResponse from(Movie movie) {
        MovieResponse r = new MovieResponse();
        r.id = movie.getId();
        r.title = movie.getTitle();
        r.description = movie.getDescription();
        r.duration = movie.getDuration();
        r.language = movie.getLanguage();
        r.genre = movie.getGenre();
        return r;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDuration() { return duration; }
    public String getLanguage() { return language; }
    public String getGenre() { return genre; }
}
