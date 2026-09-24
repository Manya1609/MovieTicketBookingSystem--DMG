package com.moviebooking.payment;

import com.moviebooking.enums.PaymentStatus;
import java.math.BigDecimal;
import java.util.UUID;

public class WalletPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult process(BigDecimal amount, String reference) {
        return new PaymentResult(PaymentStatus.SUCCESS, "WALLET-" + UUID.randomUUID(), "Wallet payment successful");
    }
}
