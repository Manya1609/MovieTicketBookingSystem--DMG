package com.moviebooking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateSeatHoldRequest {

    @NotNull
    private Long showId;

    @NotEmpty
    private List<Long> seatIds;

    public CreateSeatHoldRequest() {}

    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }
    public List<Long> getSeatIds() { return seatIds; }
    public void setSeatIds(List<Long> seatIds) { this.seatIds = seatIds; }
}
