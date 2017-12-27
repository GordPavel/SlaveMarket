package ru.cracker.model.database;

public enum DealState {
  Bought(" bought by "), FOR_SALE(" on sale by "), REMOVED(" removed by ");

  private final String state;

  DealState(String state1) {
    state = state1;
  }

  @Override
  public String toString() {
    return state;
  }
}
