package com.moviebooking.repository;

import com.moviebooking.entity.RefundPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface RefundPolicyRepository extends JpaRepository<RefundPolicy, Long> {

    @Query("SELECT rp FROM RefundPolicy rp WHERE rp.minHoursBeforeShow <= :hours ORDER BY rp.minHoursBeforeShow DESC LIMIT 1")
    Optional<RefundPolicy> findBestMatchingPolicy(@Param("hours") long hours);
}
