package model.database;


@FunctionalInterface
interface QueryComparator<A, B> {

  Boolean apply(A a, B b);
}
