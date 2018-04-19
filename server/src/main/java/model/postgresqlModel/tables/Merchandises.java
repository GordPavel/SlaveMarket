package model.postgresqlModel.tables;

import model.merchandises.Merchandise;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
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
}
