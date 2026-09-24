package com.moviebooking.dto.request;

import com.moviebooking.enums.DayType;
import com.moviebooking.enums.SeatType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreatePricingTierRequest {

    @NotNull
    private SeatType seatType;

    @NotNull
    private DayType dayType;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal basePrice;

    @DecimalMin("0.01")
    private double multiplier = 1.0;

    public CreatePricingTierRequest() {}

    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }
    public DayType getDayType() { return dayType; }
    public void setDayType(DayType dayType) { this.dayType = dayType; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
}
