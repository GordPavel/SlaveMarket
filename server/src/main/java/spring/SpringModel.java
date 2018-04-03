/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package spring;

import model.Model;

import java.util.List;

public interface SpringModel extends Model {

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
     * @param user        user's login who want to add news
     * @param token       user's token
     * @param header      news's header
     * @param description news quick review
     * @param text        new's text
     * @param image       image for news
     * @param slider      true if you want to show it on slider on main page
     */
    void addNews(String user, String token, String header, String description, String text, byte[] image, boolean slider);

    /**
     * Method to set role to user
     *
     * @param username admin's username
     * @param token    admin's current token
     * @param id       user admin want to change
     * @param role     new user's role
     */
    void setRole(String username, String token, int id, String role);

    /**
     * Method to get list of users
     *
     * @return list of json objects
     */
    List<String> getAllUsers();


    /**
     * Method to update user's token.
     *
     * @param username user's username
     * @param password user's password
     * @return new token
     */
    String updateToken(String username, String password);

    /**
     * Method to lookup for currently available permission level roles.
     *
     * @return list of roles
     */
    List<String> getAvailableRoles();

    /**
     * Method to find user with token
     *
     * @param token user's token
     * @return user on json format
     */
    String getUserByToken(String token);

    /**
     * Method to get news by specified id.
     *
     * @param id news id
     * @return news json formatted
     */
    String getNewsById(int id);
}
