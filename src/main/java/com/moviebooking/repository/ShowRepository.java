package com.moviebooking.repository;

import com.moviebooking.entity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {

    @Query("SELECT s FROM Show s JOIN s.screen sc JOIN sc.theatre t WHERE t.city.id = :cityId")
    Page<Show> findByCityId(@Param("cityId") Long cityId, Pageable pageable);

    List<Show> findByScreenId(Long screenId);

    @Query("SELECT COUNT(s) > 0 FROM Show s WHERE s.screen.id = :screenId AND s.startTime < :endTime AND s.endTime > :startTime")
    boolean existsOverlappingShow(@Param("screenId") Long screenId,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);
}
