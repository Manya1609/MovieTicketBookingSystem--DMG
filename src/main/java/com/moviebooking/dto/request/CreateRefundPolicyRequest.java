package com.moviebooking.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class CreateRefundPolicyRequest {

    @Min(0)
    private int minHoursBeforeShow;

    @Min(0)
    @Max(100)
    private int refundPercentage;

    public CreateRefundPolicyRequest() {}

    public int getMinHoursBeforeShow() { return minHoursBeforeShow; }
    public void setMinHoursBeforeShow(int minHoursBeforeShow) { this.minHoursBeforeShow = minHoursBeforeShow; }
    public int getRefundPercentage() { return refundPercentage; }
    public void setRefundPercentage(int refundPercentage) { this.refundPercentage = refundPercentage; }
}
