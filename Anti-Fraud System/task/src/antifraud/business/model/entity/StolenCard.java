package antifraud.business.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "stolen_card")
public class StolenCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String number;

    public StolenCard() {
    }

    public StolenCard(String number) {
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }
}
