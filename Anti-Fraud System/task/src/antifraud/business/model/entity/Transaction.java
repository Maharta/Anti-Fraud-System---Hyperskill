package antifraud.business.model.entity;

import antifraud.business.model.enums.Region;
import antifraud.business.model.enums.TransactionStatus;
import antifraud.presentation.DTO.transaction.TransactionDTO;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Transaction {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private int amount;

    @Column
    private String ip;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Column
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    private TransactionStatus feedback;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "card_number", referencedColumnName = "number")
    private Card card;

    public Transaction() {
    }

    public Transaction(int amount, String ip, Region region, LocalDateTime dateTime, TransactionStatus status, Card card) {
        this.amount = amount;
        this.ip = ip;
        this.region = region;
        this.dateTime = dateTime;
        this.status = status;
        this.card = card;
        feedback = null;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", ip='" + ip + '\'' +
                ", number='" + card.getNumber() + '\'' +
                ", region=" + region +
                ", dateTime=" + dateTime +
                ", status=" + status +
                '}';
    }

    public TransactionDTO toDTO() {
        return new TransactionDTO(id, amount, ip, card.getNumber(),
                region, dateTime, status, feedback);
    }

    public long getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public String getIp() {
        return ip;
    }

    public Card getCard() {
        return card;
    }

    public Region getRegion() {
        return region;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public TransactionStatus getFeedback() {
        return feedback;
    }

    public void setFeedback(TransactionStatus feedback) {
        this.feedback = feedback;
    }

    public interface RegionAndIP {
        Region getRegion();

        String getIp();
    }
}
