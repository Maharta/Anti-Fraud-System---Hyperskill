package antifraud.business.model.entity;

import antifraud.business.model.enums.RoleEnum;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true)
    private String name;

    @Column
    private String description;

    public Role(RoleEnum rolesEnum) {
        this.name = rolesEnum.toString();
        this.description = rolesEnum.getDescription();
    }

    public Role() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
