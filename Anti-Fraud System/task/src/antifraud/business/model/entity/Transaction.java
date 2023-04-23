package antifraud.business.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Transaction {
    @Id
    @Column
    private long id;

    @Column
    private long amount;

    public Transaction() {
    }

    public long getId() {
        return id;
    }

    public long getAmount() {
        return amount;
    }
}
