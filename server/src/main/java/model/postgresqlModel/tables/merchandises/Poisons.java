/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package model.postgresqlModel.tables.merchandises;

import javax.persistence.*;

@Entity
@Table(name = "poisons")
public class Poisons {
    @Column(name = "class")
    private final String poisonClass = "poison";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "merchandise_id_super")
    private int id;
    private String onset;
    private String frequency;
    private String effect;
    private Float chance;
    private Float weight;
    private String type;
    private String name;
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getOnset() {
        return onset;
    }

    public void setOnset(String onset) {
        this.onset = onset;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public Float getChance() {
        return chance;
    }

    public void setChance(Float chance) {
        this.chance = chance;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Poisons{" +
                "Class='" + poisonClass + '\'' +
                ", id=" + id +

                ", onset='" + onset + '\'' +
                ", frequency='" + frequency + '\'' +
                ", effect='" + effect + '\'' +
                ", chance=" + chance +
                ", weight=" + weight +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
