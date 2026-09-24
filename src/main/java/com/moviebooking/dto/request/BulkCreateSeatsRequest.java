package com.moviebooking.dto.request;

import com.moviebooking.enums.SeatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BulkCreateSeatsRequest {

    @NotNull
    private List<SeatEntry> seats;

    public BulkCreateSeatsRequest() {}

    public List<SeatEntry> getSeats() { return seats; }
    public void setSeats(List<SeatEntry> seats) { this.seats = seats; }

    public static class SeatEntry {
        @Min(1)
        private int rowNumber;

        @Min(1)
        private int seatNumber;

        @NotNull
        private SeatType seatType;

        public SeatEntry() {}

        public int getRowNumber() { return rowNumber; }
        public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }
        public int getSeatNumber() { return seatNumber; }
        public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
        public SeatType getSeatType() { return seatType; }
        public void setSeatType(SeatType seatType) { this.seatType = seatType; }
    }
}
