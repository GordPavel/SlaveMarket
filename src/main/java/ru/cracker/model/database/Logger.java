package ru.cracker.model.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class Logger {

    private final String mainLogFile = "logs/main/Slavemarket.log";
    private final String userDir = "logs/users/";
    private final String datePattern = "YYYY-MM-dd HH:mm:ss";
    PrintWriter writer;
    PrintWriter userWriter;

    /**
     * Default constructor
     */
    public Logger() {

        try {
            writer = new PrintWriter(new FileOutputStream(new File(mainLogFile), true), true);
        } catch (FileNotFoundException e) {
            System.out.println("\u001B[31mlogger warning: File not found!\u001B[0m");
        }
    }

    /**
     * @param user   Username who has performed an action
     * @param action action to log performed by user
     */
    public void log(String user, String action, String merchInfo) {
        try {
            userWriter = new PrintWriter(new FileOutputStream(new File(userDir + user + ".log"), true), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String date = new SimpleDateFormat(datePattern).format(new Date(System.currentTimeMillis()));
        writer.write('\n' + date + " " + action + " [" + merchInfo + "] performed by " + user);
        userWriter.write('\n' + date + " " + action + " [" + merchInfo + "]");
        System.out.println(new SimpleDateFormat(datePattern).format(new Date(System.currentTimeMillis())) + " "
                + action + " performed by " + user);
        writer.flush();
        userWriter.flush();
    }

}
