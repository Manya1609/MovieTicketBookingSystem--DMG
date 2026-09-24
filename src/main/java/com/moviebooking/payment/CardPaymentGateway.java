package com.moviebooking.payment;

import com.moviebooking.enums.PaymentStatus;
import java.math.BigDecimal;
import java.util.UUID;

public class CardPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult process(BigDecimal amount, String reference) {
        return new PaymentResult(PaymentStatus.SUCCESS, "CARD-" + UUID.randomUUID(), "Card payment successful");
    }
}
