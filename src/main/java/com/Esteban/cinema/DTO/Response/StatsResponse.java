package com.Esteban.cinema.DTO.Response;

public class StatsResponse {

    private Long allShowtimes;
    private Long allActiveRooms;
    private Long allMovies;


    public Long getAllShowtimes() {
        return allShowtimes;
    }

    public void setAllShowtimes(Long allShowtimes) {
        this.allShowtimes = allShowtimes;
    }

    public Long getAllActiveRooms() {
        return allActiveRooms;
    }

    public void setAllActiveRooms(Long allActiveRooms) {
        this.allActiveRooms = allActiveRooms;
    }

    public Long getAllMovies() {
        return allMovies;
    }

    public void setAllMovies(Long allMovies) {
        this.allMovies = allMovies;
    }

}