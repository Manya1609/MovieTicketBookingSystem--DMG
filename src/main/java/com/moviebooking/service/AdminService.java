package com.moviebooking.service;

import com.moviebooking.dto.request.*;
import com.moviebooking.dto.response.*;
import com.moviebooking.entity.*;
import com.moviebooking.enums.SeatStatus;
import com.moviebooking.exception.BusinessException;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final CityRepository cityRepository;
    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final PricingTierRepository pricingTierRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final RefundPolicyRepository refundPolicyRepository;

    public AdminService(CityRepository cityRepository, TheatreRepository theatreRepository,
                        ScreenRepository screenRepository, SeatRepository seatRepository,
                        MovieRepository movieRepository, ShowRepository showRepository,
                        ShowSeatRepository showSeatRepository, PricingTierRepository pricingTierRepository,
                        DiscountCodeRepository discountCodeRepository, RefundPolicyRepository refundPolicyRepository) {
        this.cityRepository = cityRepository;
        this.theatreRepository = theatreRepository;
        this.screenRepository = screenRepository;
        this.seatRepository = seatRepository;
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.pricingTierRepository = pricingTierRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.refundPolicyRepository = refundPolicyRepository;
    }

    // --- City ---
    @Transactional
    public CityResponse createCity(CreateCityRequest req) {
        City city = new City();
        city.setName(req.getName());
        return CityResponse.from(cityRepository.save(city));
    }

    public List<CityResponse> getAllCities() {
        return cityRepository.findAll().stream().map(CityResponse::from).toList();
    }

    @Transactional
    public CityResponse updateCity(Long id, CreateCityRequest req) {
        City city = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City", id));
        city.setName(req.getName());
        return CityResponse.from(cityRepository.save(city));
    }

    @Transactional
    public void deleteCity(Long id) {
        if (!cityRepository.existsById(id)) throw new ResourceNotFoundException("City", id);
        cityRepository.deleteById(id);
    }

    // --- Theatre ---
    @Transactional
    public TheatreResponse createTheatre(CreateTheatreRequest req) {
        City city = cityRepository.findById(req.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City", req.getCityId()));
        Theatre theatre = new Theatre();
        theatre.setName(req.getName());
        theatre.setAddress(req.getAddress());
        theatre.setCity(city);
        return TheatreResponse.from(theatreRepository.save(theatre));
    }

    public List<TheatreResponse> getAllTheatres() {
        return theatreRepository.findAll().stream().map(TheatreResponse::from).toList();
    }

    @Transactional
    public TheatreResponse updateTheatre(Long id, CreateTheatreRequest req) {
        Theatre theatre = theatreRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Theatre", id));
        theatre.setName(req.getName());
        theatre.setAddress(req.getAddress());
        if (req.getCityId() != null) {
            City city = cityRepository.findById(req.getCityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City", req.getCityId()));
            theatre.setCity(city);
        }
        return TheatreResponse.from(theatreRepository.save(theatre));
    }

    @Transactional
    public void deleteTheatre(Long id) {
        if (!theatreRepository.existsById(id)) throw new ResourceNotFoundException("Theatre", id);
        theatreRepository.deleteById(id);
    }

    // --- Screen ---
    @Transactional
    public ScreenResponse createScreen(CreateScreenRequest req) {
        Theatre theatre = theatreRepository.findById(req.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre", req.getTheatreId()));
        Screen screen = new Screen();
        screen.setName(req.getName());
        screen.setTotalSeats(req.getTotalSeats());
        screen.setTheatre(theatre);
        return ScreenResponse.from(screenRepository.save(screen));
    }

    public List<ScreenResponse> getAllScreens() {
        return screenRepository.findAll().stream().map(ScreenResponse::from).toList();
    }

    @Transactional
    public ScreenResponse updateScreen(Long id, CreateScreenRequest req) {
        Screen screen = screenRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Screen", id));
        screen.setName(req.getName());
        screen.setTotalSeats(req.getTotalSeats());
        return ScreenResponse.from(screenRepository.save(screen));
    }

    @Transactional
    public void deleteScreen(Long id) {
        if (!screenRepository.existsById(id)) throw new ResourceNotFoundException("Screen", id);
        screenRepository.deleteById(id);
    }

    // --- Seats ---
    @Transactional
    public List<ShowSeatResponse> createSeats(Long screenId, BulkCreateSeatsRequest req) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen", screenId));
        List<Seat> seats = req.getSeats().stream().map(entry -> {
            Seat seat = new Seat();
            seat.setRowNumber(entry.getRowNumber());
            seat.setSeatNumber(entry.getSeatNumber());
            seat.setSeatType(entry.getSeatType());
            seat.setScreen(screen);
            return seatRepository.save(seat);
        }).toList();
        // Return basic info — these are seats, not show seats yet
        return seats.stream().map(seat -> {
            ShowSeatResponse r = new ShowSeatResponse();
            return r;
        }).toList();
    }

    // --- Movie ---
    @Transactional
    public MovieResponse createMovie(CreateMovieRequest req) {
        Movie movie = new Movie();
        movie.setTitle(req.getTitle());
        movie.setDescription(req.getDescription());
        movie.setDuration(req.getDuration());
        movie.setLanguage(req.getLanguage());
        movie.setGenre(req.getGenre());
        return MovieResponse.from(movieRepository.save(movie));
    }

    @Transactional
    public MovieResponse updateMovie(Long id, CreateMovieRequest req) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movie", id));
        movie.setTitle(req.getTitle());
        movie.setDescription(req.getDescription());
        movie.setDuration(req.getDuration());
        movie.setLanguage(req.getLanguage());
        movie.setGenre(req.getGenre());
        return MovieResponse.from(movieRepository.save(movie));
    }

    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) throw new ResourceNotFoundException("Movie", id);
        movieRepository.deleteById(id);
    }

    // --- Show ---
    @Transactional
    public ShowResponse createShow(CreateShowRequest req) {
        Movie movie = movieRepository.findById(req.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", req.getMovieId()));
        Screen screen = screenRepository.findById(req.getScreenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen", req.getScreenId()));

        if (showRepository.existsOverlappingShow(screen.getId(), req.getStartTime(), req.getEndTime())) {
            throw new BusinessException("Screen already has a show scheduled in this time slot");
        }

        Show show = new Show();
        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(req.getStartTime());
        show.setEndTime(req.getEndTime());
        showRepository.save(show);

        // Auto-create ShowSeat records for all seats on this screen
        List<Seat> seats = seatRepository.findByScreenId(screen.getId());
        for (Seat seat : seats) {
            ShowSeat ss = new ShowSeat();
            ss.setShow(show);
            ss.setSeat(seat);
            ss.setStatus(SeatStatus.AVAILABLE);
            showSeatRepository.save(ss);
        }

        return ShowResponse.from(show);
    }

    @Transactional
    public ShowResponse updateShow(Long id, CreateShowRequest req) {
        Show show = showRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Show", id));
        show.setStartTime(req.getStartTime());
        show.setEndTime(req.getEndTime());
        return ShowResponse.from(showRepository.save(show));
    }

    @Transactional
    public void deleteShow(Long id) {
        if (!showRepository.existsById(id)) throw new ResourceNotFoundException("Show", id);
        showRepository.deleteById(id);
    }

    // --- PricingTier ---
    @Transactional
    public PricingTierResponse createPricingTier(CreatePricingTierRequest req) {
        PricingTier tier = new PricingTier();
        tier.setSeatType(req.getSeatType());
        tier.setDayType(req.getDayType());
        tier.setBasePrice(req.getBasePrice());
        tier.setMultiplier(req.getMultiplier());
        return PricingTierResponse.from(pricingTierRepository.save(tier));
    }

    public List<PricingTierResponse> getAllPricingTiers() {
        return pricingTierRepository.findAll().stream().map(PricingTierResponse::from).toList();
    }

    @Transactional
    public PricingTierResponse updatePricingTier(Long id, CreatePricingTierRequest req) {
        PricingTier tier = pricingTierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PricingTier", id));
        tier.setSeatType(req.getSeatType());
        tier.setDayType(req.getDayType());
        tier.setBasePrice(req.getBasePrice());
        tier.setMultiplier(req.getMultiplier());
        return PricingTierResponse.from(pricingTierRepository.save(tier));
    }

    @Transactional
    public void deletePricingTier(Long id) {
        if (!pricingTierRepository.existsById(id)) throw new ResourceNotFoundException("PricingTier", id);
        pricingTierRepository.deleteById(id);
    }

    // --- DiscountCode ---
    @Transactional
    public DiscountCodeResponse createDiscountCode(CreateDiscountCodeRequest req) {
        DiscountCode dc = new DiscountCode();
        dc.setCode(req.getCode());
        dc.setDiscountType(req.getDiscountType());
        dc.setDiscountValue(req.getDiscountValue());
        dc.setValidFrom(req.getValidFrom());
        dc.setValidTill(req.getValidTill());
        dc.setMaxUsage(req.getMaxUsage());
        return DiscountCodeResponse.from(discountCodeRepository.save(dc));
    }

    public List<DiscountCodeResponse> getAllDiscountCodes() {
        return discountCodeRepository.findAll().stream().map(DiscountCodeResponse::from).toList();
    }

    @Transactional
    public DiscountCodeResponse updateDiscountCode(Long id, CreateDiscountCodeRequest req) {
        DiscountCode dc = discountCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DiscountCode", id));
        dc.setCode(req.getCode());
        dc.setDiscountType(req.getDiscountType());
        dc.setDiscountValue(req.getDiscountValue());
        dc.setValidFrom(req.getValidFrom());
        dc.setValidTill(req.getValidTill());
        dc.setMaxUsage(req.getMaxUsage());
        return DiscountCodeResponse.from(discountCodeRepository.save(dc));
    }

    @Transactional
    public void deleteDiscountCode(Long id) {
        if (!discountCodeRepository.existsById(id)) throw new ResourceNotFoundException("DiscountCode", id);
        discountCodeRepository.deleteById(id);
    }

    // --- RefundPolicy ---
    @Transactional
    public RefundPolicyResponse createRefundPolicy(CreateRefundPolicyRequest req) {
        RefundPolicy policy = new RefundPolicy();
        policy.setMinHoursBeforeShow(req.getMinHoursBeforeShow());
        policy.setRefundPercentage(req.getRefundPercentage());
        return RefundPolicyResponse.from(refundPolicyRepository.save(policy));
    }

    public List<RefundPolicyResponse> getAllRefundPolicies() {
        return refundPolicyRepository.findAll().stream().map(RefundPolicyResponse::from).toList();
    }

    @Transactional
    public RefundPolicyResponse updateRefundPolicy(Long id, CreateRefundPolicyRequest req) {
        RefundPolicy policy = refundPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RefundPolicy", id));
        policy.setMinHoursBeforeShow(req.getMinHoursBeforeShow());
        policy.setRefundPercentage(req.getRefundPercentage());
        return RefundPolicyResponse.from(refundPolicyRepository.save(policy));
    }

    @Transactional
    public void deleteRefundPolicy(Long id) {
        if (!refundPolicyRepository.existsById(id)) throw new ResourceNotFoundException("RefundPolicy", id);
        refundPolicyRepository.deleteById(id);
    }
}
