package com.moviebooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateScreenRequest {

    @NotBlank
    private String name;

    @Min(1)
    private int totalSeats;

    @NotNull
    private Long theatreId;

    public CreateScreenRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public Long getTheatreId() { return theatreId; }
    public void setTheatreId(Long theatreId) { this.theatreId = theatreId; }
}
