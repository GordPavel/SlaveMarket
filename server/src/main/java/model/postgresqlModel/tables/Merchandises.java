package model.postgresqlModel.tables;

import model.merchandises.Merchandise;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Map;


@Entity
@Table(name = "merchandises")
public class Merchandises implements Merchandise, Serializable {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "class")
    private String className;
    @Column(name = "benefit")
    private Float benefit;
    @Column(name = "info")
    private String info;
    @Lob
    @Column(name = "image")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] image;


    @Override
    public String toString() {
        return "Merchandise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", benefit=" + benefit + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    public void setParamsByMap(Map<String, String> map) {
        throw new UnsupportedOperationException();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public float getBenefit() {
        return benefit;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getBase64image() {
        try {
            return new String(Base64.encodeBase64(this.image), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
