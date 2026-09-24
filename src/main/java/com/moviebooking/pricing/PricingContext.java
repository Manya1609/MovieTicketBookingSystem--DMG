package com.moviebooking.pricing;

import com.moviebooking.entity.PricingTier;
import com.moviebooking.enums.DayType;
import com.moviebooking.enums.SeatType;
import com.moviebooking.exception.BusinessException;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
public class PricingContext {

    private final List<PricingStrategy> strategies;

    public PricingContext(List<PricingStrategy> strategies) {
        this.strategies = strategies;
    }

    public BigDecimal calculatePrice(PricingTier tier) {
        SeatType seatType = tier.getSeatType();
        DayType dayType = tier.getDayType();
        return strategies.stream()
                .filter(s -> s.getSeatType() == seatType && s.getDayType() == dayType)
                .findFirst()
                .orElseThrow(() -> new BusinessException("No pricing strategy for " + seatType + "_" + dayType))
                .calculatePrice(tier);
    }
}
