package com.shtven.cinema.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shtven.cinema.DTO.Response.MovieResponse;
import com.shtven.cinema.Model.Movies;
import com.shtven.cinema.Service.MovieService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/movies")
public class MoviesController {
    @Autowired
    MovieService movieService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> saveMovie(
            @Valid @RequestPart("movie") Movies request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        movieService.saveMovie(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping ("/{idMovie}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie (@PathVariable Long idMovie){
        movieService.deleteMovie(idMovie);
        return ResponseEntity.noContent().build();
    }

    @PutMapping ("/{idMovie}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateMovie (@PathVariable Long idMovie, @Valid @RequestBody Movies request){
        movieService.updateMovie(request, idMovie);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieResponse>> getAllMovies (){
        List<MovieResponse> movies = movieService.getAllActiveMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{searchTitle}")
    public ResponseEntity<List<MovieResponse>> getMoviesByTitle(@PathVariable String searchTitle){
        return ResponseEntity.ok(movieService.findByMovieTitle(searchTitle));
    }

}