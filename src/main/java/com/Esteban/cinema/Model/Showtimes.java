package com.Esteban.cinema.Model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "showtimes")
public class Showtimes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_showtime")
    private Long idShowtime;

    @ManyToOne
    @JoinColumn(name = "id_room", nullable = false)
    private Rooms room;

    @ManyToOne
    @JoinColumn(name = "id_movie", nullable = false)
    private Movies movie;

    @Column(name = "showtime", nullable = false)
    private Timestamp showtime;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public Long getIdShowtime() {
        return idShowtime;
    }

    public void setIdShowtime(Long idShowtime) {
        this.idShowtime = idShowtime;
    }

    public Rooms getRoom() {
        return room;
    }

    public void setRoom(Rooms room) {
        this.room = room;
    }

    public Movies getMovie() {
        return movie;
    }

    public void setMovie(Movies movie) {
        this.movie = movie;
    }

    public Timestamp getShowtime() {
        return showtime;
    }

    public void setShowtime(Timestamp showtime) {
        this.showtime = showtime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

