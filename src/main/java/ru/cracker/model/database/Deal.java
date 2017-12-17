package ru.cracker.model.database;

import ru.cracker.model.merchandises.Merchandise;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Deal implements Serializable, Comparable<Deal> {
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
    this.time = LocalDateTime.now();
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Merchandise getMerchandise() {
    return this.merchandise;
  }

  public void setMerchandise(Merchandise merchandise) {
    this.merchandise = merchandise;
  }

  public int getPrice() {
    return this.price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public DealState getState() {
    return this.state;
  }

  @Override
  public String toString() {
    return this.time.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"))
            + ", merchandise=" + this.merchandise
            + ", price=" + this.price
            + ", state=" + this.state
            + this.user.getUsername()
            + ", dealId=" + this.id;
  }

  @Override
  public int compareTo(Deal o) {
    return Integer.compare(this.id, o.id);
  }
}
