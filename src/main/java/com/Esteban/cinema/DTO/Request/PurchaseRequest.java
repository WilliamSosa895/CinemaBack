package com.Esteban.cinema.DTO.Request;

import java.util.List;

public class PurchaseRequest {

    public Long idShowtime;
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