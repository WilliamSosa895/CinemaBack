package com.Esteban.cinema.DTO.Mapping;

import com.Esteban.cinema.DTO.Request.ShowtimeRequest;
import com.Esteban.cinema.DTO.Response.ShowtimeDetails;
import com.Esteban.cinema.DTO.Response.ShowtimesResponse;
import com.Esteban.cinema.Model.Movies;
import com.Esteban.cinema.Model.Rooms;
import com.Esteban.cinema.Model.Showtimes;
import com.Esteban.cinema.Repository.MovieRepository;
import com.Esteban.cinema.Repository.RoomRepository;
import com.Esteban.cinema.Repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ShowtimeMapping {
    @Autowired
    private SeatMapping seatMapping;
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private MovieRepository movieRepository;

    public Showtimes toEntity(ShowtimeRequest request) {
        Showtimes showtime = new Showtimes();
        Optional<Rooms> room = roomRepository.findById(request.getRoom());
        room.ifPresent(showtime::setRoom);
        Optional<Movies> movie = movieRepository.findById(request.getMovie());
        movie.ifPresent(showtime::setMovie);
        showtime.setShowtime(request.getShowtime());
        showtime.setLanguage(request.getLanguage());
        showtime.setActive(true);
        return showtime;
    }

    public ShowtimesResponse toResponse(Showtimes showtime) {
        ShowtimesResponse response = new ShowtimesResponse();
        response.setId(showtime.getIdShowtime());
        response.setRoomName(showtime.getRoom().getName());
        response.setShowtime(showtime.getShowtime());
        response.setMovieTitle(showtime.getMovie().getTitle());
        response.setMovieLanguage(showtime.getLanguage());
        response.setRoomType(showtime.getRoom().getType());
        return response;
    }

    public ShowtimeDetails viewShowtimeDetails(Long idShowtime) {
        Optional<Showtimes> showtime = showtimeRepository.findById(idShowtime);

        if(showtime.isPresent()){
            ShowtimeDetails view = new ShowtimeDetails();
            view.setMovieTitle(showtime.get().getMovie().getTitle());
            view.setRoomName(showtime.get().getRoom().getName());
            view.setShowtime(showtime.get().getShowtime());
            view.setPriceSeats(showtime.get().getMovie().getPrice());
            view.setSeats(seatMapping.buildTicketMatrix(showtime.get().getIdShowtime()));

            return view;
        }else{
            throw new RuntimeException("Showtime with id " + idShowtime + " not found.");
        }
    }
}