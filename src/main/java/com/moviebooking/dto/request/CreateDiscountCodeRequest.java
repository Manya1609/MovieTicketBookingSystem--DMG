package com.moviebooking.dto.request;

import com.moviebooking.enums.DiscountType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateDiscountCodeRequest {

    @NotBlank
    private String code;

    @NotNull
    private DiscountType discountType;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal discountValue;

    @NotNull
    private LocalDate validFrom;

    @NotNull
    private LocalDate validTill;

    @Min(1)
    private int maxUsage;

    public CreateDiscountCodeRequest() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTill() { return validTill; }
    public void setValidTill(LocalDate validTill) { this.validTill = validTill; }
    public int getMaxUsage() { return maxUsage; }
    public void setMaxUsage(int maxUsage) { this.maxUsage = maxUsage; }
}
