package com.moviebooking.dto.response;

import com.moviebooking.entity.Show;
import java.time.LocalDateTime;

public class ShowResponse {

    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long screenId;
    private String screenName;
    private String theatreName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ShowResponse() {}

    public static ShowResponse from(Show show) {
        ShowResponse r = new ShowResponse();
        r.id = show.getId();
        r.movieId = show.getMovie().getId();
        r.movieTitle = show.getMovie().getTitle();
        r.screenId = show.getScreen().getId();
        r.screenName = show.getScreen().getName();
        r.theatreName = show.getScreen().getTheatre().getName();
        r.startTime = show.getStartTime();
        r.endTime = show.getEndTime();
        return r;
    }

    public Long getId() { return id; }
    public Long getMovieId() { return movieId; }
    public String getMovieTitle() { return movieTitle; }
    public Long getScreenId() { return screenId; }
    public String getScreenName() { return screenName; }
    public String getTheatreName() { return theatreName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
}
