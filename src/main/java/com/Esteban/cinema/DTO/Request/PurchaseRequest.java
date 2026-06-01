package com.Esteban.cinema.DTO.Request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PurchaseRequest {

    @NotNull(message = "Showtime ID is required")
    public Long idShowtime;
    
    @NotNull(message = "Seats are required")
    private List<List<Integer>> seats;


    public Long getIdShowtime() {
        return idShowtime;
    }

    public void setIdShowtime(Long idShowtime) {
        this.idShowtime = idShowtime;
    }

    public List<List<Integer>> getSeats() {
        return seats;
    }

    public void setSeats(List<List<Integer>> seats) {
        this.seats = seats;
    }


}