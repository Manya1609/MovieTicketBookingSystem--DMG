package com.moviebooking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.dto.request.RegisterRequest;
import com.moviebooking.dto.response.AuthResponse;
import com.moviebooking.entity.*;
import com.moviebooking.enums.*;
import com.moviebooking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CityRepository cityRepository;
    @Autowired private TheatreRepository theatreRepository;
    @Autowired private ScreenRepository screenRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private MovieRepository movieRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private ShowSeatRepository showSeatRepository;
    @Autowired private PricingTierRepository pricingTierRepository;
    @Autowired private RefundPolicyRepository refundPolicyRepository;

    private String customerToken;
    private Long showId;
    private Long seatId1;
    private Long seatId2;

    @BeforeEach
    void setUp() throws Exception {
        customerToken = registerCustomer("customer@test.com", "pass123");
        seedData();
    }

    private String registerCustomer(String email, String password) throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Test Customer");
        req.setEmail(email);
        req.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        AuthResponse auth = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
        return auth.getToken();
    }

    private void seedData() {
        City city = new City();
        city.setName("Delhi");
        cityRepository.save(city);

        Theatre theatre = new Theatre();
        theatre.setName("PVR");
        theatre.setAddress("Connaught Place");
        theatre.setCity(city);
        theatreRepository.save(theatre);

        Screen screen = new Screen();
        screen.setName("Screen 1");
        screen.setTotalSeats(100);
        screen.setTheatre(theatre);
        screenRepository.save(screen);

        Seat seat1 = new Seat();
        seat1.setRowNumber(1);
        seat1.setSeatNumber(1);
        seat1.setSeatType(SeatType.REGULAR);
        seat1.setScreen(screen);
        seatRepository.save(seat1);
        seatId1 = seat1.getId();

        Seat seat2 = new Seat();
        seat2.setRowNumber(1);
        seat2.setSeatNumber(2);
        seat2.setSeatType(SeatType.REGULAR);
        seat2.setScreen(screen);
        seatRepository.save(seat2);
        seatId2 = seat2.getId();

        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setDescription("A mind-bending thriller");
        movie.setDuration(148);
        movie.setLanguage("English");
        movie.setGenre("Sci-Fi");
        movieRepository.save(movie);

        // Show on a weekday — use a fixed Monday
        Show show = new Show();
        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(LocalDateTime.of(2026, 6, 22, 14, 0)); // Monday
        show.setEndTime(LocalDateTime.of(2026, 6, 22, 16, 28));
        showRepository.save(show);
        showId = show.getId();

        ShowSeat ss1 = new ShowSeat();
        ss1.setShow(show);
        ss1.setSeat(seat1);
        ss1.setStatus(SeatStatus.AVAILABLE);
        showSeatRepository.save(ss1);

        ShowSeat ss2 = new ShowSeat();
        ss2.setShow(show);
        ss2.setSeat(seat2);
        ss2.setStatus(SeatStatus.AVAILABLE);
        showSeatRepository.save(ss2);

        PricingTier tier = new PricingTier();
        tier.setSeatType(SeatType.REGULAR);
        tier.setDayType(DayType.WEEKDAY);
        tier.setBasePrice(new BigDecimal("200.00"));
        tier.setMultiplier(1.0);
        pricingTierRepository.save(tier);

        RefundPolicy p1 = new RefundPolicy();
        p1.setMinHoursBeforeShow(12);
        p1.setRefundPercentage(100);
        refundPolicyRepository.save(p1);

        RefundPolicy p2 = new RefundPolicy();
        p2.setMinHoursBeforeShow(4);
        p2.setRefundPercentage(50);
        refundPolicyRepository.save(p2);

        RefundPolicy p3 = new RefundPolicy();
        p3.setMinHoursBeforeShow(1);
        p3.setRefundPercentage(25);
        refundPolicyRepository.save(p3);

        RefundPolicy p4 = new RefundPolicy();
        p4.setMinHoursBeforeShow(0);
        p4.setRefundPercentage(0);
        refundPolicyRepository.save(p4);
    }

    @Test
    void showSeats_listsAvailableSeats() throws Exception {
        mockMvc.perform(get("/api/shows/{id}/seats", showId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    @Test
    void fullBookingFlow_holdThenBook() throws Exception {
        // Create hold
        Map<String, Object> holdReq = Map.of("showId", showId, "seatIds", List.of(seatId1));
        MvcResult holdResult = mockMvc.perform(post("/api/holds")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(holdReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, Object> holdResponse = objectMapper.readValue(holdResult.getResponse().getContentAsString(), new TypeReference<>() {});
        Integer holdId = (Integer) holdResponse.get("id");

        // Confirm booking
        Map<String, Object> bookingReq = Map.of("holdId", holdId, "paymentMethod", "UPI");
        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalAmount").value(200.0));

        // Seat should now be BOOKED
        mockMvc.perform(get("/api/shows/{id}/seats", showId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.seatId == " + seatId1 + ")].status").value("BOOKED"));
    }

    @Test
    void createHold_withoutToken_returnsForbidden() throws Exception {
        Map<String, Object> holdReq = Map.of("showId", showId, "seatIds", List.of(seatId1));
        mockMvc.perform(post("/api/holds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(holdReq)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createHold_forAlreadyHeldSeat_returnsBadRequest() throws Exception {
        Map<String, Object> holdReq = Map.of("showId", showId, "seatIds", List.of(seatId1));

        mockMvc.perform(post("/api/holds")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(holdReq)))
                .andExpect(status().isCreated());

        // Second user tries same seat
        String customer2Token = registerCustomer("customer2@test.com", "pass456");
        mockMvc.perform(post("/api/holds")
                        .header("Authorization", "Bearer " + customer2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(holdReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancellationFlow_returnsUpdatedStatus() throws Exception {
        // Hold and book
        Map<String, Object> holdReq = Map.of("showId", showId, "seatIds", List.of(seatId2));
        MvcResult holdResult = mockMvc.perform(post("/api/holds")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(holdReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, Object> holdResponse2 = objectMapper.readValue(holdResult.getResponse().getContentAsString(), new TypeReference<>() {});
        Integer holdId = (Integer) holdResponse2.get("id");

        MvcResult bookingResult = mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("holdId", holdId, "paymentMethod", "CARD"))))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, Object> bookingResponse = objectMapper.readValue(bookingResult.getResponse().getContentAsString(), new TypeReference<>() {});
        Integer bookingId = (Integer) bookingResponse.get("id");

        // Cancel
        mockMvc.perform(patch("/api/bookings/{id}/cancel", bookingId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingStatus").value(org.hamcrest.Matchers.oneOf("CANCELLED", "REFUNDED")));
    }

    @Test
    void bookingHistory_returnsCustomerBookings() throws Exception {
        mockMvc.perform(get("/api/bookings/history")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
