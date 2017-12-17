package ru.cracker.model.database;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
  private String username;
  private String password;
  private String token = "";

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

  public boolean verifyPassword(String password) {
    return this.password.equals(password);
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
}
