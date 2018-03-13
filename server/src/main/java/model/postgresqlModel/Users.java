package model.postgresqlModel;

import javax.persistence.*;
import java.io.Serializable;

@NamedStoredProcedureQuery(name = "login", procedureName = "login", resultClasses = {String.class}, parameters = {
        @StoredProcedureParameter(name = "usernameVar", mode = ParameterMode.IN, type = String.class),
        @StoredProcedureParameter(name = "passwordVar", mode = ParameterMode.IN, type = String.class),
        @StoredProcedureParameter(name = "token", mode = ParameterMode.OUT, type = String.class)
})
@Entity
@Table(name = "users")
public class Users implements Serializable {

    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "token")
    private String token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void login() {
    }
}
