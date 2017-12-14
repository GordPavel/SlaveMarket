package ru.cracker.model.database;

public interface DealExp<A, B> {
  A apply(B deal);
}
