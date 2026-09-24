package com.moviebooking.pricing;

import com.moviebooking.entity.PricingTier;
import com.moviebooking.enums.DayType;
import com.moviebooking.enums.SeatType;
import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(PricingTier tier);
    SeatType getSeatType();
    DayType getDayType();
}
