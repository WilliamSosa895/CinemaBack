package com.Esteban.cinema.Service;

import com.Esteban.cinema.DTO.Mapping.PurchaseMapping;
import com.Esteban.cinema.DTO.Mapping.SeatMapping;
import com.Esteban.cinema.DTO.Request.PurchaseRequest;
import com.Esteban.cinema.DTO.Response.PurchaseResponse;
import com.Esteban.cinema.DTO.Response.SeatsResponse;
import com.Esteban.cinema.Model.Movies;
import com.Esteban.cinema.Model.Purchases;
import com.Esteban.cinema.Model.Rooms;
import com.Esteban.cinema.Model.Showtimes;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Repository.MovieRepository;
import com.Esteban.cinema.Repository.PurchaseRepository;
import com.Esteban.cinema.Repository.ShowtimeRepository;
import com.Esteban.cinema.Repository.UserRepository;
import com.Esteban.cinema.exceptions.BusinessException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private SeatMapping seatMapping;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShowtimeRepository showtimeRepository;
    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private PurchaseMapping purchaseMapping;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void savePurchase_whenValidData_sendsConfirmationEmail() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setIdShowtime(10L);
        request.setSeats(List.of(List.of(0, 0), List.of(0, 1)));

        Users user = new Users();
        user.setIdUser(5L);
        user.setEmail("buyer@test.com");

        Movies movie = new Movies();
        movie.setIdMovie(20L);
        movie.setTitle("Interstellar");
        movie.setPrice(120.0);

        Rooms room = new Rooms();
        room.setName("Sala 1");

        Showtimes showtime = new Showtimes();
        showtime.setIdShowtime(10L);
        showtime.setMovie(movie);
        showtime.setRoom(room);

        Purchases persisted = new Purchases();
        persisted.setIdPurchase(99L);
        persisted.setUser(user);
        persisted.setShowtime(showtime);
        persisted.setTotalAmount(240.0);

        SeatsResponse seatA1 = new SeatsResponse();
        seatA1.setSeatNumber("A1");
        SeatsResponse seatA2 = new SeatsResponse();
        seatA2.setSeatNumber("A2");

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(showtimeRepository.findById(10L)).thenReturn(Optional.of(showtime));
        when(movieRepository.findById(20L)).thenReturn(Optional.of(movie));
        when(purchaseRepository.save(any(Purchases.class))).thenReturn(persisted);
        when(seatMapping.buildSeatsResponse(any())).thenReturn(List.of(seatA1, seatA2));

        purchaseService.savePurchase(request, 5L);

        verify(emailService).loadHtmlTemplatePurchaseAndSend(
                eq("Interstellar"),
                eq("Sala 1"),
                eq("A1, A2"),
                eq("CP-99"),
                eq("$240.00"),
                eq("buyer@test.com")
        );
    }

    @Test
    void savePurchase_whenEmailFails_doesNotThrow() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setIdShowtime(10L);
        request.setSeats(List.of(List.of(0, 0)));

        Users user = new Users();
        user.setIdUser(5L);
        user.setEmail("buyer@test.com");

        Movies movie = new Movies();
        movie.setIdMovie(20L);
        movie.setTitle("Interstellar");
        movie.setPrice(120.0);

        Rooms room = new Rooms();
        room.setName("Sala 1");

        Showtimes showtime = new Showtimes();
        showtime.setIdShowtime(10L);
        showtime.setMovie(movie);
        showtime.setRoom(room);

        Purchases persisted = new Purchases();
        persisted.setIdPurchase(100L);
        persisted.setUser(user);
        persisted.setShowtime(showtime);
        persisted.setTotalAmount(120.0);

        SeatsResponse seatA1 = new SeatsResponse();
        seatA1.setSeatNumber("A1");

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(showtimeRepository.findById(10L)).thenReturn(Optional.of(showtime));
        when(movieRepository.findById(20L)).thenReturn(Optional.of(movie));
        when(purchaseRepository.save(any(Purchases.class))).thenReturn(persisted);
        when(seatMapping.buildSeatsResponse(any())).thenReturn(List.of(seatA1));

        doThrow(new MessagingException("mail error")).when(emailService)
                .loadHtmlTemplatePurchaseAndSend(any(), any(), any(), any(), any(), any());

        purchaseService.savePurchase(request, 5L);

        verify(emailService).loadHtmlTemplatePurchaseAndSend(
                eq("Interstellar"),
                eq("Sala 1"),
                eq("A1"),
                eq("CP-100"),
                eq("$120.00"),
                eq("buyer@test.com")
        );
    }

    @Test
    void savePurchase_whenUserNotFound_doesNotSaveOrSendEmail() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setIdShowtime(10L);
        request.setSeats(List.of(List.of(0, 0)));

        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        purchaseService.savePurchase(request, 5L);

        verify(showtimeRepository, never()).findById(any());
        verify(purchaseRepository, never()).save(any());
        verify(emailService, never()).loadHtmlTemplatePurchaseAndSend(any(), any(), any(), any(), any(), any());
    }

    @Test
    void getPurchaseByIdForUser_whenOwnerMatches_returnsMappedPurchase() {
        Users user = new Users();
        user.setIdUser(5L);

        Purchases purchase = new Purchases();
        purchase.setIdPurchase(200L);
        purchase.setUser(user);

        PurchaseResponse response = new PurchaseResponse();

        when(purchaseRepository.findById(200L)).thenReturn(Optional.of(purchase));
        when(purchaseMapping.purchaseView(purchase)).thenReturn(response);

        PurchaseResponse result = purchaseService.getPurchaseByIdForUser(200L, 5L);

        assertEquals(response, result);
        verify(purchaseMapping).purchaseView(purchase);
    }

    @Test
    void getPurchaseByIdForUser_whenPurchaseNotFound_throwsBusinessException() {
        when(purchaseRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> purchaseService.getPurchaseByIdForUser(999L, 5L)
        );

        assertEquals("Purchase not found", exception.getMessage());
    }

    @Test
    void getPurchaseByIdForUser_whenUserDoesNotOwnPurchase_throwsBusinessException() {
        Users owner = new Users();
        owner.setIdUser(8L);

        Purchases purchase = new Purchases();
        purchase.setIdPurchase(201L);
        purchase.setUser(owner);

        when(purchaseRepository.findById(201L)).thenReturn(Optional.of(purchase));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> purchaseService.getPurchaseByIdForUser(201L, 5L)
        );

        assertEquals("You cannot access this purchase", exception.getMessage());
    }

    @Test
    void getAllPurchasesByUser_returnsMappedPurchases() {
        Purchases purchaseA = new Purchases();
        purchaseA.setIdPurchase(1L);
        Purchases purchaseB = new Purchases();
        purchaseB.setIdPurchase(2L);

        PurchaseResponse responseA = new PurchaseResponse();
        PurchaseResponse responseB = new PurchaseResponse();

        when(purchaseRepository.getAllPurchasesByUser(5L)).thenReturn(List.of(purchaseA, purchaseB));
        when(purchaseMapping.purchaseView(purchaseA)).thenReturn(responseA);
        when(purchaseMapping.purchaseView(purchaseB)).thenReturn(responseB);

        List<PurchaseResponse> result = purchaseService.getAllPurchasesByUser(5L);

        assertEquals(2, result.size());
        assertEquals(responseA, result.get(0));
        assertEquals(responseB, result.get(1));
    }
}
