package com.moviebooking.controller;

import com.moviebooking.dto.request.*;
import com.moviebooking.dto.response.*;
import com.moviebooking.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // --- Cities ---
    @PostMapping("/cities")
    public ResponseEntity<CityResponse> createCity(@Valid @RequestBody CreateCityRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createCity(req));
    }

    @GetMapping("/cities")
    public ResponseEntity<List<CityResponse>> getAllCities() {
        return ResponseEntity.ok(adminService.getAllCities());
    }

    @PutMapping("/cities/{id}")
    public ResponseEntity<CityResponse> updateCity(@PathVariable Long id, @Valid @RequestBody CreateCityRequest req) {
        return ResponseEntity.ok(adminService.updateCity(id, req));
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        adminService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    // --- Theatres ---
    @PostMapping("/theatres")
    public ResponseEntity<TheatreResponse> createTheatre(@Valid @RequestBody CreateTheatreRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createTheatre(req));
    }

    @GetMapping("/theatres")
    public ResponseEntity<List<TheatreResponse>> getAllTheatres() {
        return ResponseEntity.ok(adminService.getAllTheatres());
    }

    @PutMapping("/theatres/{id}")
    public ResponseEntity<TheatreResponse> updateTheatre(@PathVariable Long id, @Valid @RequestBody CreateTheatreRequest req) {
        return ResponseEntity.ok(adminService.updateTheatre(id, req));
    }

    @DeleteMapping("/theatres/{id}")
    public ResponseEntity<Void> deleteTheatre(@PathVariable Long id) {
        adminService.deleteTheatre(id);
        return ResponseEntity.noContent().build();
    }

    // --- Screens ---
    @PostMapping("/screens")
    public ResponseEntity<ScreenResponse> createScreen(@Valid @RequestBody CreateScreenRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createScreen(req));
    }

    @GetMapping("/screens")
    public ResponseEntity<List<ScreenResponse>> getAllScreens() {
        return ResponseEntity.ok(adminService.getAllScreens());
    }

    @PutMapping("/screens/{id}")
    public ResponseEntity<ScreenResponse> updateScreen(@PathVariable Long id, @Valid @RequestBody CreateScreenRequest req) {
        return ResponseEntity.ok(adminService.updateScreen(id, req));
    }

    @DeleteMapping("/screens/{id}")
    public ResponseEntity<Void> deleteScreen(@PathVariable Long id) {
        adminService.deleteScreen(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/screens/{id}/seats")
    public ResponseEntity<List<ShowSeatResponse>> createSeats(@PathVariable Long id,
                                                               @Valid @RequestBody BulkCreateSeatsRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createSeats(id, req));
    }

    // --- Movies ---
    @PostMapping("/movies")
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody CreateMovieRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createMovie(req));
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id, @Valid @RequestBody CreateMovieRequest req) {
        return ResponseEntity.ok(adminService.updateMovie(id, req));
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        adminService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    // --- Shows ---
    @PostMapping("/shows")
    public ResponseEntity<ShowResponse> createShow(@Valid @RequestBody CreateShowRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createShow(req));
    }

    @PutMapping("/shows/{id}")
    public ResponseEntity<ShowResponse> updateShow(@PathVariable Long id, @Valid @RequestBody CreateShowRequest req) {
        return ResponseEntity.ok(adminService.updateShow(id, req));
    }

    @DeleteMapping("/shows/{id}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id) {
        adminService.deleteShow(id);
        return ResponseEntity.noContent().build();
    }

    // --- Pricing Tiers ---
    @PostMapping("/pricing-tiers")
    public ResponseEntity<PricingTierResponse> createPricingTier(@Valid @RequestBody CreatePricingTierRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createPricingTier(req));
    }

    @GetMapping("/pricing-tiers")
    public ResponseEntity<List<PricingTierResponse>> getAllPricingTiers() {
        return ResponseEntity.ok(adminService.getAllPricingTiers());
    }

    @PutMapping("/pricing-tiers/{id}")
    public ResponseEntity<PricingTierResponse> updatePricingTier(@PathVariable Long id,
                                                                   @Valid @RequestBody CreatePricingTierRequest req) {
        return ResponseEntity.ok(adminService.updatePricingTier(id, req));
    }

    @DeleteMapping("/pricing-tiers/{id}")
    public ResponseEntity<Void> deletePricingTier(@PathVariable Long id) {
        adminService.deletePricingTier(id);
        return ResponseEntity.noContent().build();
    }

    // --- Discount Codes ---
    @PostMapping("/discount-codes")
    public ResponseEntity<DiscountCodeResponse> createDiscountCode(@Valid @RequestBody CreateDiscountCodeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createDiscountCode(req));
    }

    @GetMapping("/discount-codes")
    public ResponseEntity<List<DiscountCodeResponse>> getAllDiscountCodes() {
        return ResponseEntity.ok(adminService.getAllDiscountCodes());
    }

    @PutMapping("/discount-codes/{id}")
    public ResponseEntity<DiscountCodeResponse> updateDiscountCode(@PathVariable Long id,
                                                                    @Valid @RequestBody CreateDiscountCodeRequest req) {
        return ResponseEntity.ok(adminService.updateDiscountCode(id, req));
    }

    @DeleteMapping("/discount-codes/{id}")
    public ResponseEntity<Void> deleteDiscountCode(@PathVariable Long id) {
        adminService.deleteDiscountCode(id);
        return ResponseEntity.noContent().build();
    }

    // --- Refund Policies ---
    @PostMapping("/refund-policies")
    public ResponseEntity<RefundPolicyResponse> createRefundPolicy(@Valid @RequestBody CreateRefundPolicyRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createRefundPolicy(req));
    }

    @GetMapping("/refund-policies")
    public ResponseEntity<List<RefundPolicyResponse>> getAllRefundPolicies() {
        return ResponseEntity.ok(adminService.getAllRefundPolicies());
    }

    @PutMapping("/refund-policies/{id}")
    public ResponseEntity<RefundPolicyResponse> updateRefundPolicy(@PathVariable Long id,
                                                                    @Valid @RequestBody CreateRefundPolicyRequest req) {
        return ResponseEntity.ok(adminService.updateRefundPolicy(id, req));
    }

    @DeleteMapping("/refund-policies/{id}")
    public ResponseEntity<Void> deleteRefundPolicy(@PathVariable Long id) {
        adminService.deleteRefundPolicy(id);
        return ResponseEntity.noContent().build();
    }
}
