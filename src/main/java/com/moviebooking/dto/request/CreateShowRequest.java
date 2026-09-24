package com.moviebooking.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateShowRequest {

    @NotNull
    private Long movieId;

    @NotNull
    private Long screenId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    public CreateShowRequest() {}

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public Long getScreenId() { return screenId; }
    public void setScreenId(Long screenId) { this.screenId = screenId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
