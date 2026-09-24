package com.moviebooking.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL)
    private List<Screen> screens = new ArrayList<>();

    public Theatre() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public List<Screen> getScreens() { return screens; }
    public void setScreens(List<Screen> screens) { this.screens = screens; }
}
