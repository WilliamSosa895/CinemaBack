package com.Esteban.cinema.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Cards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_card")
    private Long idCard;

    @Column(name = "cardnumber", nullable = false)
    private String cardNumberLegacy;

    @Column(name = "cardowner", nullable = false)
    private String cardOwnerLegacy;

    @Column(name = "expirationdate", nullable = false)
    private String expirationDateLegacy;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "card_owner", nullable = false)
    private String cardOwner;

    @Column(name = "expiration_date", nullable = false)
    private String expirationDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Users user;

    public Long getIdCard() {
        return idCard;
    }

    public void setIdCard(Long idCard) {
        this.idCard = idCard;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardOwner() {
        return cardOwner;
    }

    public void setCardOwner(String cardOwner) {
        this.cardOwner = cardOwner;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    @PrePersist
    @PreUpdate
    private void syncLegacyColumns() {
        this.cardNumberLegacy = this.cardNumber;
        this.cardOwnerLegacy = this.cardOwner;
        this.expirationDateLegacy = this.expirationDate;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}