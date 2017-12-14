package ru.cracker.exceptions;

public class InvalidToken extends IllegalArgumentException {
  public InvalidToken() {
    super("Invalid token");
  }
}
