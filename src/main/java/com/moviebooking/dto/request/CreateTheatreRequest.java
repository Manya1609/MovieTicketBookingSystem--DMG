package com.moviebooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTheatreRequest {

    @NotBlank
    private String name;

    private String address;

    @NotNull
    private Long cityId;

    public CreateTheatreRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }
}
