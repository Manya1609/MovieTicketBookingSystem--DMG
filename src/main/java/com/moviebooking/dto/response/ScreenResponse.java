package com.moviebooking.dto.response;

import com.moviebooking.entity.Screen;

public class ScreenResponse {

    private Long id;
    private String name;
    private int totalSeats;
    private Long theatreId;
    private String theatreName;

    public ScreenResponse() {}

    public static ScreenResponse from(Screen screen) {
        ScreenResponse r = new ScreenResponse();
        r.id = screen.getId();
        r.name = screen.getName();
        r.totalSeats = screen.getTotalSeats();
        r.theatreId = screen.getTheatre().getId();
        r.theatreName = screen.getTheatre().getName();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getTotalSeats() { return totalSeats; }
    public Long getTheatreId() { return theatreId; }
    public String getTheatreName() { return theatreName; }
}
