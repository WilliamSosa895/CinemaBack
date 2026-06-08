package com.Esteban.cinema.Service;

import com.Esteban.cinema.DTO.Response.StatsResponse;
import com.Esteban.cinema.Repository.MovieRepository;
import com.Esteban.cinema.Repository.RoomRepository;
import com.Esteban.cinema.Repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private MovieRepository movieRepository;

    public StatsResponse getStats(){
        StatsResponse response = new StatsResponse();
        response.setAllShowtimes(showtimeRepository.count());
        response.setAllActiveRooms(roomRepository.countByStatusTrue());
        response.setAllMovies(movieRepository.count());
        return response;
    }
}