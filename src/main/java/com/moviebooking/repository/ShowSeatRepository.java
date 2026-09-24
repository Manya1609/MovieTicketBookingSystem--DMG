package com.moviebooking.repository;

import com.moviebooking.entity.ShowSeat;
import com.moviebooking.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    List<ShowSeat> findByShowId(Long showId);

    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.seat.id IN :seatIds")
    List<ShowSeat> findByShowIdAndSeatIdIn(@Param("showId") Long showId, @Param("seatIds") List<Long> seatIds);

    Optional<ShowSeat> findByShowIdAndSeatId(Long showId, Long seatId);

    List<ShowSeat> findByShowIdAndStatus(Long showId, SeatStatus status);
}
