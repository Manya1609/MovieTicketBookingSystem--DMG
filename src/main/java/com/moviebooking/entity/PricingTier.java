package com.moviebooking.entity;

import com.moviebooking.enums.DayType;
import com.moviebooking.enums.SeatType;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"seatType", "dayType"}))
public class PricingTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayType dayType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private double multiplier;

    public PricingTier() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }
    public DayType getDayType() { return dayType; }
    public void setDayType(DayType dayType) { this.dayType = dayType; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
}
