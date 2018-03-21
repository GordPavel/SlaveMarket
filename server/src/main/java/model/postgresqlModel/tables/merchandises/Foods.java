/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package model.postgresqlModel.tables.merchandises;

import exceptions.WrongQueryException;
import model.merchandises.Merchandise;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static model.merchandises.classes.Alien.parseAndCheck;
import static model.merchandises.classes.Food.mandatoryFields;

@Entity
@Table(name = "food")
public class Foods implements Merchandise {
    @Column(name = "class")
    private final String foodClass = "food";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "merchandise_id_super")
    private int id;
    private String composition;
    private Float energy;
    private Float weight;
    private String name;
    private String info;
    private float benefit;

    public static Foods buildFromMap(HashMap<String, String> map) {
        Foods food = new Foods();
        List<String> requiredFields = mandatoryFields();
        for (String s : requiredFields) {
            if (!map.containsKey(s.toUpperCase())) {
                throw new WrongQueryException("Missed key \"" + s + "\"");
            }
        }
        try {
            food.composition = map.get("COMPOSITION");
            food.name = map.get("NAME");
            food.energy = parseAndCheck("ENERGY VALUE", map.get("ENERGY VALUE"));
            food.weight = parseAndCheck("WEIGHT", map.get("WEIGHT"));
        } catch (Exception e) {
            throw new WrongQueryException(e.getMessage());
        }
        return food;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public float getBenefit() {
        return benefit;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAllInfo() {
        return info;
    }

    @Override
    public void setParamsByMap(Map<String, String> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "Foods{" +
                "Class='" + foodClass + '\'' +
                ", id=" + id +
                ", composition='" + composition + '\'' +
                ", energy=" + energy +
                ", weight=" + weight +
                ", name='" + name + '\'' +
                ", benefit=" + benefit +
                '}';
    }
}
