package antifraud.business.model.entity;


import javax.persistence.*;

@Entity
@Table(name = "suspicious_ip")
public class IP {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String ip;

    public IP() {
    }

    public IP(String ip) {
        this.ip = ip;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
