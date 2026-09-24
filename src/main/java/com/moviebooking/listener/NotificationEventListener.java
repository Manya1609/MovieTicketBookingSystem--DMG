package com.moviebooking.listener;

import com.moviebooking.entity.Booking;
import com.moviebooking.entity.Notification;
import com.moviebooking.enums.NotificationStatus;
import com.moviebooking.enums.NotificationType;
import com.moviebooking.event.BookingCancelledEvent;
import com.moviebooking.event.BookingConfirmedEvent;
import com.moviebooking.event.RefundProcessedEvent;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationRepository notificationRepository;
    private final BookingRepository bookingRepository;

    public NotificationEventListener(NotificationRepository notificationRepository,
                                     BookingRepository bookingRepository) {
        this.notificationRepository = notificationRepository;
        this.bookingRepository = bookingRepository;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingConfirmed(BookingConfirmedEvent event) {
        Booking booking = bookingRepository.findById(event.getBooking().getId()).orElse(null);
        if (booking == null) return;

        Notification n = new Notification();
        n.setUser(booking.getCustomer());
        n.setType(NotificationType.BOOKING_CONFIRMED);
        n.setMessage("Booking #" + booking.getId() + " confirmed for " + booking.getShow().getMovie().getTitle());
        n.setStatus(NotificationStatus.SENT);
        notificationRepository.save(n);
        log.info("Booking confirmed notification sent to user {}", booking.getCustomer().getEmail());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingCancelled(BookingCancelledEvent event) {
        Booking booking = bookingRepository.findById(event.getBooking().getId()).orElse(null);
        if (booking == null) return;

        Notification n = new Notification();
        n.setUser(booking.getCustomer());
        n.setType(NotificationType.BOOKING_CANCELLED);
        n.setMessage("Booking #" + booking.getId() + " has been cancelled.");
        n.setStatus(NotificationStatus.SENT);
        notificationRepository.save(n);
        log.info("Booking cancelled notification sent to user {}", booking.getCustomer().getEmail());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRefundProcessed(RefundProcessedEvent event) {
        Booking booking = bookingRepository.findById(event.getBooking().getId()).orElse(null);
        if (booking == null) return;

        Notification n = new Notification();
        n.setUser(booking.getCustomer());
        n.setType(NotificationType.REFUND_PROCESSED);
        n.setMessage("Refund of " + event.getRefundAmount() + " processed for booking #" + booking.getId());
        n.setStatus(NotificationStatus.SENT);
        notificationRepository.save(n);
        log.info("Refund notification sent to user {}", booking.getCustomer().getEmail());
    }
}
