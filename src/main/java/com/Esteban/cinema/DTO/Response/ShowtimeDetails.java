package com.Esteban.cinema.DTO.Response;

import java.sql.Timestamp;

public class ShowtimeDetails {

    private String movieTitle;
    private String roomName;
    private Timestamp showtime;
    private double priceSeats;
    private int[][] seats;

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Timestamp getShowtime() {
        return showtime;
    }

    public void setShowtime(Timestamp showtime) {
        this.showtime = showtime;
    }

    public double getPriceSeats() {
        return priceSeats;
    }

    public void setPriceSeats(double priceSeats) {
        this.priceSeats = priceSeats;
    }

    public int[][] getSeats() {
        return seats;
    }

    public void setSeats(int[][] seats) {
        this.seats = seats;
    }

}
