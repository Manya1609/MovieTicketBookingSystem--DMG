package com.moviebooking.event;

import com.moviebooking.entity.Booking;
import java.math.BigDecimal;

public class RefundProcessedEvent {

    private final Booking booking;
    private final BigDecimal refundAmount;

    public RefundProcessedEvent(Booking booking, BigDecimal refundAmount) {
        this.booking = booking;
        this.refundAmount = refundAmount;
    }

    public Booking getBooking() { return booking; }
    public BigDecimal getRefundAmount() { return refundAmount; }
}
