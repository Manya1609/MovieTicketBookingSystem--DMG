package com.moviebooking.dto.request;

import com.moviebooking.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class CreateBookingRequest {

    @NotNull
    private Long holdId;

    @NotNull
    private PaymentMethod paymentMethod;

    private String discountCode;

    public CreateBookingRequest() {}

    public Long getHoldId() { return holdId; }
    public void setHoldId(Long holdId) { this.holdId = holdId; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
}
