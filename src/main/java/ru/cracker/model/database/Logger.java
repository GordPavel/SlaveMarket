package ru.cracker.model.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for log all actions and errors.
 */
public class Logger {

  /**
   * mainLogFile -  File string to log all of operations. userDir - directory to store users logs.
   * datePattern - pattern to write date in logs. writers to write logs in files.
   */
  private final String mainPath = "logs/main/";
  private final String mainLogFile = mainPath + "Slavemarket.log";
  private final String userDir = "logs/users/";
  private final String datePattern = "YYYY-MM-dd HH:mm:ss";
  PrintWriter writer;
  PrintWriter userWriter;
  PrintWriter errorWriter;

  /**
   * Default constructor.
   */
  public Logger() {

    try {
      Files.createDirectories(Paths.get("logs/main/"));
      Files.createDirectories(Paths.get("logs/users/"));
    } catch (IOException e) {
      System.exit(-1);
    }
    try {
      errorWriter = new PrintWriter(new FileOutputStream(new File(mainPath + "errors.log"), true),
          true);
    } catch (FileNotFoundException e) {
      try {
        boolean file = new File(mainLogFile).createNewFile();
        if (file) {
          errorWriter = new PrintWriter(
              new FileOutputStream(new File(mainPath + "errors.log"), true), true);
        }
      } catch (IOException ignored) {
        System.exit(-1);
      }
    }
    try {
      writer = new PrintWriter(new FileOutputStream(new File(mainLogFile), true), true);
    } catch (FileNotFoundException e) {
      try {
        boolean file = new File(mainLogFile).createNewFile();
        if (file) {
          writer = new PrintWriter(new FileOutputStream(new File(mainLogFile), true), true);
        }
      } catch (IOException ignored) {
        System.exit(-1);
      }
    }
  }

  /**
   * Method to log user actions.
   *
   * @param user Username who has performed an action
   * @param action action to log performed by user
   * @param merchInfo information about changed merchandise.
   */
  public void log(String user, String action, String merchInfo) {
    try {
      userWriter = new PrintWriter(new FileOutputStream(new File(userDir + user + ".log"), true),
          true);
    } catch (FileNotFoundException e) {
      try {
        boolean file = new File(userDir + user + ".log").createNewFile();
        if (file) {
          userWriter = new PrintWriter(
              new FileOutputStream(new File(userDir + user + ".log"), true), true);
        }
      } catch (IOException e1) {
        System.exit(-1);
      }
    }
    String date = new SimpleDateFormat(datePattern).format(new Date(System.currentTimeMillis()));
    writer.write('\n' + date + " " + action + " [" + merchInfo + "] performed by " + user);
    userWriter.write('\n' + date + " " + action + " [" + merchInfo + "]");
    System.out.println(
        new SimpleDateFormat(datePattern).format(new Date(System.currentTimeMillis())) + " "
            + action + " performed by " + user);
    writer.flush();
    userWriter.flush();
  }

  public void logError(String error) {
    errorWriter.write(error);
    errorWriter.flush();
  }

}
