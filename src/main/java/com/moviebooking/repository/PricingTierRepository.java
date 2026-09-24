package com.moviebooking.repository;

import com.moviebooking.entity.PricingTier;
import com.moviebooking.enums.DayType;
import com.moviebooking.enums.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PricingTierRepository extends JpaRepository<PricingTier, Long> {
    Optional<PricingTier> findBySeatTypeAndDayType(SeatType seatType, DayType dayType);
}
