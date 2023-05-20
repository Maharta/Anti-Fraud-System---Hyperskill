package antifraud.business.model.entity;


import antifraud.presentation.validation.ValidCardNumber;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "card")
public class Card implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }

    public void setMaxAllowed(int maxAllowed) {
        this.maxAllowed = maxAllowed;
    }

    public int getMaxManual() {
        return maxManual;
    }

    public void setMaxManual(int maxManual) {
        this.maxManual = maxManual;
    }
}
