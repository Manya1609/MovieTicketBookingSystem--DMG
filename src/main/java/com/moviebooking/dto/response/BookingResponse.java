package com.moviebooking.dto.response;

import com.moviebooking.entity.Booking;
import com.moviebooking.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BookingResponse {

    private Long id;
    private Long showId;
    private String movieTitle;
    private List<ShowSeatResponse> bookedSeats;
    private BigDecimal totalAmount;
    private BookingStatus bookingStatus;
    private LocalDateTime bookedAt;

    public BookingResponse() {}

    public static BookingResponse from(Booking booking) {
        BookingResponse r = new BookingResponse();
        r.id = booking.getId();
        r.showId = booking.getShow().getId();
        r.movieTitle = booking.getShow().getMovie().getTitle();
        r.bookedSeats = booking.getBookedSeats().stream().map(ShowSeatResponse::from).toList();
        r.totalAmount = booking.getTotalAmount();
        r.bookingStatus = booking.getBookingStatus();
        r.bookedAt = booking.getBookedAt();
        return r;
    }

    public Long getId() { return id; }
    public Long getShowId() { return showId; }
    public String getMovieTitle() { return movieTitle; }
    public List<ShowSeatResponse> getBookedSeats() { return bookedSeats; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BookingStatus getBookingStatus() { return bookingStatus; }
    public LocalDateTime getBookedAt() { return bookedAt; }
}
