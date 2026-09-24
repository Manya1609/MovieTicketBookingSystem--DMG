package com.moviebooking.service;

import com.moviebooking.dto.response.ShowResponse;
import com.moviebooking.dto.response.ShowSeatResponse;
import com.moviebooking.entity.Show;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.repository.ShowRepository;
import com.moviebooking.repository.ShowSeatRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;

    public ShowService(ShowRepository showRepository, ShowSeatRepository showSeatRepository) {
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
    }

    public Page<ShowResponse> getShowsByCity(Long cityId, Pageable pageable) {
        return showRepository.findByCityId(cityId, pageable).map(ShowResponse::from);
    }

    public ShowResponse getShowById(Long id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show", id));
        return ShowResponse.from(show);
    }

    public List<ShowSeatResponse> getShowSeats(Long showId) {
        if (!showRepository.existsById(showId)) {
            throw new ResourceNotFoundException("Show", showId);
        }
        return showSeatRepository.findByShowId(showId).stream().map(ShowSeatResponse::from).toList();
    }
}
