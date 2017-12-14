package ru.cracker.model.database;

import ru.cracker.model.merchandises.Merchandise;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

  private static Logger logger = new Logger();

  /**
   * Method to write data in specified file.
   *
   * @param o    data to write.
   * @param file file.
   */
  public static void writeSerialaizableToFile(Object o, String file) {
    ObjectOutputStream objectOutputStream = null;
    try {
      objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
    } catch (IOException e) {
      logger.logError(
              "\u001B[31mCan't open/create data file create"
                      + file
                      + "file in project directory\u001B[0m");
    }
    try {
      objectOutputStream.writeObject(o);
    } catch (IOException e) {
      logger.logError("\u001B[31mCan't save changes\u001B[0m");
      logger.log("DataBase", "\u001B[31mSave failed\u001B[0m", "");
    }
  }

  /**
   * Reads stored on file List of merchandises.
   *
   * @param fileName file to read
   * @return List of merchandises
   */
  public static List<Merchandise> readMerchandises(String fileName) {
    Object o = read(fileName);
    if (null != o) {
      return (List<Merchandise>) o;
    } else {
      return new ArrayList<>();
    }
  }

  public static DealList readDeals(String dealFile) {
    Object o = read(dealFile);
    if (null != o) {
      return (DealList) o;
    } else {
      return new DealList();
    }
  }

  public static List<User> readUsers(String userFile) {
    Object o = read(userFile);
    if (null != o) {
      return (List<User>) o;
    } else {
      return new ArrayList<>();
    }
  }

  private static Object read(String datafile) {
    Object merchants = null;
    try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(datafile))) {
      merchants = stream.readObject();
    } catch (ClassNotFoundException e) {
      try {
        boolean newFile = new File(datafile).createNewFile();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    } catch (IOException e) {
      logger.logError("can't open data file" + datafile);
    }
    return merchants;
  }
}
