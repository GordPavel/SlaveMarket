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
@Table(name = "aliens")
public class Aliens implements Merchandise {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "merchandise_id_super")
    @Column(name = "id")
    private int id;
    @Column(name = "planet")
    private String planet;
    @Column(name = "weight")
    private Float weight;
    @Column(name = "height")
    private Float height;
    @Column(name = "color")
    private String color;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private int age;
    @Column(name = "race")
    private String race;
    @Column(name = "class")
    private String alienClass = "alien";
    @Column(name = "benefit")
    private Float benefit;
    @Column(name = "info")
    private String info;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public float getBenefit() {
        return benefit;
    }

    public String getPlanet() {
        return planet;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getHeight() {
        return height;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAllInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "Aliens{" +
                "id=" + id +
                ", planet='" + planet + '\'' +
                ", weight=" + weight +
                ", height=" + height +
                ", color='" + color + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", race='" + race + '\'' +
                ", alienClass='" + alienClass + '\'' +
                ", benefit=" + benefit +
                '}';
    }

    @Override
    public void setParamsByMap(Map<String, String> map) {
        throw new UnsupportedOperationException();
    }

    public int getAge() {
        return age;
    }

    public String getRace() {
        return race;
    }

    public String getAlienClass() {
        return alienClass;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
