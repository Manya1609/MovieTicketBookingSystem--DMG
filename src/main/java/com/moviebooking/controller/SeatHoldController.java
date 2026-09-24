package com.moviebooking.controller;

import com.moviebooking.dto.request.CreateSeatHoldRequest;
import com.moviebooking.dto.response.SeatHoldResponse;
import com.moviebooking.service.SeatHoldService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/holds")
public class SeatHoldController {

    private final SeatHoldService seatHoldService;

    public SeatHoldController(SeatHoldService seatHoldService) {
        this.seatHoldService = seatHoldService;
    }

    @PostMapping
    public ResponseEntity<SeatHoldResponse> createHold(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateSeatHoldRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seatHoldService.createHold(userDetails.getUsername(), request));
    }
}
