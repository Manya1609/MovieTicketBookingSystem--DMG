package com.moviebooking.dto.response;

import com.moviebooking.entity.RefundPolicy;

public class RefundPolicyResponse {

    private Long id;
    private int minHoursBeforeShow;
    private int refundPercentage;

    public RefundPolicyResponse() {}

    public static RefundPolicyResponse from(RefundPolicy policy) {
        RefundPolicyResponse r = new RefundPolicyResponse();
        r.id = policy.getId();
        r.minHoursBeforeShow = policy.getMinHoursBeforeShow();
        r.refundPercentage = policy.getRefundPercentage();
        return r;
    }

    public Long getId() { return id; }
    public int getMinHoursBeforeShow() { return minHoursBeforeShow; }
    public int getRefundPercentage() { return refundPercentage; }
}
