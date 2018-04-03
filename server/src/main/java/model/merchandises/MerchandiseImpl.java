/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package model.merchandises;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class MerchandiseImpl implements Merchandise {
    private int id;
    private String name;
    private float benefit;
    private String allInfo;
    private byte[] image;

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

    public String getImage() {
        try {
            return new String(Base64.encodeBase64(this.image), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
