package model.database;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final Logger logger = new Logger();

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
            FileManager.logger.logError(
                    "\u001B[31mCan't open/create data file create"
                            + file
                            + "file in project directory\u001B[0m");
        }
        try {
            objectOutputStream.writeObject(o);
        } catch (IOException e) {
            FileManager.logger.logError("\u001B[31mCan't save changes\u001B[0m");
            FileManager.logger.log("DataBase", "\u001B[31mSave failed\u001B[0m", "");
        }
    }

    /**
     * Method to read {@link DealList} object.
     * Be careful, If method can't parse object it's return null
     *
     * @param dealFile string that contains file_name/path
     * @return {@link DealList} object stored in dealFile
     */
    public static DealList readDeals(String dealFile) {
        Object o = FileManager.read(dealFile);
        if (null != o) {
            return (DealList) o;
        } else {
            return new DealList();
        }
    }

    public static List<User> readUsers(String userFile) {
        Object o = FileManager.read(userFile);
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
                new File(datafile).createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            FileManager.logger.logError("can't open data file " + datafile + " because " + e.getMessage());
        }
        return merchants;
    }
}
