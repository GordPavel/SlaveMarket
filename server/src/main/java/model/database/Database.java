package model.database;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import exceptions.CreateMerchandiseException;
import exceptions.InvalidToken;
import exceptions.MerchandiseNotFoundException;
import model.merchandises.Merchandise;

import java.util.List;
import java.util.Map;

/**
 * Database interface to manage the data adn provide it to view through controllers.
 */
public interface Database {


    /**
     * Puts merch into the vault.
     *
     * @param merch Merch to put in vault
     * @param user  user who performed action
     */
    void addMerchandise(Merchandise merch, String user, String token, int price);

    /**
     * Removes merchandise from database.
     *
     * @param merch Removes merchandise from vault
     * @param user  user who performed action
     */
    void removeMerchandise(Merchandise merch, String user, String token);

    /**
     * remove merchandise from vault by id.
     *
     * @param user user who performed action
     */
    void removeMerchandise(int id, String user, String token);

    /**
     * Method to find specified Merchandises.
     *
     * @param querry querry to filter results
     * @return List of Merchandises specified by query
     */
    List<String> searchMerchandise(String querry);

    /**
     * Returns merchandise by id or exception.
     *
     * @param id id of Merchandise
     * @return Founded merchandise or Exception
     */
    String getMerchantById(int id) throws MerchandiseNotFoundException;

    /**
     * Marks merchandise as bought.
     *
     * @param id   unique merchandise identity
     * @param user user who performed action
     * @return bought merchandise
     * @throws MerchandiseNotFoundException throws if merchandise can not be found
     */
    String buyMerchandise(int id, String user, String token) throws MerchandiseNotFoundException;

    /**
     * Set new values  to merchandise.
     *
     * @param id     id of merchandise to be changed
     * @param user   user who performed action
     * @param params String of parameters with values to change
     */
    void setValuesToMerchandise(int id, String params, String user, String token);

    /**
     * Method for getting available types of merchandises.
     *
     * @return list of available types for creation
     */
    List<String> getAvailableClasses();

    /**
     * Returns minimum of required fields for create an object of chosen class.
     * To see all available tables call {@link #getAvailableClasses()} Method
     *
     * @return field names
     */
    List<String> getMandatoryFields(String className);

    /**
     * Method to add new Merchandise item.
     *
     * @param className merchandise's type
     * @param kvs       map with params.
     *                  To get valid keys call {@link #getMandatoryFields(String className)}
     * @param user      current user
     * @param token     current user's token
     * @param price     merchandise's price
     * @throws CreateMerchandiseException throws if can't create merchandise by map.
     */
    void addMerchandiseByMap(
            String className,
            Map<String, String> kvs,
            String user, String token,
            int price)
            throws CreateMerchandiseException;

    /**
     * Method to connect to shop.
     *
     * @param username username
     * @param password password
     * @return new created token
     */
    String login(String username, String password);

    /**
     * Method to create and add new user.
     *
     * @param username new profile username
     * @param pass     new password
     * @return true if registration complete, false if registration failed.
     */
    boolean register(String username, String pass);

    /**
     * Method to disconnect user and reset token.
     *
     * @param username user's login
     * @param token    user's current token
     * @throws InvalidToken if user token is invalid.
     */
    void disconnect(String username, String token);

    /**
     * Method to see all of user's deals.
     *
     * @param username user, that make deals.
     * @return list of deals strings.
     * @throws InvalidToken if user token is invalid.
     */
    List<String> getDealsByUser(String username, String token);

    /**
     * Method to change user's username.
     *
     * @param username current username
     * @param newLogin new username
     * @param token    current token
     * @return true if login successfully changed
     */
    boolean changeLogin(String username, String newLogin, String token);

    /**
     * Method to change user's password.
     *
     * @param username    user who want to change password.
     * @param newPassword new user's password.
     * @param token       current user's token.
     */
    void changePassword(String username, String newPassword, String token);

    /**
     * Method to get deal specified by id.
     *
     * @param id deal's id.
     * @return deal in json format string
     */
    String getDealById(int id);


    /**
     * Method to export all database into xml file.
     *
     * @param fileName file to export
     * @return true if export successful
     */
    boolean exportAllData(String fileName);

    /**
     * Method to import data from xml file.
     *
     * @param filename file to import
     * @return true if import successful
     */
    boolean importAllData(String filename);

    /**
     * Method to get all news.
     *
     * @return json object of news
     */
    List<String> getNews();

    /**
     * Get news by specified id.
     *
     * @param id news id
     * @return json object of news
     */
    String newsById(int id);

    /**
     * Method to add news in database.
     *
     * @param userId      user who want to add news
     * @param token       user's token
     * @param header      news's header
     * @param description news quick review
     * @param text        new's text
     * @param image       image for news
     * @param slider      true if you want to show it on slider on main page
     */
    void addNews(int userId, String token, @NotNull String header, String description, String text, @Nullable byte[] image, boolean slider);
}