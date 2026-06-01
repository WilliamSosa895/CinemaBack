package com.Esteban.cinema.Service;

import com.Esteban.cinema.DTO.Mapping.ShowtimeMapping;
import com.Esteban.cinema.DTO.Request.ShowtimeRequest;
import com.Esteban.cinema.DTO.Response.ShowtimeDetails;
import com.Esteban.cinema.DTO.Response.ShowtimesResponse;
import com.Esteban.cinema.Model.Movies;
import com.Esteban.cinema.Model.Rooms;
import com.Esteban.cinema.Model.Showtimes;
import com.Esteban.cinema.Repository.RoomRepository;
import com.Esteban.cinema.Repository.ShowtimeRepository;
import com.Esteban.cinema.exceptions.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private ShowtimeMapping showtimeMapping;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private ShowtimeService showtimeService;

    @Test
    void createShowtime_whenDuplicateExists_throwsBusinessException() {
        ShowtimeRequest request = new ShowtimeRequest();
        request.setRoom(1L);
        request.setShowtime(Timestamp.valueOf(LocalDateTime.of(2026, 6, 1, 20, 0)));

        when(showtimeRepository.existsByRoom_IdRoomAndShowtimeAndActiveTrue(1L, request.getShowtime())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> showtimeService.createShowtime(request));

        assertEquals("Showtime already exists for the given room and time.", exception.getMessage());
        verify(showtimeRepository, never()).save(any(Showtimes.class));
    }

    @Test
    void createShowtime_whenDataIsValid_savesMappedEntity() {
        ShowtimeRequest request = new ShowtimeRequest();
        request.setRoom(1L);
        request.setShowtime(Timestamp.valueOf(LocalDateTime.of(2026, 6, 1, 20, 0)));

        Showtimes entity = new Showtimes();

        when(showtimeRepository.existsByRoom_IdRoomAndShowtimeAndActiveTrue(1L, request.getShowtime())).thenReturn(false);
        when(showtimeMapping.toEntity(request)).thenReturn(entity);

        showtimeService.createShowtime(request);

        verify(showtimeRepository).save(entity);
    }

    @Test
    void updateShowtime_whenShowtimeDoesNotExist_throwsBusinessException() {
        ShowtimeRequest request = new ShowtimeRequest();
        when(showtimeRepository.findById(77L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> showtimeService.updateShowtime(77L, request));

        assertEquals("Showtime with id 77 not found.", exception.getMessage());
        verify(showtimeRepository, never()).save(any(Showtimes.class));
    }

    @Test
    void updateShowtime_whenDuplicateExists_throwsBusinessException() {
        Showtimes persisted = new Showtimes();

        ShowtimeRequest request = new ShowtimeRequest();
        request.setRoom(1L);
        request.setShowtime(Timestamp.valueOf(LocalDateTime.of(2026, 6, 1, 20, 0)));

        when(showtimeRepository.findById(50L)).thenReturn(Optional.of(persisted));
        when(showtimeRepository.existsByRoom_IdRoomAndShowtimeAndActiveTrue(1L, request.getShowtime())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> showtimeService.updateShowtime(50L, request));

        assertEquals("Showtime already exists for the given room and time.", exception.getMessage());
        verify(showtimeMapping, never()).toEntity(any(ShowtimeRequest.class));
        verify(showtimeRepository, never()).save(any(Showtimes.class));
    }

    @Test
    void updateShowtime_whenDataIsValid_updatesAndSavesEntity() {
        Rooms oldRoom = new Rooms();
        oldRoom.setIdRoom(1L);
        oldRoom.setName("Sala A");

        Movies oldMovie = new Movies();
        oldMovie.setIdMovie(10L);

        Showtimes persisted = new Showtimes();
        persisted.setIdShowtime(99L);
        persisted.setRoom(oldRoom);
        persisted.setMovie(oldMovie);
        persisted.setShowtime(Timestamp.valueOf(LocalDateTime.of(2026, 6, 1, 18, 0)));
        persisted.setLanguage("ES");
        persisted.setActive(true);

        Rooms newRoom = new Rooms();
        newRoom.setIdRoom(2L);
        newRoom.setName("Sala B");

        Movies newMovie = new Movies();
        newMovie.setIdMovie(20L);

        Showtimes mapped = new Showtimes();
        mapped.setRoom(newRoom);
        mapped.setMovie(newMovie);
        mapped.setShowtime(Timestamp.valueOf(LocalDateTime.of(2026, 6, 1, 20, 0)));
        mapped.setLanguage("EN");
        mapped.setActive(false);

        ShowtimeRequest request = new ShowtimeRequest();
        request.setRoom(2L);
        request.setShowtime(mapped.getShowtime());

        when(showtimeRepository.findById(99L)).thenReturn(Optional.of(persisted));
        when(showtimeRepository.existsByRoom_IdRoomAndShowtimeAndActiveTrue(2L, mapped.getShowtime())).thenReturn(false);
        when(showtimeMapping.toEntity(request)).thenReturn(mapped);

        showtimeService.updateShowtime(99L, request);

        assertEquals(newRoom, persisted.getRoom());
        assertEquals(newMovie, persisted.getMovie());
        assertEquals(mapped.getShowtime(), persisted.getShowtime());
        assertEquals("EN", persisted.getLanguage());
        assertEquals(false, persisted.getActive());
        verify(showtimeRepository).save(persisted);
    }

    @Test
    void deleteShowtime_callsRepositoryDeleteById() {
        showtimeService.deleteShowtime(15L);

        verify(showtimeRepository).deleteById(15L);
    }

    @Test
    void getShowtimesFromMovie_returnsMappedResponses() {
        Showtimes first = new Showtimes();
        Showtimes second = new Showtimes();

        ShowtimesResponse firstResponse = new ShowtimesResponse();
        firstResponse.setId(1L);

        ShowtimesResponse secondResponse = new ShowtimesResponse();
        secondResponse.setId(2L);

        when(showtimeRepository.findByMovieIdMovieAndActiveTrue(8L)).thenReturn(List.of(first, second));
        when(showtimeMapping.toResponse(first)).thenReturn(firstResponse);
        when(showtimeMapping.toResponse(second)).thenReturn(secondResponse);

        List<ShowtimesResponse> result = showtimeService.getShowtimesFromMovie(8L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getShowtimeDetails_returnsMappingResult() {
        ShowtimeDetails details = new ShowtimeDetails();
        details.setMovieTitle("Dune");

        when(showtimeMapping.viewShowtimeDetails(30L)).thenReturn(details);

        ShowtimeDetails result = showtimeService.getShowtimeDetails(30L);

        assertEquals("Dune", result.getMovieTitle());
    }

    @Test
    void getAllShowtimes_returnsMappedResponses() {
        Showtimes showtime = new Showtimes();

        ShowtimesResponse response = new ShowtimesResponse();
        response.setId(45L);

        when(showtimeRepository.findAll()).thenReturn(List.of(showtime));
        when(showtimeMapping.toResponse(showtime)).thenReturn(response);

        List<ShowtimesResponse> result = showtimeService.getAllShowtimes();

        assertEquals(1, result.size());
        assertEquals(45L, result.get(0).getId());
    }

    @Test
    void getShowtimesByMovieName_returnsMappedResponses() {
        Showtimes showtime = new Showtimes();

        ShowtimesResponse response = new ShowtimesResponse();
        response.setMovieTitle("Interstellar");

        when(showtimeRepository.findByMovieTitle("Inter")).thenReturn(List.of(showtime));
        when(showtimeMapping.toResponse(showtime)).thenReturn(response);

        List<ShowtimesResponse> result = showtimeService.getShowtimesByMovieName("Inter");

        assertEquals(1, result.size());
        assertEquals("Interstellar", result.get(0).getMovieTitle());
    }

    @Test
    void validateStatusShowtime_updatesExpiredShowtimesAndRoomStatus() {
        LocalDateTime now = LocalDateTime.now();

        Rooms occupiedRoom = new Rooms();
        occupiedRoom.setIdRoom(1L);
        occupiedRoom.setStatus(false);

        Rooms freeRoom = new Rooms();
        freeRoom.setIdRoom(2L);
        freeRoom.setStatus(true);

        Movies shortMovie = new Movies();
        shortMovie.setDuration(LocalTime.of(1, 0));

        Showtimes expired = new Showtimes();
        expired.setRoom(occupiedRoom);
        expired.setMovie(shortMovie);
        expired.setShowtime(Timestamp.valueOf(now.minusHours(3)));
        expired.setActive(true);

        Showtimes inProgress = new Showtimes();
        inProgress.setRoom(occupiedRoom);
        inProgress.setMovie(shortMovie);
        inProgress.setShowtime(Timestamp.valueOf(now.minusMinutes(10)));
        inProgress.setActive(true);

        Showtimes finishedInFreeRoom = new Showtimes();
        finishedInFreeRoom.setRoom(freeRoom);
        finishedInFreeRoom.setMovie(shortMovie);
        finishedInFreeRoom.setShowtime(Timestamp.valueOf(now.minusHours(4)));
        finishedInFreeRoom.setActive(false);

        when(showtimeRepository.findAll()).thenReturn(List.of(expired, inProgress, finishedInFreeRoom));
        when(roomRepository.findAll()).thenReturn(List.of(occupiedRoom, freeRoom));

        showtimeService.validateStatusShowtime();

        assertEquals(false, expired.getActive());
        assertEquals(true, occupiedRoom.getStatus());
        assertEquals(false, freeRoom.getStatus());

        verify(showtimeRepository).save(expired);
        verify(roomRepository).save(occupiedRoom);
        verify(roomRepository).save(freeRoom);
        verify(showtimeRepository, never()).save(eq(inProgress));
    }
}
