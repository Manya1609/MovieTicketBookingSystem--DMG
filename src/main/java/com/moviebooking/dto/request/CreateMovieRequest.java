package com.moviebooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateMovieRequest {

    @NotBlank
    private String title;

    private String description;

    @Min(1)
    private int duration;

    private String language;

    private String genre;

    public CreateMovieRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
}
