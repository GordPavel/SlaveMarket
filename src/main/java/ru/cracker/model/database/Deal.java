package ru.cracker.model.database;

import ru.cracker.model.merchandises.Merchandise;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Deal implements Serializable {
  private final DealState state;
  LocalDateTime time;
  private User user;
  private Merchandise merchandise;
  private int price;
  private int id;

  /**
   * Constructor to create {@link Deal} object.
   *
   * @param user        user who performed action.
   * @param merchandise merchandise that was bought or sold.
   * @param price       merchandise's price.
   * @param state       current deal state. watch {@link DealState}
   *                    for more info.
   * @param id          deal's id.
   */
  public Deal(User user, Merchandise merchandise, int price, DealState state, int id) {
    this.user = user;
    this.merchandise = merchandise;
    this.price = price;
    this.state = state;
    this.id = id;
    time = LocalDateTime.now();
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Merchandise getMerchandise() {
    return merchandise;
  }

  public void setMerchandise(Merchandise merchandise) {
    this.merchandise = merchandise;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public DealState getState() {
    return state;
  }

  @Override
  public String toString() {
    return time +
            ", merchandise=" + merchandise +
            ", price=" + price +
            ", state=" + state.getState() +
            ", dealId=" + id;
  }
}
