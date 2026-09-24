package com.moviebooking.entity;

import com.moviebooking.enums.DiscountType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTill;

    private int maxUsage;

    private int currentUsage = 0;

    @Version
    private Long version;

    public DiscountCode() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public int getCurrentUsage() { return currentUsage; }
    public void setCurrentUsage(int currentUsage) { this.currentUsage = currentUsage; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
