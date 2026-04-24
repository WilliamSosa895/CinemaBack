package com.Esteban.cinema.Service;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.Esteban.cinema.DTO.Mapping.MovieMapping;
import com.Esteban.cinema.DTO.Response.MovieResponse;
import com.Esteban.cinema.Model.Movies;
import com.Esteban.cinema.Repository.MovieRepository;
import com.Esteban.cinema.exceptions.BusinessException;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieMapping movieMapping;
    @Autowired
    private Cloudinary cloudinary;

    public void saveMovie(Movies request, MultipartFile file) throws IOException {

        request.setActive(true);
        Movies savedMovie = movieRepository.save(request);

        if (file != null && !file.isEmpty()) {
            String posterUrl = uploadPosterToCloudinary(savedMovie.getIdMovie(), file);
            savedMovie.setPosterPath(posterUrl);
            movieRepository.save(savedMovie);
        }
    }

    public void deleteMovie(Long idMovie) {
        Optional<Movies> movie = movieRepository.findById(idMovie);
        if(movie.isPresent()){
            Movies movies = movie.get();
            movies.setActive(false);
            movieRepository.save(movies);
        }else{
            throw new BusinessException("Movie with id " + idMovie + " not found.");
        }
    }

    public void updateMovie(Movies request, Long idMovie) {
        Optional<Movies> movie = movieRepository.findById(idMovie);
        if(movie.isPresent()){
            Movies movies = movie.get();
            movies.setTitle(request.getTitle());
            movies.setDuration(request.getDuration());
            movies.setPrice(request.getPrice());
            movies.setGenre(request.getGenre());
            movieRepository.save(movies);
        }else{
            throw new BusinessException("Movie with id " + idMovie + " not found.");
        }
    }

    public List<MovieResponse> getAllActiveMovies() {
        return movieRepository.findAllIfActivate().stream().map(movieMapping::movieView).toList();
    }

    private String uploadPosterToCloudinary(Long movieId, MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "cinema/posters",
                            "public_id", "movie_" + movieId,
                            "overwrite", true,
                            "resource_type", "image"
                    )
            );

            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new BusinessException("Error uploading poster to Cloudinary: " + e.getMessage());
        }
    }


    public String getPosterPath(Long idMovie) {
        Optional<Movies> movie = movieRepository.findById(idMovie);
        return movie.map(Movies::getPosterPath).orElse(null);
    }

    public List<MovieResponse> findByMovieTitle(String search) {
        return movieRepository.findByTitle(search).stream().map(movieMapping::movieView).toList();
    }
}
