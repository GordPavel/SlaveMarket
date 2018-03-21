package model.database;

public enum DealState {
  BOUGHT("bought"), FOR_SALE("on sale"), REMOVED("removed"), SOLD("sold");

  private final String state;

  DealState(String state1) {
    state = state1;
  }

  @Override
  public String toString() {
    return state;
  }
}
