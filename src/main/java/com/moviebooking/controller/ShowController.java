package com.moviebooking.controller;

import com.moviebooking.dto.response.ShowResponse;
import com.moviebooking.dto.response.ShowSeatResponse;
import com.moviebooking.service.ShowService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping("/api/cities/{cityId}/shows")
    public ResponseEntity<Page<ShowResponse>> getShowsByCity(
            @PathVariable Long cityId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(showService.getShowsByCity(cityId, pageable));
    }

    @GetMapping("/api/shows/{id}")
    public ResponseEntity<ShowResponse> getShowById(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowById(id));
    }

    @GetMapping("/api/shows/{id}/seats")
    public ResponseEntity<List<ShowSeatResponse>> getShowSeats(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowSeats(id));
    }
}
