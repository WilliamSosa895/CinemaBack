package com.shtven.cinema.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shtven.cinema.DTO.Mapping.ShowtimeMapping;
import com.shtven.cinema.DTO.Request.ShowtimeRequest;
import com.shtven.cinema.DTO.Response.ShowtimeDetails;
import com.shtven.cinema.DTO.Response.ShowtimesResponse;
import com.shtven.cinema.Model.Rooms;
import com.shtven.cinema.Model.Showtimes;
import com.shtven.cinema.Repository.RoomRepository;
import com.shtven.cinema.Repository.ShowtimeRepository;
import com.shtven.cinema.exceptions.BusinessException;

@Service
public class ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private ShowtimeMapping showtimeMapping;
    @Autowired
    private RoomRepository roomRepository;

    public void createShowtime(ShowtimeRequest request) {
        boolean exists = showtimeRepository.existsByRoom_IdRoomAndShowtimeAndActiveTrue(request.getRoom(), request.getShowtime());

        if (exists) {
            throw new BusinessException("Showtime already exists for the given room and time.");
        }

        Showtimes showtime = showtimeMapping.toEntity(request);
        showtimeRepository.save(showtime);
    }

    public void deleteShowtime(Long idShowtime) {
        showtimeRepository.deleteById(idShowtime);
    }

    public void updateShowtime(Long idShowtime, ShowtimeRequest request) {
        Optional<Showtimes> showtimes = showtimeRepository.findById(idShowtime);
        if (showtimes.isPresent()) {
            boolean exists = showtimeRepository.existsByRoom_IdRoomAndShowtimeAndActiveTrue(request.getRoom(), request.getShowtime());

            if (exists) {
                throw new BusinessException("Showtime already exists for the given room and time.");
            }

            Showtimes showtime = showtimeMapping.toEntity(request);
            showtimes.get().setMovie(showtime.getMovie());
            showtimes.get().setRoom(showtime.getRoom());
            showtimes.get().setShowtime(showtime.getShowtime());
            showtimes.get().setLanguage(showtime.getLanguage());
            showtimes.get().setActive(showtime.getActive());

            showtimeRepository.save(showtimes.get());

        }else{
            throw new BusinessException("Showtime with id " + idShowtime + " not found.");
        }
    }

    public List<ShowtimesResponse> getShowtimesFromMovie(Long idMovie) {
        return showtimeRepository.findByMovieIdMovie(idMovie).stream().map(showtimeMapping::toResponse).toList();
    }

    public ShowtimeDetails getShowtimeDetails(Long idShowtime) {
        return showtimeMapping.viewShowtimeDetails(idShowtime);
    }

    public List<ShowtimesResponse> getAllShowtimes() {
        return showtimeRepository.findAll().stream().map(showtimeMapping::toResponse).toList();
    }

    public List<ShowtimesResponse> getShowtimesByMovieName(String titleSearch) {
        return showtimeRepository.findByMovieTitle(titleSearch).stream()
                .map(showtimeMapping::toResponse)
                .toList();
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    protected void validateStatusShowtime() {
        List<Showtimes> allShowtime = showtimeRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (Showtimes showtime : allShowtime) {
            LocalDateTime start = showtime.getShowtime().toLocalDateTime();
            LocalDateTime end = calculateEnd(showtime);

            if (now.isAfter(end) && showtime.getActive()) {
                showtime.setActive(false);
                showtimeRepository.save(showtime);
            }
        }

        Set<Long> occupiedRooms = new HashSet<>();

        for (Showtimes showtime : allShowtime) {
            LocalDateTime start = showtime.getShowtime().toLocalDateTime();
            LocalDateTime end = calculateEnd(showtime);

            boolean inProgress =
                    (now.isEqual(start) || now.isAfter(start)) && now.isBefore(end);

            if (inProgress) {
                occupiedRooms.add(showtime.getRoom().getIdRoom());
            }
        }

        List<Rooms> allRooms = roomRepository.findAll();
        for (Rooms room : allRooms) {
            boolean shouldBeActive = occupiedRooms.contains(room.getIdRoom());

            if (room.getStatus() != shouldBeActive) {
                room.setStatus(shouldBeActive);
                roomRepository.save(room);
            }
        }

    }

    private LocalDateTime calculateEnd(Showtimes showtime) {
        LocalDateTime start = showtime.getShowtime().toLocalDateTime();
        long totalMinutes = showtime.getMovie().getDuration().getHour() * 60L
                + showtime.getMovie().getDuration().getMinute();
        return start.plusMinutes(totalMinutes);
    }

}