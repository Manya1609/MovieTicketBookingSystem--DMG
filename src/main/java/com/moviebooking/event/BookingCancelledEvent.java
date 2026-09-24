package com.moviebooking.event;

import com.moviebooking.entity.Booking;

public class BookingCancelledEvent {

    private final Booking booking;

    public BookingCancelledEvent(Booking booking) {
        this.booking = booking;
    }

    public Booking getBooking() { return booking; }
}
