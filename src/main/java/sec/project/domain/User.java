package sec.project.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "Logins")
public class User extends AbstractPersistable<Long> {

    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "cc")
    private String cc;
    @Id
    private Long id;

    public User() {
        super();
    }

    public User(String username, String password, String cc) {
        this();
        this.username = username;
        this.password = password;
        this.cc = cc;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "(" + id + ") " + username + ":" + password + " creditcard: " + cc;
    }

}
