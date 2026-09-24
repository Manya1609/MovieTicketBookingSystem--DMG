package com.moviebooking.pricing;

import com.moviebooking.entity.PricingTier;
import com.moviebooking.enums.DayType;
import com.moviebooking.enums.SeatType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class PremiumWeekdayPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(PricingTier tier) {
        return tier.getBasePrice().multiply(BigDecimal.valueOf(tier.getMultiplier()));
    }

    @Override
    public SeatType getSeatType() { return SeatType.PREMIUM; }

    @Override
    public DayType getDayType() { return DayType.WEEKDAY; }
}
