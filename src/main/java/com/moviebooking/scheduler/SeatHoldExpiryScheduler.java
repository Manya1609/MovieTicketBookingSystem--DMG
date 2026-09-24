package com.moviebooking.scheduler;

import com.moviebooking.entity.SeatHold;
import com.moviebooking.entity.ShowSeat;
import com.moviebooking.enums.SeatStatus;
import com.moviebooking.repository.SeatHoldRepository;
import com.moviebooking.repository.ShowSeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeatHoldExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(SeatHoldExpiryScheduler.class);

    private final SeatHoldRepository seatHoldRepository;
    private final ShowSeatRepository showSeatRepository;

    public SeatHoldExpiryScheduler(SeatHoldRepository seatHoldRepository, ShowSeatRepository showSeatRepository) {
        this.seatHoldRepository = seatHoldRepository;
        this.showSeatRepository = showSeatRepository;
    }

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void expireStaleHolds() {
        List<SeatHold> expiredHolds = seatHoldRepository.findExpiredHolds(LocalDateTime.now());
        if (expiredHolds.isEmpty()) return;

        for (SeatHold hold : expiredHolds) {
            for (ShowSeat showSeat : hold.getShowSeats()) {
                if (showSeat.getStatus() == SeatStatus.HELD) {
                    showSeat.setStatus(SeatStatus.AVAILABLE);
                    showSeatRepository.save(showSeat);
                }
            }
            hold.setExpired(true);
            seatHoldRepository.save(hold);
            log.debug("Expired hold {} for show {}", hold.getId(), hold.getShow().getId());
        }

        log.info("Expired {} stale seat holds", expiredHolds.size());
    }
}
