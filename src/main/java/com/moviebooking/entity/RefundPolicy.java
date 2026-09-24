package com.moviebooking.entity;

import jakarta.persistence.*;

@Entity
public class RefundPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int minHoursBeforeShow;

    @Column(nullable = false)
    private int refundPercentage;

    public RefundPolicy() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getMinHoursBeforeShow() { return minHoursBeforeShow; }
    public void setMinHoursBeforeShow(int minHoursBeforeShow) { this.minHoursBeforeShow = minHoursBeforeShow; }
    public int getRefundPercentage() { return refundPercentage; }
    public void setRefundPercentage(int refundPercentage) { this.refundPercentage = refundPercentage; }
}
