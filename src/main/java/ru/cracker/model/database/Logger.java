package ru.cracker.model.database;

import java.io.*;
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
  private final String mainLogFile = this.mainPath + "Slavemarket.log";
  private final String userDir = "logs/users/";
  private final String systemFile = this.mainPath + "System.log";
  private final String datePattern = "YYYY-MM-dd HH:mm:ss";
  private PrintWriter writer;
  private PrintWriter userWriter;
  private PrintWriter errorWriter;
  private PrintWriter systemWriter;

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
      this.errorWriter = new PrintWriter(
              new FileOutputStream(
                      new File(
                              this.mainPath + "errors.log"),
                      true),
              true);
    } catch (FileNotFoundException e) {
      try {
        boolean file = new File(this.mainLogFile).createNewFile();
        if (file) {
          this.errorWriter = new PrintWriter(
                  new FileOutputStream(new File(this.mainPath + "errors.log"), true), true);
        }
      } catch (IOException ignored) {
        System.exit(-1);
      }
    }
    try {
      this.writer = new PrintWriter(new FileOutputStream(new File(this.mainLogFile), true), true);
    } catch (FileNotFoundException e) {
      try {
        boolean file = new File(this.mainLogFile).createNewFile();
        if (file) {
          this.writer = new PrintWriter(
                  new FileOutputStream(
                          new File(this.mainLogFile),
                          true),
                  true);
        }
      } catch (IOException ignored) {
        System.exit(-1);
      }
    }
    try {
      this.systemWriter = new PrintWriter(new FileOutputStream(new File(this.systemFile), true), true);
    } catch (FileNotFoundException e) {
      try {
        boolean file = new File(this.systemFile).createNewFile();
        if (file) {
          this.writer = new PrintWriter(
                  new FileOutputStream(
                          new File(this.systemFile),
                          true),
                  true);
        }
      } catch (IOException ignored) {
        System.exit(-1);
      }
    }
  }

  /**
   * Method to log user actions.
   *
   * @param user      Username who has performed an action
   * @param action    action to log performed by user
   * @param merchInfo information about changed merchandise.
   */
  public void log(String user, String action, String merchInfo) {
    try {
      this.userWriter = new PrintWriter(
              new FileOutputStream(
                      new File(this.userDir + user + ".log"),
                      true),
              true);
    } catch (FileNotFoundException e) {
      try {
        boolean file = new File(this.userDir + user + ".log").createNewFile();
        if (file) {
          this.userWriter = new PrintWriter(
                  new FileOutputStream(new File(this.userDir + user + ".log"), true), true);
        }
      } catch (IOException e1) {
        System.exit(-1);
      }
    }
    String date = new SimpleDateFormat(this.datePattern)
            .format(new Date(System.currentTimeMillis()));
    this.writer.write('\n' + date + " " + action + " [" + merchInfo + "] performed by " + user);
    this.userWriter.write('\n' + date + " " + action + " [" + merchInfo + "]");
    this.writer.flush();
    this.userWriter.flush();
  }

  /**
   * Method used to log all errors in error file
   * opened in stream {@link #errorWriter}
   *
   * @param error String of error
   */
  public void logError(String error) {
    String date = new SimpleDateFormat(this.datePattern)
            .format(new Date(System.currentTimeMillis()));
    this.errorWriter.write(date + error + '\n');
    this.errorWriter.flush();
  }

  public void systemLog(String message) {
    String date = new SimpleDateFormat(this.datePattern)
            .format(new Date(System.currentTimeMillis()));
    this.systemWriter.write(date + " " + message + '\n');
    this.systemWriter.flush();
  }
}
