package com.moviebooking.dto.response;

import com.moviebooking.entity.City;

public class CityResponse {

    private Long id;
    private String name;

    public CityResponse() {}

    public static CityResponse from(City city) {
        CityResponse r = new CityResponse();
        r.id = city.getId();
        r.name = city.getName();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
