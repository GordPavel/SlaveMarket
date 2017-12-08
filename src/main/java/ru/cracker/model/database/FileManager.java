package ru.cracker.model.database;

import ru.cracker.model.merchandises.Merchandise;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static Logger logger = new Logger();

    public static void writeSerialaizableToFile(Object o, String file) {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
        } catch (IOException e) {
            logger.logError("\u001B[31mCan't open/create data file create data.dat file in project directory\u001B[0m");
        }
        try {
            objectOutputStream.writeObject(o);
        } catch (IOException e) {
            logger.logError("\u001B[31mCan't save changes\u001B[0m");
            logger.log("DataBase", "\u001B[31mSave failed\u001B[0m", "");
        }
    }

    public static List<Merchandise> readMerchandises(String fileName) {
        List<Merchandise> merchants = new ArrayList<>();
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(fileName))) {
            merchants = (List<Merchandise>) stream.readObject();
        } catch (ClassNotFoundException e) {
            try {
                boolean newFile = new File(fileName).createNewFile();
                if (newFile) {
                    merchants.clear();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            logger.logError("can't open data file");
        }
        return merchants;
    }
}
