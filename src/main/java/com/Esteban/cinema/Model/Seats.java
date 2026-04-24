package com.Esteban.cinema.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seat")
    private Long idSeat;

    @Column(name = "row_number", nullable = false)
    private int rowNumber;

    @Column(name = "column_number", nullable = false)
    private int columnNumber;

    @Column(name = "status", nullable = false)
    private int status;

    @ManyToOne
    @JoinColumn(name = "id_showtime", nullable = false)
    private Showtimes showtime;

    @ManyToOne
    @JoinColumn(name = "id_purchase", nullable = false)
    private Purchases purchase;

    public Long getIdSeat() {
        return idSeat;
    }

    public void setIdSeat(Long idSeat) {
        this.idSeat = idSeat;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Showtimes getShowtime() {
        return showtime;
    }

    public void setShowtime(Showtimes showtime) {
        this.showtime = showtime;
    }

    public Purchases getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchases purchase) {
        this.purchase = purchase;
    }
}
