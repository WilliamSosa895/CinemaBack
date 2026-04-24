package com.Esteban.cinema.Service;


import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Esteban.cinema.DTO.Response.MovieResponse;
import com.Esteban.cinema.Model.Movies;

@Service
public class MovieService {
    public void saveMovie(Movies request, MultipartFile file) throws IOException {
    }

    public void deleteMovie(Long idMovie) {
    }

    public void updateMovie(Movies request, Long idMovie) {
    }

    public List<MovieResponse> getAllActiveMovies() {
        return List.of();
    }


    public String getPosterPath(Long idMovie) {
        return null;
    }

    public List<MovieResponse> findByMovieTitle(String search) {
        return List.of();
    }
}
