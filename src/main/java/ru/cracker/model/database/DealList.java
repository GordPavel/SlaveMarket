package ru.cracker.model.database;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class DealList implements Serializable {
  @XmlElementWrapper
  @XmlAnyElement(lax=true)
  List<Deal> deals = new ArrayList<Deal>();

  public List<Deal> getDeals() {
    return this.deals;
  }

  public void addDeal(Deal deal) {
    deals.add(deal);
  }

}
