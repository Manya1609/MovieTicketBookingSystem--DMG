package com.moviebooking.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    private List<Theatre> theatres = new ArrayList<>();

    public City() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Theatre> getTheatres() { return theatres; }
    public void setTheatres(List<Theatre> theatres) { this.theatres = theatres; }
}
