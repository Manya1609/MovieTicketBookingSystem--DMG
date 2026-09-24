package com.moviebooking.refund;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TimeBasedRefundStrategy implements RefundStrategy {

    @Override
    public BigDecimal calculateRefund(BigDecimal originalAmount, long hoursBeforeShow, int refundPercentage) {
        if (refundPercentage <= 0) return BigDecimal.ZERO;
        return originalAmount
                .multiply(BigDecimal.valueOf(refundPercentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
