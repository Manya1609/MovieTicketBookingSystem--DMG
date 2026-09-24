package com.moviebooking.dto.response;

import com.moviebooking.entity.SeatHold;
import java.time.LocalDateTime;
import java.util.List;

public class SeatHoldResponse {

    private Long id;
    private Long showId;
    private List<Long> showSeatIds;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public SeatHoldResponse() {}

    public static SeatHoldResponse from(SeatHold hold) {
        SeatHoldResponse r = new SeatHoldResponse();
        r.id = hold.getId();
        r.showId = hold.getShow().getId();
        r.showSeatIds = hold.getShowSeats().stream().map(ss -> ss.getId()).toList();
        r.createdAt = hold.getCreatedAt();
        r.expiresAt = hold.getExpiresAt();
        return r;
    }

    public Long getId() { return id; }
    public Long getShowId() { return showId; }
    public List<Long> getShowSeatIds() { return showSeatIds; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}
