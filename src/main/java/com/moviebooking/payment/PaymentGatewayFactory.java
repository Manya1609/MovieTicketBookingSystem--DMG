package com.moviebooking.payment;

import com.moviebooking.enums.PaymentMethod;
import com.moviebooking.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayFactory {

    public PaymentGateway getGateway(PaymentMethod method) {
        return switch (method) {
            case UPI -> new UpiPaymentGateway();
            case CARD -> new CardPaymentGateway();
            case WALLET -> new WalletPaymentGateway();
            default -> throw new BusinessException("Unsupported payment method: " + method);
        };
    }
}
