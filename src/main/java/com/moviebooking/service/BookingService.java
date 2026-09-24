package com.moviebooking.service;

import com.moviebooking.dto.request.CreateBookingRequest;
import com.moviebooking.dto.response.BookingResponse;
import com.moviebooking.entity.*;
import com.moviebooking.enums.*;
import com.moviebooking.event.BookingCancelledEvent;
import com.moviebooking.event.BookingConfirmedEvent;
import com.moviebooking.event.RefundProcessedEvent;
import com.moviebooking.exception.BusinessException;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.payment.PaymentGateway;
import com.moviebooking.payment.PaymentGatewayFactory;
import com.moviebooking.payment.PaymentResult;
import com.moviebooking.pricing.PricingContext;
import com.moviebooking.refund.RefundStrategy;
import com.moviebooking.repository.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final PaymentRepository paymentRepository;
    private final PricingTierRepository pricingTierRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final RefundPolicyRepository refundPolicyRepository;
    private final ShowSeatRepository showSeatRepository;
    private final UserRepository userRepository;
    private final PaymentGatewayFactory paymentGatewayFactory;
    private final PricingContext pricingContext;
    private final RefundStrategy refundStrategy;
    private final ApplicationEventPublisher eventPublisher;

    public BookingService(BookingRepository bookingRepository, SeatHoldRepository seatHoldRepository,
                          PaymentRepository paymentRepository, PricingTierRepository pricingTierRepository,
                          DiscountCodeRepository discountCodeRepository, RefundPolicyRepository refundPolicyRepository,
                          ShowSeatRepository showSeatRepository, UserRepository userRepository,
                          PaymentGatewayFactory paymentGatewayFactory, PricingContext pricingContext,
                          RefundStrategy refundStrategy, ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.paymentRepository = paymentRepository;
        this.pricingTierRepository = pricingTierRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.refundPolicyRepository = refundPolicyRepository;
        this.showSeatRepository = showSeatRepository;
        this.userRepository = userRepository;
        this.paymentGatewayFactory = paymentGatewayFactory;
        this.pricingContext = pricingContext;
        this.refundStrategy = refundStrategy;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public BookingResponse createBooking(String customerEmail, CreateBookingRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SeatHold hold = seatHoldRepository.findById(request.getHoldId())
                .orElseThrow(() -> new ResourceNotFoundException("Hold", request.getHoldId()));

        if (!hold.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("Hold does not belong to the current user");
        }
        if (hold.isExpired() || hold.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Seat hold has expired. Please select seats again.");
        }

        Show show = hold.getShow();
        DayType dayType = isDayTypeWeekend(show.getStartTime()) ? DayType.WEEKEND : DayType.WEEKDAY;

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ShowSeat ss : hold.getShowSeats()) {
            SeatType seatType = ss.getSeat().getSeatType();
            PricingTier tier = pricingTierRepository.findBySeatTypeAndDayType(seatType, dayType)
                    .orElseThrow(() -> new BusinessException("No pricing configured for " + seatType + "_" + dayType));
            totalAmount = totalAmount.add(pricingContext.calculatePrice(tier));
        }

        // Apply discount
        if (request.getDiscountCode() != null && !request.getDiscountCode().isBlank()) {
            totalAmount = applyDiscount(request.getDiscountCode(), totalAmount);
        }

        // Process payment
        PaymentGateway gateway = paymentGatewayFactory.getGateway(request.getPaymentMethod());
        PaymentResult result = gateway.process(totalAmount, "BOOKING-" + System.currentTimeMillis());

        if (result.getStatus() != PaymentStatus.SUCCESS) {
            for (ShowSeat ss : hold.getShowSeats()) {
                ss.setStatus(SeatStatus.AVAILABLE);
                showSeatRepository.save(ss);
            }
            hold.setExpired(true);
            seatHoldRepository.save(hold);
            throw new BusinessException("Payment failed: " + result.getMessage());
        }

        // Mark seats as BOOKED
        for (ShowSeat ss : hold.getShowSeats()) {
            ss.setStatus(SeatStatus.BOOKED);
            showSeatRepository.save(ss);
        }

        // Create booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setShow(show);
        booking.setBookedSeats(new ArrayList<>(hold.getShowSeats()));
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setBookedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // Record payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(result.getTransactionId());
        paymentRepository.save(payment);

        // Expire the hold
        hold.setExpired(true);
        seatHoldRepository.save(hold);

        eventPublisher.publishEvent(new BookingConfirmedEvent(booking));

        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(String customerEmail, Long bookingId) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("Booking does not belong to the current user");
        }
        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessException("Only confirmed bookings can be cancelled");
        }

        long hoursBeforeShow = ChronoUnit.HOURS.between(LocalDateTime.now(), booking.getShow().getStartTime());
        if (hoursBeforeShow < 0) {
            throw new BusinessException("Cannot cancel a booking after the show has started");
        }

        int refundPercentage = refundPolicyRepository.findBestMatchingPolicy(hoursBeforeShow)
                .map(RefundPolicy::getRefundPercentage)
                .orElse(0);

        BigDecimal refundAmount = refundStrategy.calculateRefund(
                booking.getTotalAmount(), hoursBeforeShow, refundPercentage);

        // Release seats
        for (ShowSeat ss : booking.getBookedSeats()) {
            ss.setStatus(SeatStatus.AVAILABLE);
            showSeatRepository.save(ss);
        }

        booking.setBookingStatus(refundAmount.compareTo(BigDecimal.ZERO) > 0
                ? BookingStatus.REFUNDED : BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        eventPublisher.publishEvent(new BookingCancelledEvent(booking));
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            eventPublisher.publishEvent(new RefundProcessedEvent(booking, refundAmount));
        }

        return BookingResponse.from(booking);
    }

    public Page<BookingResponse> getBookingHistory(String customerEmail, Pageable pageable) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByCustomerId(customer.getId(), pageable)
                .map(BookingResponse::from);
    }

    private BigDecimal applyDiscount(String code, BigDecimal amount) {
        DiscountCode discount = discountCodeRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Invalid discount code: " + code));

        if (discount.getCurrentUsage() >= discount.getMaxUsage()) {
            throw new BusinessException("Discount code has reached its usage limit");
        }
        if (discount.getValidTill().isBefore(java.time.LocalDate.now())) {
            throw new BusinessException("Discount code has expired");
        }
        if (discount.getValidFrom().isAfter(java.time.LocalDate.now())) {
            throw new BusinessException("Discount code is not yet active");
        }

        BigDecimal discounted;
        if (discount.getDiscountType() == DiscountType.FLAT) {
            discounted = amount.subtract(discount.getDiscountValue()).max(BigDecimal.ZERO);
        } else {
            BigDecimal reduction = amount.multiply(discount.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            discounted = amount.subtract(reduction);
        }

        discount.setCurrentUsage(discount.getCurrentUsage() + 1);
        discountCodeRepository.save(discount);
        return discounted;
    }

    private boolean isDayTypeWeekend(LocalDateTime dateTime) {
        java.time.DayOfWeek day = dateTime.getDayOfWeek();
        return day == java.time.DayOfWeek.SATURDAY || day == java.time.DayOfWeek.SUNDAY;
    }
}
