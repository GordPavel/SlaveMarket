package ru.cracker.model.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DealList implements Serializable {
  List<Deal> deals = new ArrayList<Deal>();

  public List<Deal> getDeals() {
    return this.deals;
  }

  public void addDeal(Deal deal) {
    deals.add(deal);
  }

}
