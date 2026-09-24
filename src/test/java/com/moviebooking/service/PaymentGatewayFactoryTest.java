package com.moviebooking.service;

import com.moviebooking.enums.PaymentMethod;
import com.moviebooking.enums.PaymentStatus;
import com.moviebooking.payment.PaymentGateway;
import com.moviebooking.payment.PaymentGatewayFactory;
import com.moviebooking.payment.PaymentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class PaymentGatewayFactoryTest {

    private PaymentGatewayFactory factory;

    @BeforeEach
    void setUp() {
        factory = new PaymentGatewayFactory();
    }

    @Test
    void upiGateway_returnsSuccess() {
        PaymentGateway gateway = factory.getGateway(PaymentMethod.UPI);
        PaymentResult result = gateway.process(new BigDecimal("200.00"), "ref-1");
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.getTransactionId()).startsWith("UPI-");
    }

    @Test
    void cardGateway_returnsSuccess() {
        PaymentGateway gateway = factory.getGateway(PaymentMethod.CARD);
        PaymentResult result = gateway.process(new BigDecimal("300.00"), "ref-2");
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.getTransactionId()).startsWith("CARD-");
    }

    @Test
    void walletGateway_returnsSuccess() {
        PaymentGateway gateway = factory.getGateway(PaymentMethod.WALLET);
        PaymentResult result = gateway.process(new BigDecimal("150.00"), "ref-3");
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.getTransactionId()).startsWith("WALLET-");
    }

    @Test
    void eachCall_returnsUniqueTransactionId() {
        PaymentGateway g1 = factory.getGateway(PaymentMethod.UPI);
        PaymentGateway g2 = factory.getGateway(PaymentMethod.UPI);
        String tx1 = g1.process(BigDecimal.TEN, "r1").getTransactionId();
        String tx2 = g2.process(BigDecimal.TEN, "r2").getTransactionId();
        assertThat(tx1).isNotEqualTo(tx2);
    }
}
