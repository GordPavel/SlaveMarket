package ru.cracker.model.database;

import java.io.Serializable;

public enum DealState implements Serializable {
  Bought(" bought by "), FOR_SALE(" sold by ");


  private String state;

  DealState(String state1) {
    state = state1;
  }

  public String getState() {
    return state;
  }
}
