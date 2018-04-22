/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package model.merchandises;

import java.util.Map;

public abstract class MerchandiseImpl implements Merchandise {
    private int id;
    private String name;
    private float benefit;
    private String allInfo;
    private String className;
    private int price;
    private String image;

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
        return Math.round(benefit);
    }

    public void setBenefit(float benefit) {
        this.benefit = benefit;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAllInfo() {
        return allInfo;
    }

    public void setAllInfo(String allInfo) {
        this.allInfo = allInfo;
    }

    @Override
    public void setParamsByMap(Map<String, String> map) {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
