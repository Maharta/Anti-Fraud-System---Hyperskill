package antifraud.business.model.entity;


import antifraud.presentation.validation.ValidCardNumber;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "card")
public class Card {

    @Id
    private long id;

    @Column
    @ValidCardNumber
    private String number;

    @Column
    private int maxAllowed;

    @Column
    private int maxManual;

    public Card() {
    }

    public Card(String number) {
        this.number = number;
        maxAllowed = 200;
        maxManual = 1500;
    }

    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }

    public int getMaxManual() {
        return maxManual;
    }
}
