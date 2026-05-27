package com.Esteban.cinema.Configuration;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.Esteban.cinema.Model.Movies;
import com.Esteban.cinema.Model.Rooms;
import com.Esteban.cinema.Model.Showtimes;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Repository.MovieRepository;
import com.Esteban.cinema.Repository.RoomRepository;
import com.Esteban.cinema.Repository.ShowtimeRepository;
import com.Esteban.cinema.Repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final ShowtimeRepository showtimeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            MovieRepository movieRepository,
            RoomRepository roomRepository,
            ShowtimeRepository showtimeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.showtimeRepository = showtimeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Users admin = userRepository.findByEmail("admin.cineplus@example.com").orElseGet(() -> {
            Users user = new Users();
            user.setFullName("Administrador Prueba");
            user.setEmail("admin.cineplus@example.com");
            user.setPassword(passwordEncoder.encode("Admin1234!"));
            user.setRole("ADMIN");
            return userRepository.save(user);
        });

        Movies movie = movieRepository.findByTitle("Pelicula de prueba").stream().findFirst().orElseGet(() -> {
            Movies newMovie = new Movies();
            newMovie.setTitle("Pelicula de prueba");
            newMovie.setDuration(LocalTime.of(1, 40));
            newMovie.setGenre("Accion");
            newMovie.setPrice(12.5);
            newMovie.setActive(true);
            newMovie.setPosterPath("https://res.cloudinary.com/dtipujfyp/image/upload/v1778432856/Logo_pmbwud.jpg");
            return movieRepository.save(newMovie);
        });

        Rooms room = roomRepository.findAll().stream().filter(r -> "Sala 1".equals(r.getName())).findFirst().orElseGet(() -> {
            Rooms newRoom = new Rooms();
            newRoom.setName("Sala 1");
            newRoom.setType("2D");
            newRoom.setStatus(true);
            return roomRepository.save(newRoom);
        });

        LocalDateTime seedDateTime = LocalDateTime.now().plusDays(1)
                .withHour(18)
                .withMinute(30)
                .withSecond(0)
                .withNano(0);

        boolean activeShowtimeExists = showtimeRepository.findByMovieIdMovie(movie.getIdMovie()).stream()
                .anyMatch(showtime -> showtime.getRoom() != null
                        && room.getIdRoom().equals(showtime.getRoom().getIdRoom())
                        && Boolean.TRUE.equals(showtime.getActive()));

        if (!activeShowtimeExists) {
            Showtimes showtime = new Showtimes();
            showtime.setMovie(movie);
            showtime.setRoom(room);
            showtime.setShowtime(Timestamp.valueOf(seedDateTime));
            showtime.setLanguage("Español");
            showtime.setActive(true);
            showtimeRepository.save(showtime);
        }

        if (admin.getIdUser() == null) {
            throw new IllegalStateException("Admin seed could not be created");
        }
    }
}