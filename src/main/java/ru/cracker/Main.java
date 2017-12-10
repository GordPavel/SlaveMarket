package ru.cracker;

import ru.cracker.controller.Controller;
import ru.cracker.controller.SlaveController;
import ru.cracker.model.Model;
import ru.cracker.model.SlaveMarketModel;

/**
 * Start of program.
 */
public class Main {

  /**
   * Function to start the program.
   *
   * @param args some arguments.
   */
  public static void main(String[] args) {
    Model model = new SlaveMarketModel();
    Controller controller = new SlaveController(model);
    controller.start();
  }
}
