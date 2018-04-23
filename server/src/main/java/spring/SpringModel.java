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


    /**
     * Method to get user's deals with offset
     *
     * @param username user's username
     * @param token    user's token
     * @param offset   start with
     * @param limit    deals quantity
     * @return json formatted string
     */
    String getDealsByUser(String username, String token, int offset, int limit);

    /**
     * Method to get json object that contains all required fields with types
     *
     * @param className class to get field from
     * @return json formatted string
     */
    String getMandatoryFieldsWithTypes(String className);

    /**
     * Method to search merchandise by string
     *
     * @param query search query
     * @param limit quantity of return values
     * @return list of json formatted merchandises
     */
    List<String> searchMerchandise(String query, int limit, String order, boolean desc);

    /**
     * get info about group of merchandises
     *
     * @param ids list with merch ids.
     * @return json formatted merchandises.
     */
    List<String> getMerchandisesGroup(List<Integer> ids);

    void buyMerchandises(List<Integer> cart, String username, String token);
}
