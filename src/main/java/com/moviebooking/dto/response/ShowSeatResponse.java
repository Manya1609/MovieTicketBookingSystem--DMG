package com.moviebooking.dto.response;

import com.moviebooking.entity.ShowSeat;
import com.moviebooking.enums.SeatStatus;
import com.moviebooking.enums.SeatType;

public class ShowSeatResponse {

    private Long id;
    private Long seatId;
    private int rowNumber;
    private int seatNumber;
    private SeatType seatType;
    private SeatStatus status;

    public ShowSeatResponse() {}

    public static ShowSeatResponse from(ShowSeat ss) {
        ShowSeatResponse r = new ShowSeatResponse();
        r.id = ss.getId();
        r.seatId = ss.getSeat().getId();
        r.rowNumber = ss.getSeat().getRowNumber();
        r.seatNumber = ss.getSeat().getSeatNumber();
        r.seatType = ss.getSeat().getSeatType();
        r.status = ss.getStatus();
        return r;
    }

    public Long getId() { return id; }
    public Long getSeatId() { return seatId; }
    public int getRowNumber() { return rowNumber; }
    public int getSeatNumber() { return seatNumber; }
    public SeatType getSeatType() { return seatType; }
    public SeatStatus getStatus() { return status; }
}
