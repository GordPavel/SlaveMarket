package model.database;

public interface DealExp<A, B> {
  A apply(B deal);
}
