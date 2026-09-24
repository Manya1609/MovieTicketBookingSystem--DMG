package com.moviebooking.service;

import com.moviebooking.dto.request.CreateSeatHoldRequest;
import com.moviebooking.dto.response.SeatHoldResponse;
import com.moviebooking.entity.*;
import com.moviebooking.enums.SeatStatus;
import com.moviebooking.exception.BusinessException;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeatHoldService {

    @Value("${app.hold.duration.minutes:10}")
    private int holdDurationMinutes;

    private final SeatHoldRepository seatHoldRepository;
    private final ShowSeatRepository showSeatRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;

    public SeatHoldService(SeatHoldRepository seatHoldRepository, ShowSeatRepository showSeatRepository,
                           ShowRepository showRepository, UserRepository userRepository) {
        this.seatHoldRepository = seatHoldRepository;
        this.showSeatRepository = showSeatRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SeatHoldResponse createHold(String customerEmail, CreateSeatHoldRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show", request.getShowId()));

        List<ShowSeat> showSeats = showSeatRepository.findByShowIdAndSeatIdIn(
                request.getShowId(), request.getSeatIds());

        if (showSeats.size() != request.getSeatIds().size()) {
            throw new BusinessException("One or more seats not found for this show");
        }

        for (ShowSeat ss : showSeats) {
            if (ss.getStatus() != SeatStatus.AVAILABLE) {
                throw new BusinessException("Seat row=" + ss.getSeat().getRowNumber()
                        + " num=" + ss.getSeat().getSeatNumber() + " is not available");
            }
        }

        // Mark all seats as HELD (optimistic locking applies at save)
        for (ShowSeat ss : showSeats) {
            ss.setStatus(SeatStatus.HELD);
            showSeatRepository.save(ss);
        }

        SeatHold hold = new SeatHold();
        hold.setCustomer(customer);
        hold.setShow(show);
        hold.setShowSeats(showSeats);
        hold.setCreatedAt(LocalDateTime.now());
        hold.setExpiresAt(LocalDateTime.now().plusMinutes(holdDurationMinutes));
        seatHoldRepository.save(hold);

        return SeatHoldResponse.from(hold);
    }
}
