package antifraud.business.model;

import antifraud.business.enums.RolesEnum;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String name;

    @Column
    private String description;

    public Role(RolesEnum rolesEnum, String description) {
        this.name = rolesEnum.toString();
        this.description = description;
    }

    public Role() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
