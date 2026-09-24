package com.moviebooking.payment;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult process(BigDecimal amount, String reference);
}
