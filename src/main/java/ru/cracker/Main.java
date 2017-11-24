package ru.cracker;

import ru.cracker.controller.Controller;
import ru.cracker.controller.SlaveController;
import ru.cracker.model.Model;
import ru.cracker.model.SlaveMarketModel;

/**
 *
 */
public class Main {

    public static void main(String[] args) {
        Model model = new SlaveMarketModel();
        Controller controller = new SlaveController(model);
    }
}
