package com.moviebooking.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateCityRequest {

    @NotBlank
    private String name;

    public CreateCityRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
