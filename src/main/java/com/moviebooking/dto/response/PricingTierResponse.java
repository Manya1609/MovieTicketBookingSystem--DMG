package com.moviebooking.dto.response;

import com.moviebooking.entity.PricingTier;
import com.moviebooking.enums.DayType;
import com.moviebooking.enums.SeatType;
import java.math.BigDecimal;

public class PricingTierResponse {

    private Long id;
    private SeatType seatType;
    private DayType dayType;
    private BigDecimal basePrice;
    private double multiplier;
    private BigDecimal effectivePrice;

    public PricingTierResponse() {}

    public static PricingTierResponse from(PricingTier tier) {
        PricingTierResponse r = new PricingTierResponse();
        r.id = tier.getId();
        r.seatType = tier.getSeatType();
        r.dayType = tier.getDayType();
        r.basePrice = tier.getBasePrice();
        r.multiplier = tier.getMultiplier();
        r.effectivePrice = tier.getBasePrice().multiply(BigDecimal.valueOf(tier.getMultiplier()));
        return r;
    }

    public Long getId() { return id; }
    public SeatType getSeatType() { return seatType; }
    public DayType getDayType() { return dayType; }
    public BigDecimal getBasePrice() { return basePrice; }
    public double getMultiplier() { return multiplier; }
    public BigDecimal getEffectivePrice() { return effectivePrice; }
}
