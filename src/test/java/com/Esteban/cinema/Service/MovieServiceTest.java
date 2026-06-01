package com.Esteban.cinema.Service;

import com.Esteban.cinema.DTO.Mapping.MovieMapping;
import com.Esteban.cinema.DTO.Response.MovieResponse;
import com.Esteban.cinema.Model.Movies;
import com.Esteban.cinema.Repository.MovieRepository;
import com.Esteban.cinema.exceptions.BusinessException;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieMapping movieMapping;

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private MovieService movieService;

    @Test
    void saveMovie_whenFileIsNull_savesActiveMovieOnce() throws Exception {
        Movies request = new Movies();
        request.setTitle("Interstellar");
        request.setActive(false);

        Movies saved = new Movies();
        saved.setIdMovie(1L);
        saved.setTitle("Interstellar");
        saved.setActive(true);

        when(movieRepository.save(request)).thenReturn(saved);

        movieService.saveMovie(request, null);

        assertEquals(true, request.getActive());
        verify(movieRepository).save(request);
        verify(movieRepository, never()).save(saved);
    }

    @Test
    void saveMovie_whenFileIsPresent_uploadsPosterAndSavesMovieTwice() throws Exception {
        Movies request = new Movies();
        request.setTitle("Dune");

        Movies saved = new Movies();
        saved.setIdMovie(7L);
        saved.setTitle("Dune");
        saved.setActive(true);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "poster.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        when(movieRepository.save(request)).thenReturn(saved);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of("secure_url", "https://cdn.test/poster.jpg"));

        movieService.saveMovie(request, file);

        assertEquals(true, request.getActive());
        assertEquals("https://cdn.test/poster.jpg", saved.getPosterPath());
        verify(movieRepository).save(request);
        verify(movieRepository).save(saved);
    }

    @Test
    void saveMovie_whenCloudinaryUploadFails_throwsBusinessException() throws Exception {
        Movies request = new Movies();
        request.setTitle("Avatar");

        Movies saved = new Movies();
        saved.setIdMovie(9L);
        saved.setTitle("Avatar");
        saved.setActive(true);

        MultipartFile file = new MockMultipartFile(
                "file",
                "poster.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        when(movieRepository.save(request)).thenReturn(saved);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new IOException("cloud error"));

        BusinessException exception = assertThrows(BusinessException.class, () -> movieService.saveMovie(request, file));

        assertEquals("Error uploading poster to Cloudinary: cloud error", exception.getMessage());
    }

    @Test
    void deleteMovie_whenExists_deactivatesAndSavesMovie() {
        Movies movie = new Movies();
        movie.setIdMovie(11L);
        movie.setTitle("Matrix");
        movie.setActive(true);

        when(movieRepository.findById(11L)).thenReturn(Optional.of(movie));

        movieService.deleteMovie(11L);

        assertEquals(false, movie.getActive());
        verify(movieRepository).save(movie);
    }

    @Test
    void deleteMovie_whenMovieDoesNotExist_throwsBusinessException() {
        when(movieRepository.findById(404L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> movieService.deleteMovie(404L));

        assertEquals("Movie with id 404 not found.", exception.getMessage());
    }

    @Test
    void updateMovie_whenExists_updatesFieldsAndSavesMovie() {
        Movies existing = new Movies();
        existing.setIdMovie(15L);
        existing.setTitle("Old title");
        existing.setGenre("Drama");
        existing.setDuration(LocalTime.of(1, 30));
        existing.setPrice(50.0);

        Movies request = new Movies();
        request.setTitle("New title");
        request.setGenre("Sci-Fi");
        request.setDuration(LocalTime.of(2, 15));
        request.setPrice(120.0);

        when(movieRepository.findById(15L)).thenReturn(Optional.of(existing));

        movieService.updateMovie(request, 15L);

        assertEquals("New title", existing.getTitle());
        assertEquals("Sci-Fi", existing.getGenre());
        assertEquals(LocalTime.of(2, 15), existing.getDuration());
        assertEquals(120.0, existing.getPrice());
        verify(movieRepository).save(existing);
    }

    @Test
    void updateMovie_whenMovieDoesNotExist_throwsBusinessException() {
        Movies request = new Movies();
        when(movieRepository.findById(88L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> movieService.updateMovie(request, 88L));

        assertEquals("Movie with id 88 not found.", exception.getMessage());
    }

    @Test
    void getAllActiveMovies_returnsMappedResponses() {
        Movies movie = new Movies();
        movie.setIdMovie(1L);

        MovieResponse response = new MovieResponse();
        response.setId(1L);

        when(movieRepository.findAllIfActivate()).thenReturn(List.of(movie));
        when(movieMapping.movieView(movie)).thenReturn(response);

        List<MovieResponse> result = movieService.getAllActiveMovies();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getAllMoviesForBillboard_returnsMappedResponses() {
        Movies movie = new Movies();
        movie.setIdMovie(2L);

        MovieResponse response = new MovieResponse();
        response.setId(2L);

        when(movieRepository.findAllWithActiveShowtimes()).thenReturn(List.of(movie));
        when(movieMapping.movieView(movie)).thenReturn(response);

        List<MovieResponse> result = movieService.getAllMoviesForBillboard();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void getPosterPath_returnsPosterPathOrNull() {
        Movies movie = new Movies();
        movie.setPosterPath("https://cdn.test/poster.jpg");

        when(movieRepository.findById(33L)).thenReturn(Optional.of(movie));
        when(movieRepository.findById(44L)).thenReturn(Optional.empty());

        assertEquals("https://cdn.test/poster.jpg", movieService.getPosterPath(33L));
        assertNull(movieService.getPosterPath(44L));
    }

    @Test
    void findByMovieTitle_returnsMappedResponses() {
        Movies movie = new Movies();
        movie.setTitle("Interstellar");

        MovieResponse response = new MovieResponse();
        response.setTitle("Interstellar");

        when(movieRepository.findByTitle("Inter")).thenReturn(List.of(movie));
        when(movieMapping.movieView(movie)).thenReturn(response);

        List<MovieResponse> result = movieService.findByMovieTitle("Inter");

        assertEquals(1, result.size());
        assertEquals("Interstellar", result.get(0).getTitle());
    }
}
