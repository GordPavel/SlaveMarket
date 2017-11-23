package ru.cracker.exceptions;

public class MerchandiseAlreadyBought extends IllegalArgumentException {
    public MerchandiseAlreadyBought(int id) {
        super("Merchandise with id " + id + " already bought");
    }
}
