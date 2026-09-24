package com.moviebooking.dto.response;

import com.moviebooking.entity.DiscountCode;
import com.moviebooking.enums.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DiscountCodeResponse {

    private Long id;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDate validFrom;
    private LocalDate validTill;
    private int maxUsage;
    private int currentUsage;

    public DiscountCodeResponse() {}

    public static DiscountCodeResponse from(DiscountCode dc) {
        DiscountCodeResponse r = new DiscountCodeResponse();
        r.id = dc.getId();
        r.code = dc.getCode();
        r.discountType = dc.getDiscountType();
        r.discountValue = dc.getDiscountValue();
        r.validFrom = dc.getValidFrom();
        r.validTill = dc.getValidTill();
        r.maxUsage = dc.getMaxUsage();
        r.currentUsage = dc.getCurrentUsage();
        return r;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public DiscountType getDiscountType() { return discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidTill() { return validTill; }
    public int getMaxUsage() { return maxUsage; }
    public int getCurrentUsage() { return currentUsage; }
}
