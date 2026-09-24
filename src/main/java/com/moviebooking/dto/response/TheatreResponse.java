package com.moviebooking.dto.response;

import com.moviebooking.entity.Theatre;

public class TheatreResponse {

    private Long id;
    private String name;
    private String address;
    private Long cityId;
    private String cityName;

    public TheatreResponse() {}

    public static TheatreResponse from(Theatre theatre) {
        TheatreResponse r = new TheatreResponse();
        r.id = theatre.getId();
        r.name = theatre.getName();
        r.address = theatre.getAddress();
        r.cityId = theatre.getCity().getId();
        r.cityName = theatre.getCity().getName();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public Long getCityId() { return cityId; }
    public String getCityName() { return cityName; }
}
