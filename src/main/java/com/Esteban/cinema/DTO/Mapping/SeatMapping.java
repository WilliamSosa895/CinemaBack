package com.Esteban.cinema.DTO.Mapping;

import com.Esteban.cinema.DTO.Response.SeatsResponse;
import com.Esteban.cinema.Model.Purchases;
import com.Esteban.cinema.Model.Seats;
import com.Esteban.cinema.Model.Showtimes;
import com.Esteban.cinema.Repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SeatMapping {

    @Autowired
    private SeatRepository seatRepository;

    public void saveSeats(List<List<Integer>> seats, Showtimes showtimes, Purchases purchase) {
        List<Seats> seatsList = new ArrayList<>();
        for (List<Integer> seat : seats) {
            int row = seat.get(0);
            int col = seat.get(1);
            Seats seatsEntity = new Seats();
            seatsEntity.setRowNumber(row);
            seatsEntity.setColumnNumber(col);
            seatsEntity.setStatus(1);
            seatsEntity.setShowtime(showtimes);
            seatsEntity.setPurchase(purchase);
            seatRepository.save(seatsEntity);
            seatsList.add(seatsEntity);
        }
        purchase.setSeats(seatsList);
    }

    public int[][] buildTicketMatrix(Long showtimeId) {
        List<Seats> seats = seatRepository.findByShowtime(showtimeId);
        int[][] matrix = new int[10][8];

        for (Seats seat : seats) {
            int row = seat.getRowNumber();
            int col = seat.getColumnNumber();
            matrix[row][col] = seat.getStatus();
        }

        return matrix;
    }

    public List<SeatsResponse> buildSeatsResponse(List<Seats> seats) {
        List<SeatsResponse> seatsResponse = new ArrayList<>();
        for (Seats seat : seats) {
            int rowIndex = seat.getRowNumber();
            int colIndex = seat.getColumnNumber();

            char rowLetter = (char) ('A' + rowIndex);
            int col = colIndex + 1;

            SeatsResponse response = new SeatsResponse();
            response.setSeatNumber(rowLetter + String.valueOf(col));
            seatsResponse.add(response);
        }

        return seatsResponse;
    }
}
