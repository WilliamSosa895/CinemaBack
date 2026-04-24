package com.Esteban.cinema.Service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.Esteban.cinema.DTO.Request.ShowtimeRequest;
import com.Esteban.cinema.DTO.Response.ShowtimeDetails;
import com.Esteban.cinema.DTO.Response.ShowtimesResponse;

@Service
public class ShowtimeService {

    public void createShowtime(ShowtimeRequest request) {
    }

    public void deleteShowtime(Long idShowtime) {
    }

    public void updateShowtime(Long idShowtime, ShowtimeRequest request) {
    }

    public List<ShowtimesResponse> getShowtimesFromMovie(Long idMovie) {
        return List.of();
    }

    public ShowtimeDetails getShowtimeDetails(Long idShowtime) {
        return null;
    }

    public List<ShowtimesResponse> getAllShowtimes() {
        return List.of();
    }

    public List<ShowtimesResponse> getShowtimesByMovieName(String titleSearch) {
        return List.of();
    }

}
