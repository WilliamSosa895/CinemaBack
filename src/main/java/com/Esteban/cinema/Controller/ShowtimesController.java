package com.Esteban.cinema.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Esteban.cinema.DTO.Request.ShowtimeRequest;
import com.Esteban.cinema.DTO.Response.ShowtimeDetails;
import com.Esteban.cinema.DTO.Response.ShowtimesResponse;
import com.Esteban.cinema.Service.ShowtimeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/showtimes")
public class ShowtimesController {

    @Autowired
    private ShowtimeService showtimeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createShowtime (@Valid @RequestBody ShowtimeRequest request){
        showtimeService.createShowtime(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping ("/{idShowtime}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateShowtime (@PathVariable Long idShowtime, @Valid @RequestBody ShowtimeRequest request){
        showtimeService.updateShowtime(idShowtime, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping ("/{idShowtime}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShowtime (@PathVariable Long idShowtime){
        showtimeService.deleteShowtime(idShowtime);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/movie/{idMovie}")
    public ResponseEntity<List<ShowtimesResponse>> getShowtimesFromMovie(@PathVariable Long idMovie) {
        return ResponseEntity.ok(showtimeService.getShowtimesFromMovie(idMovie));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowtimeDetails> getShowtimeDetails(@PathVariable Long id ) {
        return ResponseEntity.ok(showtimeService.getShowtimeDetails(id));
    }

    @GetMapping("/search/{titleSearch}")
    public ResponseEntity<List<ShowtimesResponse>> getShowtimesByMovieName(@PathVariable String titleSearch) {
        return ResponseEntity.ok(showtimeService.getShowtimesByMovieName(titleSearch));
    }
    
    @GetMapping()
    public ResponseEntity<List<ShowtimesResponse>> getAllShowtimes() {
        return ResponseEntity.ok(showtimeService.getAllShowtimes());
    }

}
