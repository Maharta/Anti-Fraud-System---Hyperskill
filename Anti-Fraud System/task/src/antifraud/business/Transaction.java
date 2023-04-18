package antifraud.business;

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

    public long getAmount() {
        return amount;
    }

    public Transaction() {
    }
}
