package com.moviebooking.service;

import com.moviebooking.refund.TimeBasedRefundStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class RefundStrategyTest {

    private TimeBasedRefundStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new TimeBasedRefundStrategy();
    }

    @Test
    void fullRefund_whenOverTwelveHours() {
        BigDecimal result = strategy.calculateRefund(new BigDecimal("500.00"), 13, 100);
        assertThat(result).isEqualByComparingTo("500.00");
    }

    @Test
    void halfRefund_whenFourToTwelveHours() {
        BigDecimal result = strategy.calculateRefund(new BigDecimal("500.00"), 6, 50);
        assertThat(result).isEqualByComparingTo("250.00");
    }

    @Test
    void quarterRefund_whenOneToFourHours() {
        BigDecimal result = strategy.calculateRefund(new BigDecimal("500.00"), 2, 25);
        assertThat(result).isEqualByComparingTo("125.00");
    }

    @Test
    void noRefund_whenZeroPercent() {
        BigDecimal result = strategy.calculateRefund(new BigDecimal("500.00"), 0, 0);
        assertThat(result).isEqualByComparingTo("0.00");
    }

    @Test
    void refund_roundsHalfUp() {
        BigDecimal result = strategy.calculateRefund(new BigDecimal("100.00"), 6, 33);
        assertThat(result).isEqualByComparingTo("33.00");
    }
}
