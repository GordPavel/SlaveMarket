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
