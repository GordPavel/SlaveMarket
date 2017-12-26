package ru.cracker;

import ru.cracker.execute.StartServer;

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
    StartServer server = new StartServer();
    Thread thread = new Thread(() -> server.main(new String[]{}));
    thread.start();
  }
}
