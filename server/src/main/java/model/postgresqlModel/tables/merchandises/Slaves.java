/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package model.postgresqlModel.tables.merchandises;

import model.merchandises.Merchandise;

import javax.persistence.*;
import java.util.Map;

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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public void setName(String name) {
        this.name = name;
    }

    public float getBenefit() {
        return benefit;
    }

    public void setBenefit(float benefit) {
        this.benefit = benefit;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAllInfo() {
        return info;
    }

    @Override
    public void setParamsByMap(Map<String, String> map) {
        throw new UnsupportedOperationException();
    }
}
