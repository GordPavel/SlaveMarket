/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package spring.springControllers;

import exceptions.CreateMerchandiseException;
import exceptions.MerchandiseAlreadyBought;
import exceptions.MerchandiseNotFoundException;
import exceptions.WrongQueryException;
import model.merchandises.Merchandise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.PostgresModel;
import spring.SpringModel;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class PostgresSpringService {

    @Autowired
    private SpringModel model;

    public PostgresSpringService() {

    }

    @Transactional
    public void addMerchandise(Merchandise merch, String user, String token, int price) {
        model.addMerchandise(merch, user, token, price);
    }

    @Transactional
    public void removeMerchandise(Merchandise merch, String user, String token) {
        model.removeMerchandise(merch, user, token);
    }

    @Transactional
    public void removeMerchandise(int id, String user, String token) throws MerchandiseAlreadyBought {
        model.removeMerchandise(id, user, token);
    }

    @Transactional
    public List<String> searchMerchandise(String query) throws WrongQueryException {
        return model.searchMerchandise(query);
    }

    @Transactional
    public String getMerchantById(int id) throws MerchandiseNotFoundException {
        return model.getMerchantById(id);
    }

    @Transactional
    public String buyMerchandise(int id, String user, String token) throws MerchandiseNotFoundException {
        return model.buyMerchandise(id, user, token);
    }

    @Transactional
    public void setValuesToMerchandise(int id, String params, String user, String token) {
        model.setValuesToMerchandise(id, params, user, token);
    }

    @Transactional
    public List<String> getAvailableClasses() {
        return model.getAvailableClasses();
    }

    @Transactional
    public List<String> getMandatoryFields(String className) {
        return model.getMandatoryFields(className);
    }

    @Transactional
    public void addMerchandiseByMap(String className, Map<String, String> kvs, String user, String token, int price) throws CreateMerchandiseException {
        model.addMerchandiseByMap(className, kvs, user, token, price);
    }

    @Transactional
    public String login(String username, String password) {
        return model.login(username, password);
    }

    @Transactional
    public boolean register(String username, String pass) {
        return model.register(username, pass);
    }

    @Transactional
    public void disconnect(String username, String token) {
        model.disconnect(username, token);
    }

    @Transactional
    public List<String> getDealsByUser(String username, String token) {
        return model.getDealsByUser(username, token);
    }

    @Transactional
    public boolean changeLogin(String username, String newLogin, String token) {
        return model.changeLogin(username, newLogin, token);
    }

    @Transactional
    public void changePassword(String username, String newPassword, String token) {
        model.changePassword(username, newPassword, token);
    }

    @Transactional
    public String getDealById(int id) {
        return model.getDealById(id);
    }

    @Transactional
    public boolean exportAllData(String fileName) {
        return model.exportAllData(fileName);
    }

    @Transactional
    public boolean importAllData(String filename) {
        return model.importAllData(filename);
    }

    @Transactional
    public List<String> getNews() {
        return model.getNews();
    }

    @Transactional
    public String newsById(int id) {
        return model.newsById(id);
    }

    @Transactional
    public void addNews(String user, String token, String header, String description, String text, byte[] image, boolean slider) {
        model.addNews(user, token, header, description, text, image, slider);
    }

    @Transactional
    public void setRole(String username, String token, int id, String role) {
        model.setRole(username, token, id, role);
    }

    @Transactional
    public List<String> getAvailableRoles() {
        return model.getAvailableRoles();
    }

    @Transactional
    public List<String> getAllUsers() {
        return model.getAllUsers();
    }

    @Transactional
    public String getUserByToken(String token) {
        return model.getUserByToken(token);
    }

    @Transactional
    public String updateToken(String username, String password) {
        return model.updateToken(username, password);
    }

    @Transactional
    public String getNewsById(int id){
        return model.getNewsById(id);
    }

    public void setModel(PostgresModel model) {
        this.model = model;
    }
}
