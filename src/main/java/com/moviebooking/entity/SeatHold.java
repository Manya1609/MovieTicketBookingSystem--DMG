package com.moviebooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SeatHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "seat_hold_show_seats",
        joinColumns = @JoinColumn(name = "seat_hold_id"),
        inverseJoinColumns = @JoinColumn(name = "show_seat_id")
    )
    private List<ShowSeat> showSeats = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean expired = false;

    public SeatHold() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }
    public Show getShow() { return show; }
    public void setShow(Show show) { this.show = show; }
    public List<ShowSeat> getShowSeats() { return showSeats; }
    public void setShowSeats(List<ShowSeat> showSeats) { this.showSeats = showSeats; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }
}
