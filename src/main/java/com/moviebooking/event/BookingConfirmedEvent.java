package com.moviebooking.event;

import com.moviebooking.entity.Booking;

public class BookingConfirmedEvent {

    private final Booking booking;

    public BookingConfirmedEvent(Booking booking) {
        this.booking = booking;
    }

    public Booking getBooking() { return booking; }
}
