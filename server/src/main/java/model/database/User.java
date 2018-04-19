package model.database;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.UUID;

@XmlRootElement(name = "user")
public class User implements Serializable {
    private String username;
    private String password;
    private String token = "";

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean verifyUser(String username, String password) {
        return this.password.equals(password)
                && (username.hashCode() + password.hashCode()) == (this.username.hashCode() + this.password.hashCode());
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String generateToken() {
        token = UUID.randomUUID() + ":" + System.currentTimeMillis();
        return token;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
