/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package model.postgresqlModel;

import model.postgresqlModel.tables.Deals;
import model.postgresqlModel.tables.merchandises.Aliens;
import model.postgresqlModel.tables.merchandises.Foods;
import model.postgresqlModel.tables.merchandises.Poisons;
import model.postgresqlModel.tables.merchandises.Slaves;

import java.util.List;

public class JsonExportObject {
    List<Users> users;
    List<Deals> deals;
    List<Aliens> aliens;
    List<Foods> foods;
    List<Poisons> poisons;
    List<Slaves> slaves;

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    public List<Deals> getDeals() {
        return deals;
    }

    public void setDeals(List<Deals> deals) {
        this.deals = deals;
    }

    public List<Aliens> getAliens() {
        return aliens;
    }

    public void setAliens(List<Aliens> aliens) {
        this.aliens = aliens;
    }

    public List<Foods> getFoods() {
        return foods;
    }

    public void setFoods(List<Foods> foods) {
        this.foods = foods;
    }

    public List<Poisons> getPoisons() {
        return poisons;
    }

    public void setPoisons(List<Poisons> poisons) {
        this.poisons = poisons;
    }

    public List<Slaves> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<Slaves> slaves) {
        this.slaves = slaves;
    }
}
