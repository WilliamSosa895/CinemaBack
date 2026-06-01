package com.Esteban.cinema.Service;


import com.Esteban.cinema.Model.Cards;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Repository.CardRepository;
import com.Esteban.cinema.Repository.UserRepository;
import com.Esteban.cinema.exceptions.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveCard(Cards card, Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        Optional<Users> user = userRepository.findById(userId);
        if (user.isPresent()) {
            entityManager.createNativeQuery(
                    "INSERT INTO cards (cardnumber, cardowner, expirationdate, card_number, card_owner, expiration_date, id_user) " +
                    "VALUES (:cardnumber, :cardowner, :expirationdate, :cardNumber, :cardOwner, :expirationDate, :idUser)"
            )
            .setParameter("cardnumber", card.getCardNumber())
            .setParameter("cardowner", card.getCardOwner())
            .setParameter("expirationdate", card.getExpirationDate())
            .setParameter("cardNumber", card.getCardNumber())
            .setParameter("cardOwner", card.getCardOwner())
            .setParameter("expirationDate", card.getExpirationDate())
            .setParameter("idUser", userId)
            .executeUpdate();
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User with ID " + userId + " not found."
            );
        }
    }

    public void deleteCard(Long cardId, Long userId) {
        Optional<Cards> optionalCard = cardRepository.findByIdCardAndIdUser(cardId, userId);
        if(optionalCard.isPresent()) {
            cardRepository.delete(optionalCard.get());
        }else{
            throw new BusinessException("Card with ID " + cardId + " not found for User ID " + userId + ".");
        }
    }

    public List<Cards> findAllByUser(Long userId) {
        return cardRepository.findAllByUser(userId);
    }
}