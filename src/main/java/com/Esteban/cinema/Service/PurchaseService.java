package com.Esteban.cinema.Service;

import com.google.zxing.WriterException;
import com.Esteban.cinema.DTO.Mapping.PurchaseMapping;
import com.Esteban.cinema.DTO.Mapping.SeatMapping;
import com.Esteban.cinema.DTO.Request.PurchaseRequest;
import com.Esteban.cinema.DTO.Response.PurchaseResponse;
import com.Esteban.cinema.DTO.Response.SeatsResponse;
import com.Esteban.cinema.Model.Movies;
import com.Esteban.cinema.Model.Purchases;
import com.Esteban.cinema.Model.Showtimes;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Repository.MovieRepository;
import com.Esteban.cinema.Repository.PurchaseRepository;
import com.Esteban.cinema.Repository.ShowtimeRepository;
import com.Esteban.cinema.Repository.UserRepository;
import com.Esteban.cinema.exceptions.BusinessException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private SeatMapping seatMapping;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PurchaseMapping purchaseMapping;
    @Autowired
    private EmailService emailService;

    public void savePurchase(PurchaseRequest purchaseRequest, Long userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<Showtimes> showtime = showtimeRepository.findById(purchaseRequest.getIdShowtime());
            if (showtime.isPresent()) {
                Purchases purchases = new Purchases();
                purchases.setDate(Timestamp.from(java.time.Instant.now()));
                purchases.setUser(user.get());
                purchases.setShowtime(showtime.get());
                Optional<Movies> movie = movieRepository.findById(showtime.get().getMovie().getIdMovie());
                if (movie.isPresent()) {
                    double totalAmount = movie.get().getPrice() * purchaseRequest.getSeats().size();
                    purchases.setTotalAmount(totalAmount);

                    purchases = purchaseRepository.save(purchases);

                    seatMapping.saveSeats(purchaseRequest.getSeats(), showtime.get(), purchases);

                    String movies = movie.get().getTitle();
                    String rooms = showtime.get().getRoom().getName();
                    String seats = seatMapping.buildSeatsResponse(purchases.getSeats())
                            .stream()
                            .map(SeatsResponse::getSeatNumber)
                            .collect(Collectors.joining(", "));
                    String folio = "CP-" + purchases.getIdPurchase();
                    String total = String.format("$%.2f", purchases.getTotalAmount());

                    try{
                        emailService.loadHtmlTemplatePurchaseAndSend(movies, rooms, seats, folio, total, user.get().getEmail());
                    }catch(MessagingException | IOException | WriterException ex){
                        ex.printStackTrace();
                    }
                }

            }
        }
    }

    public PurchaseResponse getPurchaseByIdForUser(Long idPurchase, Long userId) {
        Purchases purchase = purchaseRepository.findById(idPurchase)
                .orElseThrow(() -> new BusinessException("Purchase not found"));

        if (!purchase.getUser().getIdUser().equals(userId)) {
            throw new BusinessException("You cannot access this purchase");
        }

        return purchaseMapping.purchaseView(purchase);
    }

    public List<PurchaseResponse> getAllPurchasesByUser(Long userId) {
        return purchaseRepository.getAllPurchasesByUser(userId)
                .stream().map(purchaseMapping::purchaseView).toList();
    }

}