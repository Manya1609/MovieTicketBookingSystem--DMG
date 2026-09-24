package com.moviebooking.refund;

import java.math.BigDecimal;

public interface RefundStrategy {
    BigDecimal calculateRefund(BigDecimal originalAmount, long hoursBeforeShow, int refundPercentage);
}
