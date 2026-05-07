package com.Esteban.cinema.Service;


import com.Esteban.cinema.Model.Cards;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Repository.CardRepository;
import com.Esteban.cinema.Repository.UserRepository;
import com.Esteban.cinema.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserRepository userRepository;

    public void saveCard(Cards card, Long userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isPresent()) {
            card.setUser(user.get());
            cardRepository.save(card);
        } else {
            throw new BusinessException("User with ID " + userId + " not found.");
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