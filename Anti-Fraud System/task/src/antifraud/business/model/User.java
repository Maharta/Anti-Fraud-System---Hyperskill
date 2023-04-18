package antifraud.business.model;

import antifraud.business.enums.RolesEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @ManyToMany(cascade = {
            CascadeType.MERGE,
            CascadeType.PERSIST
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLES")
    private List<Role> rolesAndAuthorities; // should be prefixed with ROLE_

    public User() {
    }

    public User(String name, String username, String password) {
        this.username = username.toLowerCase();
        this.name = name;
        this.password = password;
        this.rolesAndAuthorities = new ArrayList<>();
        rolesAndAuthorities.add(new Role(RolesEnum.ROLE_USER, RolesEnum.getRoleUserDescription()));
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRolesAndAuthorities() {
        return rolesAndAuthorities;
    }

    public void setRolesAndAuthorities(List<Role> rolesAndAuthorities) {
        this.rolesAndAuthorities = rolesAndAuthorities;
    }
}
