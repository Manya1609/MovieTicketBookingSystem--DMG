package com.moviebooking.repository;

import com.moviebooking.entity.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {

    @Query("SELECT h FROM SeatHold h WHERE h.expiresAt < :now AND h.expired = false")
    List<SeatHold> findExpiredHolds(LocalDateTime now);

    List<SeatHold> findByCustomerIdAndExpiredFalse(Long customerId);
}
