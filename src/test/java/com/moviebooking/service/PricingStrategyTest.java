package com.moviebooking.service;

import com.moviebooking.entity.PricingTier;
import com.moviebooking.enums.DayType;
import com.moviebooking.enums.SeatType;
import com.moviebooking.exception.BusinessException;
import com.moviebooking.pricing.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PricingStrategyTest {

    private PricingContext pricingContext;

    @BeforeEach
    void setUp() {
        pricingContext = new PricingContext(List.of(
                new RegularWeekdayPricingStrategy(),
                new RegularWeekendPricingStrategy(),
                new PremiumWeekdayPricingStrategy(),
                new PremiumWeekendPricingStrategy()
        ));
    }

    @Test
    void regularWeekday_calculatesBasePrice() {
        PricingTier tier = tier(SeatType.REGULAR, DayType.WEEKDAY, new BigDecimal("200.00"), 1.0);
        assertThat(pricingContext.calculatePrice(tier)).isEqualByComparingTo("200.00");
    }

    @Test
    void regularWeekend_appliesMultiplier() {
        PricingTier tier = tier(SeatType.REGULAR, DayType.WEEKEND, new BigDecimal("200.00"), 1.5);
        assertThat(pricingContext.calculatePrice(tier)).isEqualByComparingTo("300.00");
    }

    @Test
    void premiumWeekday_appliesMultiplier() {
        PricingTier tier = tier(SeatType.PREMIUM, DayType.WEEKDAY, new BigDecimal("200.00"), 1.5);
        assertThat(pricingContext.calculatePrice(tier)).isEqualByComparingTo("300.00");
    }

    @Test
    void premiumWeekend_appliesHighestMultiplier() {
        PricingTier tier = tier(SeatType.PREMIUM, DayType.WEEKEND, new BigDecimal("200.00"), 2.0);
        assertThat(pricingContext.calculatePrice(tier)).isEqualByComparingTo("400.00");
    }

    @Test
    void missingStrategy_throwsBusinessException() {
        PricingContext emptyContext = new PricingContext(List.of());
        PricingTier tier = tier(SeatType.REGULAR, DayType.WEEKDAY, new BigDecimal("200.00"), 1.0);
        assertThatThrownBy(() -> emptyContext.calculatePrice(tier))
                .isInstanceOf(BusinessException.class);
    }

    private PricingTier tier(SeatType seatType, DayType dayType, BigDecimal basePrice, double multiplier) {
        PricingTier t = new PricingTier();
        t.setSeatType(seatType);
        t.setDayType(dayType);
        t.setBasePrice(basePrice);
        t.setMultiplier(multiplier);
        return t;
    }
}
