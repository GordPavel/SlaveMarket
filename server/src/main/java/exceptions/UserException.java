package exceptions;

public class UserException extends IllegalArgumentException {
  public UserException(String message) {
    super(message);
  }
}
