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
import static model.merchandises.classes.Slave.mandatoryFields;

@Entity
@Table(name = "slaves")
public class Slaves implements Merchandise {
    @Column(name = "class")
    private final String slavesClass = "slave";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "merchandise_id_super")
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "benefit")
    private float benefit;
    @Column(name = "height")
    private float height;
    @Column(name = "weight")
    private float weight;
    @Column(name = "age")
    private int age;
    @Column(name = "info")
    private String info;
    @Column(name = "gender")
    private String gender;

    /**
     * returns new instance of Slave created by map.
     *
     * @param map map for creating. Where key - field Name, value - field value.
     * @return new Instance of class made by map.
     */
    public static Slaves buildFromMap(HashMap<String, String> map) {
        // This is crap code but it’s 3 a.m. and I need to get this working
        Slaves slave = new Slaves();
        List<String> requiredFields = mandatoryFields();
        for (String s : requiredFields) {
            if (!map.containsKey(s.toUpperCase())) {
                throw new WrongQueryException("Missed key \"" + s + "\"");
            }
        }
        try {
            slave.age = parseAndCheck("AGE", map.get("AGE")).intValue();
            slave.weight = parseAndCheck("WEIGHT", map.get("WEIGHT"));
            slave.height = parseAndCheck("HEIGHT", map.get("HEIGHT"));
            slave.gender = map.get("GENDER");
            slave.name = map.get("NAME");
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return slave;
    }

    public String getSlavesClass() {
        return slavesClass;
    }

    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public float getBenefit() {
        return benefit;
    }

    public float getHeight() {
        return height;
    }

    public float getWeight() {
        return weight;
    }

    public int getAge() {
        return age;
    }

    public String getAllInfo() {
        return info;
    }

    @Override
    public void setParamsByMap(Map<String, String> map) {
        throw new UnsupportedOperationException();
    }
}
